/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

public abstract class AbstractCacheServiceManager implements InternalCacheServiceManager {

    private final Cache<?, ?> cache;

    private volatile CacheConfiguration conf;

    AbstractCacheServiceManager(Cache<?, ?> cache, CacheConfiguration conf) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }
        this.cache = cache;
    }
    /** {@inheritDoc} */
    public boolean isShutdown() {
        return getRunState().isShutdown();
    }
    /** {@inheritDoc} */
    public boolean isStarted() {
        return getRunState().isStarted();
    }
    /** {@inheritDoc} */
    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

    Cache<?, ?> getCache() {
        return cache;
    }

    CacheConfiguration getConf() {
        return conf;
    }

    abstract RunState getRunState();

    void setConf(CacheConfiguration conf) {
        this.conf = conf;
    }

    static enum RunState {
        COULD_NOT_START, NOTRUNNING, STARTING, RUNNING, SHUTDOWN, STOP, TERMINATED, TIDYING;

        public boolean isShutdown() {
            return this != RUNNING && this != NOTRUNNING;
        }

        public boolean isStarted() {
            return this != NOTRUNNING && this != COULD_NOT_START;
        }

        public boolean isTerminated() {
            return this == TERMINATED || this == COULD_NOT_START;
        }

        public boolean isTerminating() {
            return this == SHUTDOWN || this == STOP;
        }
    }

    public void shutdown(Throwable cause) {
        //First thing we must do is set the exception so later invocations
        //of methods on the cache will throw it.
        //after that we might want to try and shutdown the cache
        //perhaps we can have a terminateCache(Throwable cause)
        //what about terminatation of services??
        //lots to think about
        shutdown();
        
        throw new UnsupportedOperationException();
    }
}
