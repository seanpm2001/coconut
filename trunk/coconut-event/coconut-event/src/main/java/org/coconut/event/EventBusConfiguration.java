/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.core.Log;
import org.coconut.event.spi.EventBusErrorHandler;
import org.coconut.filter.matcher.FilterMatcher;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusConfiguration<E> {

    /**
     * The default configuration that can be used by any event bus that is not
     * provided with a eventbus configuration object.
     */
    public static final EventBusConfiguration DEFAULT_CONFIGURATION = null;

    public CacheConfiguration conf;

    private FilterMatcher<?, E> fm;

    private Log log;

    private Executor e;

    private boolean reentrant = false;

    private EventBusErrorHandler<E> errorHandler;

    public void setAsync(Executor e) {
        this.e = e;
    }

    public void setCheckReentrant(boolean check) {
        this.reentrant = check;
    }

    public boolean getCheckReentrant() {
        return reentrant;
    }
    /**
     * Returns the log configured for the cache.
     * 
     * @return the log configured for the cache, or <tt>null</tt> if no log is
     *         configured
     * @see #setLog(Log)
     */
    public EventBusErrorHandler<E> getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * Sets the log that the cache should use for logging anomalies and errors.
     * If no log is set the cache will redirect all output to dev/null
     * 
     * @param log
     *            the log to use
     * @return this configuration
     * @see org.coconut.core.Logs
     * @see org.coconut.core.Log
     * @throws NullPointerException
     *             if log is <tt>null</tt>
     */
    public EventBusConfiguration<E> setErrorHandler(EventBusErrorHandler<E> errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException("errorHandler is null");
        }
        this.errorHandler = errorHandler;
        return this;
    }
}
