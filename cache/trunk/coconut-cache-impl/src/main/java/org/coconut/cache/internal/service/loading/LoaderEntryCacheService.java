/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoaderEntryCacheService<K, V>  {

//    private final ConcurrentHashMap<K, Future<V>> loads = new ConcurrentHashMap<K, Future<V>>();
//
//    //cannot have cancelAll(false) followed by cancelAll(true)
//    void cancelAll(boolean interrupt) {
//        for (Iterator<Map.Entry<K, Future<V>>> iter = loads.entrySet().iterator(); iter
//                .hasNext();) {
//            Map.Entry<K, Future<V>> element = iter.next();
//            element.getValue().cancel(interrupt);
//            iter.remove();
//        }
//    }
//    
    //void load()
}
