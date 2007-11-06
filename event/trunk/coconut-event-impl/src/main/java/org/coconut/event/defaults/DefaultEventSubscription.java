/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.defaults;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class DefaultEventSubscription<E> extends ReentrantReadWriteLock implements
        EventSubscription<E> {
    private final DefaultEventBus<E> bus;

    private final EventProcessor<? super E> destination;

    private final Predicate<? super E> filter;

    private final String name;

    private volatile boolean isActive = true;

    /**
     * @param destination
     * @param filter
     */
    DefaultEventSubscription(DefaultEventBus<E> bus, final String name,
            final EventProcessor<? super E> destination, final Predicate<? super E> filter) {
        this.bus = bus;
        this.name = name;
        this.destination = destination;
        this.filter = filter;
    }

    /**
     * @see org.coconut.event.EventSubscription#unsubscribe()
     */
    public void unsubscribe() {
        bus.cancel(this);
    }

    /**
     * @see org.coconut.event.EventSubscription#getEventProcessor()
     */
    public EventProcessor<? super E> getEventProcessor() {
        return destination;
    }

    /**
     * @see org.coconut.event.EventSubscription#getFilter()
     */
    public Predicate<? super E> getFilter() {
        return filter;
    }

    /**
     * @see org.coconut.event.EventSubscription#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.coconut.event.EventSubscription#isValid()
     */
    public boolean isValid() {
        return isActive;
    }

    void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
