/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.InternalCacheSupport;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

/**
 * 
 * 
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class SynchronizedCacheEvictionService<K, V> extends
        UnsynchronizedCacheEvictionService {
    private final Cache mutex;

    public SynchronizedCacheEvictionService(Cache c, CacheEvictionConfiguration conf,
            InternalCacheSupport<K, V> helper) {
        super(conf, helper);
        this.mutex = c;
    }

    /** {@inheritDoc} */
    @Override
    public long getMaximumVolume() {
        synchronized (mutex) {
            return super.getMaximumVolume();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getMaximumSize() {
        synchronized (mutex) {
            return super.getMaximumSize();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumVolume(long size) {
        synchronized (mutex) {
            super.setMaximumVolume(size);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumSize(int size) {
        synchronized (mutex) {
            super.setMaximumSize(size);
        }
    }

//    /** {@inheritDoc} */
//    @Override
//    public long getPreferableCapacity() {
//        synchronized (mutex) {
//            return super.getPreferableCapacity();
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public int getPreferableSize() {
//        synchronized (mutex) {
//            return super.getPreferableSize();
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void setPreferableCapacity(long size) {
//        synchronized (mutex) {
//            super.setPreferableCapacity(size);
//        }
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void setPreferableSize(int size) {
//        synchronized (mutex) {
//            super.setPreferableSize(size);
//        }
//    }
}
