/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import static org.coconut.cache.internal.service.event.InternalEntryEvent.added;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.evicted;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.expired;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.removed;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.updated;
import static org.coconut.cache.internal.service.event.InternalEvent.cleared;

import java.util.Collection;
import java.util.Map;

import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.servicemanager.CacheServiceManager;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.event.EventBus;
import org.coconut.event.EventSubscription;
import org.coconut.event.defaults.DefaultEventBus;
import org.coconut.filter.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheEventService<K, V> extends AbstractCacheLifecycle implements
        InternalCacheEventService<K, V> {

    private final boolean doAdd;

    private final boolean doClear;

    private final boolean doEvict;

    private final boolean doExpire;

    private final boolean doRemove;

    private final boolean doUpdate;

    private final EventBus<CacheEvent<K, V>> eb = new DefaultEventBus<CacheEvent<K, V>>();

    private final boolean isEnabled;

    private final CacheServiceManager manager;

    private final Offerable<CacheEvent<K, V>> offerable;

    public DefaultCacheEventService(CacheServiceManager manager, CacheEventConfiguration co) {
        super(CacheEventConfiguration.SERVICE_NAME);
        isEnabled = co.isEnabled();
        this.manager = manager;
        this.offerable = eb;
        this.doAdd = co.isIncluded(CacheEntryEvent.ItemAdded.class);
        this.doClear = co.isIncluded(CacheEvent.CacheCleared.class);
        this.doRemove = co.isIncluded(CacheEntryEvent.ItemRemoved.class);
        this.doExpire = co.isIncluded(CacheEntryEvent.ItemRemoved.class);
        this.doEvict = co.isIncluded(CacheEntryEvent.ItemRemoved.class);
        this.doUpdate = co.isIncluded(CacheEntryEvent.ItemUpdated.class);
    }

    public void afterCacheClear(Cache<K, V> cache, long timestamp,
            Collection<? extends CacheEntry<K, V>> entries, long previousVolume) {
        if (entries != null && doRemove) {
            for (CacheEntry<K, V> entry : entries) {
                removed(cache, entry, false);
            }
        }
        if (doClear) {
            dispatch(cleared(cache, entries.size(), previousVolume));
        }
    }

    /** {@inheritDoc} */
    public void afterCacheEvict(Cache<K, V> cache, long started, int size, int previousSize,
            long capacity, long previousCapacity, Collection<? extends CacheEntry<K, V>> evicted,
            Collection<? extends CacheEntry<K, V>> expired) {
        doEvictAll(cache, evicted);
        doExpireAll(cache, expired);
    }

    /** {@inheritDoc} */
    public void afterPut(Cache<K, V> cache, long ignoreStarted,
            Collection<? extends CacheEntry<K, V>> entries, AbstractCacheEntry<K, V> prev,
            AbstractCacheEntry<K, V> newEntry) {
        doEvictAll(cache, entries);

        put(cache, prev, newEntry);

    }

    void put(Cache<K, V> cache, AbstractCacheEntry<K, V> prev, AbstractCacheEntry<K, V> newEntry) {
        if (prev == null) {
            if (doAdd && newEntry != null && newEntry.getPolicyIndex() >= 0) {
                dispatch(added(cache, newEntry, false));
            }
        } else if (prev.getPolicyIndex() >= 0 && newEntry != null
                && newEntry.getPolicyIndex() == -1) {
            if (doRemove) {
                dispatch(removed(cache, prev, false));
            }
        } else if (doUpdate) {
            dispatch(updated(cache, newEntry, prev.getValue(), false, false));
        }
    }

    /** {@inheritDoc} */
    public void afterPutAll(Cache<K, V> cache, long ignoreStarted,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> entries) {
        doEvictAll(cache, evictedEntries);
        for (Map.Entry<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> entry : entries
                .entrySet()) {
            put(cache, entry.getValue(), entry.getKey());
        }
    }

    /** {@inheritDoc} */
    public void afterGet(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, K key, CacheEntry<K, V> prev,
            CacheEntry<K, V> newEntry, boolean isExpired) {
        doEvictAll(cache, evictedEntries);
        if (newEntry == null) {
            if (isExpired && prev != null && doRemove) {
                // removed expiration
                dispatch(removed(cache, prev, true));
            }
        } else {
            if (newEntry != prev && prev != null && doUpdate) {
                dispatch(updated(cache, newEntry, prev.getValue(), true, isExpired));
            }
            if (prev == null && doAdd) {
                dispatch(added(cache, newEntry, true));
            }
        }
    }

    /** {@inheritDoc} */
    public void afterRemove(Cache<K, V> cache, long ignoreStarted, CacheEntry<K, V> entry) {
        if (doRemove && entry != null) {
            dispatch(removed(cache, entry, false));
        }
    }

    /** {@inheritDoc} */
    public void afterReplace(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, CacheEntry<K, V> oldEntry,
            CacheEntry<K, V> newEntry) {
        doEvictAll(cache, evictedEntries);
        processRemoved(cache, newEntry, oldEntry);
    }

    /** {@inheritDoc} */
    public void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume) {
        doEvictAll(cache, evictedEntries);
    }

    /** {@inheritDoc} */
    public Collection<EventSubscription<CacheEvent<K, V>>> getSubscribers() {
        return eb.getSubscribers();
    }

    /** {@inheritDoc} */
    @Override
    public void registerServices(Map<Class<?>, Object> serviceMap) {
        if (isEnabled) {
            serviceMap.put(CacheEventService.class, this);
        }
    }

    /** {@inheritDoc} */
    public boolean isEnabled() {
        return isEnabled;
    }

    /** {@inheritDoc} */
    public boolean offer(CacheEvent<K, V> element) {
        manager.lazyStart(false);
        return eb.offer(element);
    }

    /** {@inheritDoc} */
    public boolean offerAll(Collection<? extends CacheEvent<K, V>> c) {
        manager.lazyStart(false);
        return eb.offerAll(c);
    }

    /** {@inheritDoc} */
    public void process(CacheEvent<K, V> event) {
        manager.lazyStart(false);
        eb.process(event);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler) {
        manager.lazyStart(false);
        return eb.subscribe(eventHandler);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler,
            Predicate<? super CacheEvent<K, V>> filter) {
        manager.lazyStart(false);
        return eb.subscribe(eventHandler, filter);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> listener,
            Predicate<? super CacheEvent<K, V>> filter, String name) {
        manager.lazyStart(false);
        return eb.subscribe(listener, filter, name);
    }

    /** {@inheritDoc} */
    public Collection<EventSubscription<CacheEvent<K, V>>> unsubscribeAll() {
        manager.lazyStart(false);
        return eb.unsubscribeAll();
    }

    private void doEvictAll(Cache<K, V> cache, Iterable<? extends CacheEntry<K, V>> entries) {
        if (entries != null && doEvict) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(evicted(cache, entry));
            }
        }
    }

    private void doExpireAll(Cache<K, V> cache, Iterable<? extends CacheEntry<K, V>> entries) {
        if (entries != null && doExpire) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(expired(cache, entry));
            }
        }
    }

    private void processRemoved(Cache<K, V> cache, CacheEntry<K, V> newEntry, CacheEntry<K, V> prev) {
        if (prev == null) {
            if (newEntry != null && doAdd) {
                dispatch(added(cache, newEntry, false));
            }
        } else if (newEntry == null) {
            if (doRemove) {
                dispatch(removed(cache, prev, false));
            }
        } else if (doUpdate) {
            dispatch(updated(cache, newEntry, prev.getValue(), false, false));
        }
    }

    protected void dispatch(CacheEvent<K, V> event) {
        offerable.offer(event);
    }

    public interface NotificationTransformer {
        Notification notification(Object source);
    }

    public void afterPurge(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> expired) {
        doExpireAll(cache, expired);
    }

    public void dexpired(Cache<K, V> cache, long started, CacheEntry<K, V> entry) {
        dispatch(removed(cache, entry, true));
    }

    public void afterRemoveAll(Cache<K, V> cache, long started, Collection<CacheEntry<K, V>> entries) {
        for (CacheEntry<K, V> entry : entries) {
            afterRemove(cache, started, entry);
        }

    }
}
