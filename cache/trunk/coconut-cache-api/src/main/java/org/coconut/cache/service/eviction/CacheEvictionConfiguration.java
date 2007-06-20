/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.readInt;
import static org.coconut.internal.util.XmlUtil.readLong;
import static org.coconut.internal.util.XmlUtil.writeInt;
import static org.coconut.internal.util.XmlUtil.writeLong;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.ReplacementPolicy;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the eviction service bundle prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEvictionConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    /** The default maximum capacity of a cache unless otherwise specified. */
    public final static long DEFAULT_MAXIMUM_CAPACITY = Long.MAX_VALUE;

    /** The default maximum size of a cache unless otherwise specified. */
    public final static int DEFAULT_MAXIMUM_SIZE = Integer.MAX_VALUE;

    /** The short name of this service. */
    public final static String SERVICE_NAME = "Eviction";

    /** The default settings, used when xml-serializing this configuration */
    private final static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration<Object, Object>();

    private final static String MAXIMUM_CAPACITY = "max-capacity";

    private final static String MAXIMUM_SIZE = "max-size";

    private final static String PREFERABLE_CAPACITY = "preferable-capacity";

    private final static String PREFERABLE_SIZE = "preferable-size";

    private final static String SCHEDULE_EVICT_TAG = "schedule-evict";

    private final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.NANOSECONDS;

    private long maximumCapacity;

    private int maximumSize;

    private long preferableCapacity;

    private int preferableSize;

    /** The replacement policy used for evicting elements. */
    private ReplacementPolicy<?> replacementPolicy;

    private long scheduleEvictionAtFixedRateNanos;

    /**
     * Creates a new CacheEvictionConfiguration with default settings.
     */
    public CacheEvictionConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the maximum allowed capacity of the cache or {@link Long#MAX_VALUE} if
     * there is no limit.
     * 
     * @return the maximum allowed capacity of the cache or Long.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumCapacity(long)
     */
    public long getMaximumCapacity() {
        return maximumCapacity;
    }

    /**
     * Returns the maximum allowed size of the cache or {@link Integer#MAX_VALUE} if there
     * is no limit.
     * 
     * @return the maximum allowed size of the cache or Integer.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumSize(int)
     */
    public int getMaximumSize() {
        return maximumSize;
    }

    /**
     * Returns the configured replacement policy or <tt>null</tt> if none has been
     * configured.
     * 
     * @return the configured replacement policy or <tt>null</tt> if none has been
     *         configured
     * @see #setPolicy(ReplacementPolicy)
     */
    public ReplacementPolicy<?> getPolicy() {
        return replacementPolicy;
    }

    public long getPreferableCapacity() {
        return preferableCapacity;
    }

    public int getPreferableSize() {
        return preferableSize;
    }

    public long getScheduledEvictionAtFixedRate(TimeUnit unit) {
        if (scheduleEvictionAtFixedRateNanos == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Sets that maximum number of elements that a cache can contain. If the limit is
     * reached the cache must evict existing elements before adding new elements. For
     * example, if the limit is 10 elements and the cache currently holds 10 elements.
     * Then, if a user tries to add a new element the cache must choose one of the 10
     * elements to remove from the cache before it inserting the new element.
     * <p>
     * The default value is Integer.MAX_VALUE. Which roughly translates to no limit on the
     * number of elements.
     * 
     * @param maximumCapacity
     *            the maximum capacity.
     */
    public CacheEvictionConfiguration<K, V> setMaximumCapacity(long maximumCapacity) {
        if (maximumCapacity < 0) {
            throw new IllegalArgumentException(
                    "capacity must be a non-negative number, was " + maximumCapacity);
        }
        this.maximumCapacity = maximumCapacity;
        return this;
    }

    /**
     * Sets that maximum number of elements that a cache can hold. If the limit is reached
     * the cache must evict an existing element(s) before adding a new element. For
     * example, if the maximum size is 10 and the cache currently holds 10 elements. Then,
     * if a user tries to add a new element the cache must choose one of the 10 elements
     * to remove from the cache before it inserts the new element. As an alternative the
     * cache might choose to keep the 10 existing elements and not add the new element.
     * For example, if it estimates that the likelihood of requesting anyone of the 10
     * elements in the near future are higher then the likelihood of requsting the new
     * element.
     * <p>
     * To indicate that a cache can hold an unlimited number of elements, specify
     * {@link Integer#MAX_VALUE}. This is also the default value.
     * <p>
     * If the specified maximum size is 0, the cache will never store any elements
     * internally.
     * 
     * @param maximumSize
     *            the maximum number of elements the cache can hold or Integer.MAX_VALUE
     *            if there is no limit
     * @throws IllegalArgumentException
     *             if the specified integer is negative
     */
    public CacheEvictionConfiguration<K, V> setMaximumSize(int maximumSize) {
        if (maximumSize < 0) {
            throw new IllegalArgumentException(
                    "number of maximum elements must be 0 or greater, was " + maximumSize);
        }
        this.maximumSize = maximumSize;
        return this;
    }

    /**
     * Sets the replacement policy that decides which of the currently held elements are
     * evicted in order to make room for new elements. If no replacement policy is
     * specified the cache may choose which elements to evict anyway possible. Note:
     * Whatever way it chooses the elements, it must still obey the configured maximum
     * size and volume.
     * <p>
     * If a cache implementation does not allow the specification of arbitrary
     * replacements policies. But is tightly coupled with a specific implementation of a
     * replacement policy. The cache must throw an exception at construction time, if the
     * users tries to specify any value but <code>null</code>. For example, it is often
     * impossible to implement a highly concurrent cache while at the same time allowing
     * users to specify arbitrary implementations of a replacement policy. This
     * observation is generally refered to as the <code>inheritance anomaly</code>.
     * <p>
     * The cache will, unless otherwise specified, pass instances of
     * {@link org.coconut.cache.CacheEntry} to {@link ReplacementPolicy#add(Object)} and
     * {@link ReplacementPolicy#update(int, Object)}
     * 
     * @param policy
     *            the replacement policy that should select which elements to evict when
     *            the cache is full
     * @return this CacheEvictionConfiguration
     */
    public CacheEvictionConfiguration<K, V> setPolicy(ReplacementPolicy<?> policy) {
        replacementPolicy = policy;
        return this;
    }

    public CacheEvictionConfiguration<K, V> setPreferableCapacity(long capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity must greater then 0, was "
                    + capacity);
        }
        preferableCapacity = capacity;
        return this;
    }

    public CacheEvictionConfiguration<K, V> setPreferableSize(int elements) {
        if (elements < 0) {
            throw new IllegalArgumentException(
                    "number of maximum elements must be 0 or greater, was " + elements);
        }
        preferableSize = elements;
        return this;
    }

    /**
     * 0 = automatic.
     * <p>
     * This requires that the CacheThreadingConfiguration isEnabled.
     * 
     * @param period
     * @param unit
     * @return this configuration
     */
    public CacheEvictionConfiguration<K, V> setScheduledEvictionAtFixedRate(long period,
            TimeUnit unit) {
        if (period < 0) {
            throw new IllegalArgumentException("period must be 0 or greater, was "
                    + period);
        }
        scheduleEvictionAtFixedRateNanos = unit.toNanos(period);
        return this;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Element e) throws Exception {
        maximumCapacity = readLong(getChild(MAXIMUM_CAPACITY, e), maximumCapacity);
        preferableCapacity = readLong(getChild(PREFERABLE_CAPACITY, e),
                preferableCapacity);
        maximumSize = readInt(getChild(MAXIMUM_SIZE, e), maximumSize);
        preferableSize = readInt(getChild(PREFERABLE_SIZE, e), preferableSize);

        /* Eviction time */
        Element evictionTime = getChild(SCHEDULE_EVICT_TAG, e);
        long timee = UnitOfTime.fromElement(evictionTime, DEFAULT_TIME_UNIT,
                CacheExpirationService.NEVER_EXPIRE);
        setScheduledEvictionAtFixedRate(timee, DEFAULT_TIME_UNIT);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) throws Exception {
        writeLong(doc, base, MAXIMUM_CAPACITY, maximumCapacity, DEFAULT
                .getMaximumCapacity());
        writeLong(doc, base, PREFERABLE_CAPACITY, preferableCapacity, DEFAULT
                .getPreferableCapacity());
        writeInt(doc, base, MAXIMUM_SIZE, maximumSize, DEFAULT.getMaximumSize());
        writeInt(doc, base, PREFERABLE_SIZE, preferableSize, DEFAULT.getPreferableSize());

        /* Expiration Timer */
        UnitOfTime.toElementCompact(doc, base, SCHEDULE_EVICT_TAG,
                scheduleEvictionAtFixedRateNanos, DEFAULT_TIME_UNIT,
                DEFAULT.scheduleEvictionAtFixedRateNanos);

    }
}
