/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.event.InternalCacheEventService;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.loading.InternalCacheLoadingService;
import org.coconut.cache.internal.service.service.InternalCacheServiceManager;
import org.coconut.cache.internal.service.service.UnsynchronizedCacheServiceManager;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps.DefaultAttributeMap;
import org.coconut.filter.Filter;
import org.coconut.internal.util.CollectionUtils;

/**
 * An unsynchronized cache implementation.
 * <p>
 * If multiple threads access this cache concurrently, and at least one of the threads
 * modifies the cache structurally, it <i>must</i> be synchronized externally. (A
 * structural modification is any operation that adds, deletes or changes one or more
 * mappings.) This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
/**
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheEvictionService.class,
        CacheExpirationService.class, CacheLoadingService.class,
        CacheManagementService.class, CacheServiceManagerService.class,
        CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends AbstractCache<K, V> implements
        ConcurrentMap<K, V> {
    public final InternalCacheLoadingService<K, V> loadingService;

    private final AbstractCacheEntryFactoryService<K, V> entryFactory;

    private final InternalCacheEventService<K, V> eventService;

    private final InternalCacheEvictionService<K, V, AbstractCacheEntry<K, V>> evictionService;

    private final DefaultCacheExpirationService<K, V> expiration;

    private final EntryMap<K, V> map = new EntryMap<K, V>(false);

    private final InternalCacheServiceManager serviceManager;

    private final DefaultCacheStatisticsService<K, V> statistics;

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    @SuppressWarnings("unchecked")
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        serviceManager = new UnsynchronizedCacheServiceManager(this, new MyHelper(), conf);
        Defaults.initializeUnsynchronizedCache(serviceManager);
        expiration = serviceManager.getService(DefaultCacheExpirationService.class);
        loadingService = serviceManager.getService(InternalCacheLoadingService.class);
        evictionService = serviceManager.getService(InternalCacheEvictionService.class);
        eventService = serviceManager.getService(InternalCacheEventService.class);
        statistics = serviceManager.getService(DefaultCacheStatisticsService.class);
        entryFactory = serviceManager.getService(AbstractCacheEntryFactoryService.class);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        checkStarted();
        long started = statistics.beforeCacheClear(this);
        int size = map.size();
        long capacity = map.capacity();
        Collection<? extends AbstractCacheEntry<K, V>> list = null;
        if (size != 0) {
            if (statistics.needElementsAfterClear()) {
                list = new ArrayList<AbstractCacheEntry<K, V>>(map.getAll());
            }
            evictionService.clear();
            map.clear();
        }
        statistics.afterCacheClear(this, started, size, capacity, list);
        eventService.afterCacheClear(this, started, size, capacity, list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySetPublic(this);
    }

    public void evict() {
        checkStarted();
        long started = statistics.beforeCacheEvict(this);
        int previousSize = map.size();
        long previousCapacity = map.capacity();
        List<AbstractCacheEntry<K, V>> expired = new ArrayList<AbstractCacheEntry<K, V>>();
        if (expiration != null) {
            for (Iterator<AbstractCacheEntry<K, V>> iterator = map.iterator(); iterator
                    .hasNext();) {
                AbstractCacheEntry<K, V> m = iterator.next();
                loadingService.reloadIfNeeded(m);
                if (expiration.isExpired(m)) {
                    expired.add(m);
                    evictionService.remove(m.getPolicyIndex());
                    iterator.remove();
                }
            }
        }
        List<AbstractCacheEntry<K, V>> evicted = evictionService.evict(map.size(), map
                .capacity());
        for (AbstractCacheEntry<K, V> e : evicted) {
            map.remove(e.getKey());
        }
        statistics.afterCacheEvict(this, started, map.size(), previousSize, map
                .capacity(), previousCapacity, evicted, expired);
        eventService.afterCacheEvict(this, started, map.size(), previousSize, map
                .capacity(), previousCapacity, evicted, expired);
    }

    public Map<Class<?>, Object> getAllServices() {
        checkStarted();
        return serviceManager.getAllPublicServices();
    }

    /**
     * {@inheritDoc}
     */
    public long getCapacity() {
        return map.capacity();
    }

    public final <T> T getService(Class<T> serviceType) {
        checkStarted();
        return serviceManager.getPublicService(serviceType);
    }

    /**
     * @see org.coconut.cache.Cache#hasService(java.lang.Class)
     */
    public boolean hasService(Class<?> serviceType) {
        checkStarted();
        return serviceManager.hasPublicService(serviceType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<K> keySet() {
        return map.keySet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<V> values() {
        return map.values(this);
    }

    private void checkStarted() {
        serviceManager.lazyStart(false);
    }

    private AbstractCacheEntry<K, V> doPut(AbstractCacheEntry<K, V> entry) {
        if (entry.getPolicyIndex() == -1) { // entry is newly added
            entry.setPolicyIndex(evictionService.add(entry));
            if (entry.getPolicyIndex() < 0) {
                return null;
            }
        } else if (!evictionService.replace(entry.getPolicyIndex(), entry)) {
            map.remove(entry.getKey());
            return null;
        }
        return map.put(entry);
    }

    private List<CacheEntry<K, V>> trim() {
        List<CacheEntry<K, V>> list = null;
        while (evictionService.isSizeBreached(map.size())) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            list.add(e);
        }
        while (evictionService.isCapacityBreached(map.capacity())) {
            AbstractCacheEntry<K, V> e = evictionService.evictNext();
            map.remove(e.getKey());
            if (list == null) {
                list = new ArrayList<CacheEntry<K, V>>(2);
            }
            list.add(e);
        }
        return list;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doGet(java.lang.Object)
     */
    AbstractCacheEntry<K, V> doGet(K key) {
        checkStarted();
        boolean isHit = false;
        boolean isExpired = false;
        long started = statistics.beforeGet(this, key);

        AbstractCacheEntry<K, V> prev = map.get(key);
        AbstractCacheEntry<K, V> newE = null;
        List<? extends CacheEntry<K, V>> trimmed = null;
        if (prev == null) { // Cache Miss
            AttributeMap attributes = new DefaultAttributeMap();
            V newValue = loadingService.loadBlocking(key, attributes);
            if (newValue != null) {
                newE = entryFactory.createEntry(key, newValue, attributes, prev);
                doPut(newE);
                if (newE.getPolicyIndex() >= 0) {
                    newE.accessed();
                    trimmed = trim();
                }
            }
        } else if (expiration.isExpired(prev)) {
            isExpired = true;
            AttributeMap attributes = new DefaultAttributeMap();
            V newValue = loadingService.loadBlocking(key, attributes);
            // TODO what about lazy.., when does it expire??
            if (newValue == null) {
                map.remove(key);
                evictionService.remove(prev.getPolicyIndex());
            } else {
                newE = entryFactory.createEntry(key, newValue, attributes, prev);
                doPut(newE);
                if (newE.getPolicyIndex() >= 0) {
                    newE.accessed();
                    trimmed = trim();
                }
            }
        } else {
            isHit = true;
            // TODO check if expired...
            loadingService.reloadIfNeeded(prev);
           // prev.incrementHits();
            prev.accessed();
            evictionService.touch(prev.getPolicyIndex());
        }
        final AbstractCacheEntry<K, V> returnMe;
        if (isHit) {
            returnMe = prev;
        } else {
            returnMe = newE != null && newE.getPolicyIndex() >= 0 ? newE : null;
        }
        statistics.afterGet(this, started, trimmed, key, prev, returnMe, isExpired);
        eventService.afterGet(this, started, trimmed, key, prev, returnMe, isExpired);
        return returnMe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Map<K, V> doGetAll(Collection<? extends K> keys) {
        HashMap<K, V> result = new HashMap<K, V>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AbstractCacheEntry<K, V> doPeek(K key) {
        return map.get(key);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    CacheEntry<K, V> doPut(K key, V newValue, boolean putOnlyIfAbsent,
            AttributeMap attributes) {
        checkStarted();
        long started = statistics.beforePut(this, key, newValue);
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (putOnlyIfAbsent && prev != null) {
            statistics.afterPut(this, started, null, prev, null);
            eventService.afterPut(this, started, null, prev, null);
            return prev;
        }
        AbstractCacheEntry<K, V> e = entryFactory.createEntry(key, newValue, attributes,
                prev);
        doPut(e);
        statistics.afterPut(this, started, Collections.EMPTY_LIST, prev, e
                .getPolicyIndex() >= 0 ? e : null);
        eventService.afterPut(this, started, trim(), e.getPolicyIndex() >= 0 ? e : null,
                prev);
        return prev;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void doPutAll(Map<? extends K, ? extends V> t, AttributeMap attributes) {
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
            doPut(e.getKey(), e.getValue(), false, attributes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        checkStarted();
        long started = statistics.beforeRemove(this, key);
        AbstractCacheEntry<K, V> e = map.remove(key, value);
        if (e != null) {
            evictionService.remove(e.getPolicyIndex());
        }
        statistics.afterRemove(this, started, e);
        eventService.afterRemove(this, started, e);
        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue, AttributeMap attributes) {
        checkStarted();
        long started = statistics.beforePut(this, key, newValue);
        AbstractCacheEntry<K, V> prev = map.get(key);
        if (oldValue == null) {
            if (prev == null) {
                statistics.afterPut(this, started, null, prev, null);
                eventService.afterPut(this, started, null, prev, null);
                return null;
            }
        } else {
            if (prev == null || !oldValue.equals(prev.getValue())) {
                statistics.afterPut(this, started, null, prev, null);
                eventService.afterPut(this, started, null, prev, null);
                return null;
            }
        }

        AbstractCacheEntry<K, V> e = entryFactory.createEntry(key, newValue, attributes,
                prev);
        doPut(e);
        statistics.afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e
                : null);
        eventService.afterPut(this, started, trim(), prev, e.getPolicyIndex() >= 0 ? e
                : null);
        return prev;
    }

    /**
     * {@inheritDoc}
     */
    void doTrimToSize(int newSize) {
        if (newSize < 0) {
            throw new IllegalArgumentException(
                    "newSize cannot be a negative number, was " + newSize);
        }
        checkStarted();
        long started = statistics.beforeTrimToSize(this);
        int numberToTrim = Math.max(0, map.size() - newSize);
        List<AbstractCacheEntry<K, V>> l = evictionService.evict(numberToTrim);
        for (AbstractCacheEntry<K, V> entry : l) {
            map.remove(entry.getKey());
        }
        // this happens if we use a null evictionService
        numberToTrim = map.size() - newSize;
        if (numberToTrim > 0) {
            Iterator<AbstractCacheEntry<K, V>> i = map.iterator();
            while (numberToTrim-- > 0 && i.hasNext()) {
                AbstractCacheEntry<K, V> e = i.next();
                l.add(e);
                evictionService.remove(e.getPolicyIndex());
                i.remove();
            }
        }
        statistics.afterTrimToSize(this, started, l);
        eventService.afterTrimToSize(this, started, l);
    }

    /**
     *  A helper class.
     */
    class MyHelper implements CacheHelper<K, V> {


        /**
         * {@inheritDoc}
         */
        public int removeAll(Collection<? extends K> collection) {
            if (collection == null) {
                throw new NullPointerException("collection is null");
            }
            CollectionUtils.checkCollectionForNulls(collection);
            checkStarted();
            int count = 0;
            for (K key : collection) {
                long started = statistics.beforeRemove(UnsynchronizedCache.this, key);
                AbstractCacheEntry<K, V> e = map.remove(key, null);
                if (e != null) {
                    evictionService.remove(e.getPolicyIndex());
                    count++;
                }
                statistics.afterRemove(UnsynchronizedCache.this, started, e);
                eventService.afterRemove(UnsynchronizedCache.this, started, e);
            }
            return count;
        }

        /**
         * {@inheritDoc}
         */
        public int removeAllFiltered(Filter<? super CacheEntry<K, V>> filter) {
            if (filter == null) {
                throw new NullPointerException("filter is null");
            }
            checkStarted();
            return removeAll(filterKeys(filter));
        }

        /**
         * {@inheritDoc}
         */
        public Collection<? extends CacheEntry<K, V>> filter(
                Filter<? super CacheEntry<K, V>> filter) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Collection<? extends K> filterKeys(Filter<? super CacheEntry<K, V>> filter) {
            checkStarted();
            ArrayList<K> l=new ArrayList<K>();
            for (CacheEntry<K, V> ce : map) {
                if (filter.accept(ce)) {
                    l.add(ce.getKey());
                }
            }
            return l;
        }

        /**
         * {@inheritDoc}
         */
        public Object getMutex() {
            throw new UnsupportedOperationException("synchronization not available");
        }

        /**
         * {@inheritDoc}
         */
        public boolean isValid(K key) {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public V put(K key, V value, AttributeMap attributes) {
            CacheEntry<K, V> prev = doPut(key, value, false, attributes);
            return prev == null ? null : prev.getValue();
        }

        /**
         * {@inheritDoc}
         */
        public void putAll(Map<? extends K, ? extends V> keyValues,
                Map<? extends K, AttributeMap> attributes) {
            for (Map.Entry<? extends K, ? extends V> e : keyValues.entrySet()) {
                put(e.getKey(), e.getValue(), attributes.get(e.getKey()));
            }
            // TODO Auto-generated method stub

        }

        /**
         * {@inheritDoc}
         */
        public void trimToCapacity(long capacity) {}

        /**
         * {@inheritDoc}
         */
        public void trimToSize(int size) {
            doTrimToSize(size);
        }

        /**
         * {@inheritDoc}
         */
        public void valueLoaded(K key, V value, AttributeMap attributes) {
            if (value != null) {
                doPut(key, value, false, attributes);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void valuesLoaded(Map<? extends K, ? extends V> values,
                Map<? extends K, AttributeMap> keys) {
            for (Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
                doPut(entry.getKey(), entry.getValue(), false, keys.get(entry.getKey()));
            }
        }
    }
}
