/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;

import java.util.Collection;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoaderCallback;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.AttributeMap;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.junit.Test;

/**
 * Tests that a Cache Loader and the Refresh filter can be managed.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class LoadingCacheLoader extends AbstractLoadingTestBundle {

    @Test
    public void loadingLifecycle() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        MyLoader2 loader = new MyLoader2();
        c = newCache(cc.loading().setLoader(loader).c());

        loader.assertNotStarted();
        loading().load(M1.getKey());// lazy start
        awaitAllLoads();
        loader.shutdownAndAssert(c);
    }


    static class MyLoader2 extends AbstractLifecycleVerifier implements CacheLoader {
        public Object load(Object key, AttributeMap attributes) throws Exception {
            return "1";
        }

        public final void loadAll(Collection loadRequests) {
            for (Object req : loadRequests) {
                ((CacheLoaderCallback) req).completed("1");
            }
        }
    }
}
