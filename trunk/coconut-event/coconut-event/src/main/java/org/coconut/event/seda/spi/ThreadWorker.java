/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.spi;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ThreadWorker<T> extends Runnable {

    /**
     * Returns the thread executing within this worker.
     */
    Thread getThread();

    /**
     * Returns <code>true</code> if this worker is currently performing any
     * kind of work.
     */
    boolean isActive();

    /**
     * Interrupt thread if not running a task.
     */
    void interruptNow();

    /**
     * Cause worker to die even if running a task.
     */
    void interruptIfIdle();

    /**
     * Attaches the given object to this thread worker.
     * <p>
     * An attached object may later be retrieved via the {@link #getAttachment
     * getAttachment} method. Only one object may be attached at a time;
     * invoking this method causes any previous attachment to be discarded. The
     * current attachment may be discarded by attaching <tt>null</tt>.
     * </p>
     * 
     * @param ob
     *            The object to be attached; may be <tt>null</tt>
     * @return The previously-attached object, if any, otherwise <tt>null</tt>
     */
    T setAttachment(T ob);

    /**
     * Retrieves the current attachment.
     * </p>
     * 
     * @return The object currently attached to this thread worker, or
     *         <tt>null</tt> if there is no attachment
     */
    T getAttachment();
    
    //Stage getCurrentStage()???;
}
