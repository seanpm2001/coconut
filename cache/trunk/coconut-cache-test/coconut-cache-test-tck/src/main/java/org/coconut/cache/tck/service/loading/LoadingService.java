/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.test.MockTestCase;
import org.junit.Test;

public class LoadingService extends AbstractCacheTCKTestBundle {

    @Test
    public void testNonConfigured() {
        c = newCache();
        assertFalse(c.hasService(CacheLoadingService.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonConfiguredIAE() {
        c = newCache();
        c.getService(CacheLoadingService.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConfigured() {
        c = newCache(newConf().loading().setLoader(
                MockTestCase.mockDummy(CacheLoader.class)));
        assertTrue(c.hasService(CacheLoadingService.class));
        // check that it doesn't fail with a classcast exception
        c.getService(CacheLoadingService.class);
    }
}
