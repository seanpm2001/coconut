/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event;

import java.util.concurrent.locks.Lock;

import org.coconut.core.Named;

/**
 * A stage is self-contained component that handle events, optionally
 * dispatching events to other stages while processing these. Most
 * implementations will use an incoming queue to store events that have not yet
 * been processed. A StageManager takes care of thread scheduling
 * <p>
 * Each stage is registered with a unique name within a stage manager. Most
 * implementations do not allow stages to be renamed.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Stage.java 26 2006-07-14 11:42:29Z kasper $
 */
public interface Stage extends Named /* extends ReadWriteLock */{
    /**
     * copied here to provide a description at some later stage.
     * 
     * @see java.util.concurrent.locks.ReadWriteLock#readLock()
     */
    Lock readLock();

    /**
     * copied here to provide a description at some later stage.
     * 
     * @see java.util.concurrent.locks.ReadWriteLock#writeLock()
     */
    Lock writeLock();
}
