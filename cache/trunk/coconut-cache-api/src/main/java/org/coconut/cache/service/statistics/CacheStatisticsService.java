/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.cache.CacheEntry;

/**
 * This is the main interface for controlling the statistics service of a cache at
 * runtime.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheStatisticsService {

    /**
     * Resets the hit ratio.
     * <p>
     * The number of hits returned by individual items {@link CacheEntry#getHits()} are
     * not affected by calls to this method.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not allow resetting the cache statistics (read-only
     *             cache)
     */
    void resetStatistics();

    /**
     * Returns the current <tt>hit statistics</tt> for the cache (optional operation).
     * The returned object is an immutable snapshot that reflects the state of the cache
     * at the calling time.
     * 
     * @return the current hit statistics
     * @throws UnsupportedOperationException
     *             if gathering of statistics is not supported by this cache.
     */
    CacheHitStat getHitStat();
}
