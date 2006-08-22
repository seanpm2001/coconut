/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.loading;

import junit.framework.AssertionFailedError;

import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.util.AbstractCacheLoader;

public class ConcurrentLoading extends CacheTestBundle{

    // /**
    // * cancel(true) interrupts a running task
    // */
    // public void testCancelInterrupt() {
    // final Cache c = newCache(CacheConfiguration.newConf().setLoader(
    // new SleepLoader()));
    //
    // Exchanger<Future> e=new Exchanger<Future>();
    //            
    //        
    //
    // new Future(new Callable() {
    // public Object call() {
    // try {
    // Thread.sleep(MEDIUM_DELAY_MS);
    // threadShouldThrow();
    // } catch (InterruptedException success) {
    // }
    // return Boolean.TRUE;
    // }
    // });
    // Thread t = new Thread(new Runnable() {
    // public void run() {
    // c.load(1000);
    // }
    // });
    // t.start();
    //
    // try {
    // Thread.sleep(SHORT_DELAY_MS);
    // assertTrue(task.cancel(true));
    // t.join();
    // assertTrue(task.isDone());
    // assertTrue(task.isCancelled());
    // } catch (InterruptedException e) {
    // unexpectedException();
    // }
    // }

    class SleepLoader extends AbstractCacheLoader<Integer, String> {

        /**
         * @see org.coconut.cache.util.AbstractCacheLoader#load(java.lang.Object)
         */
        public String load(Integer key) {
            try {
                Thread.sleep(key.intValue());
                throw new AssertionFailedError();
            } catch (InterruptedException success) {
            }
            return "";
        }

    }
}
