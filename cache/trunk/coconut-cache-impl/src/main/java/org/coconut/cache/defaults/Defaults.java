/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.UnsynchronizedCacheEvictionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.loading.DefaultCacheLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.servicemanager.CacheServiceManager;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.service.worker.UnsynchronizedCacheWorkerService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class Defaults {

    /** Cannot instantiate. */
    private Defaults() {}

    @SuppressWarnings("unchecked")
    static <K, V> void initializeUnsynchronizedCache(CacheConfiguration<K, V> conf,
            CacheServiceManager serviceManager) {
        serviceManager.registerServices(DefaultCacheStatisticsService.class);
        serviceManager.registerServices(UnsynchronizedCacheEvictionService.class);
        serviceManager.registerServices(DefaultCacheExpirationService.class);
        serviceManager.registerServices(DefaultCacheLoaderService.class);
        serviceManager.registerServices(DefaultCacheManagementService.class);
        serviceManager.registerServices(DefaultCacheEventService.class);
        serviceManager.registerServices(UnsynchronizedCacheWorkerService.class);
    }
}
