/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.internal.picocontainer.PicoContainer;
import org.coconut.management.ManagedLifecycle;

/**
 * Various utility classes for {@link CacheServiceManagerService} implementations.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class ServiceManagerUtil {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ServiceManagerUtil() {}

    // /CLOVER:ON

    static List<ManagedLifecycle> initializeManagedObjects(PicoContainer container) {
        List<ManagedLifecycle> managedObjects = new ArrayList<ManagedLifecycle>();
        List<AbstractCacheLifecycle> l = container
                .getComponentInstancesOfType(AbstractCacheLifecycle.class);

        for (AbstractCacheLifecycle a : l) {
            if (a instanceof CompositeService) {
                for (Object o : ((CompositeService) a).getChildServices()) {
                    if (o instanceof ManagedLifecycle) {
                        managedObjects.add((ManagedLifecycle) o);
                    }
                }
            }
            if (a instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) a);
            }
        }
        CacheConfiguration conf = (CacheConfiguration) container
                .getComponentInstance(CacheConfiguration.class);
        for (Object service : conf.serviceManager().getObjects()) {
            if (service instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) service);
            }
        }
        return managedObjects;
    }

    /**
     * Wraps a CacheServiceManagerService implementation such that only methods from the
     * CacheServiceManagerService interface is exposed.
     * 
     * @param service
     *            the CacheServiceManagerService to wrap
     * @return a wrapped service that only exposes CacheServiceManagerService methods
     */
    public static CacheServiceManagerService wrapService(CacheServiceManagerService service) {
        return new DelegatedCacheServiceManagerService(service);
    }

    /**
     * A wrapper class that exposes only the CacheServiceManagerService methods of a
     * CacheServiceManagerService implementation.
     */
    public static final class DelegatedCacheServiceManagerService implements
            CacheServiceManagerService {

        /** The CacheServiceManagerService that is wrapped. */
        private final CacheServiceManagerService delegate;

        /**
         * Creates a wrapped CacheServiceManagerService from the specified implementation.
         * 
         * @param service
         *            the CacheServiceManagerService to wrap
         */
        public DelegatedCacheServiceManagerService(CacheServiceManagerService service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.delegate = service;
        }

        /** {@inheritDoc} */
        public Map<Class<?>, Object> getAllServices() {
            return delegate.getAllServices();
        }

        /** {@inheritDoc} */
        public boolean hasService(Class<?> serviceType) {
            return delegate.hasService(serviceType);
        }

        /** {@inheritDoc} */
        public void shutdownServiceAsynchronously(Runnable service) {
            delegate.shutdownServiceAsynchronously(service);
        }

        /** {@inheritDoc} */
        public <T> T getService(Class<T> serviceType) {
            return delegate.getService(serviceType);
        }
    }
}
