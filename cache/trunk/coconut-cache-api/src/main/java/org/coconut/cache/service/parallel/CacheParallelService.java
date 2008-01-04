/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.parallel;

public interface CacheParallelService<K, V> {
    ParallelCache<K, V> get();
}
