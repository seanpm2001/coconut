package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.test.CollectionUtils;
import org.junit.Test;

public class Remove extends AbstractCacheTCKTest {

    @Test
    public void remove() {
        c = newCache(0);
        assertNull(c.remove(MNAN2.getKey()));
        assertNull(c.remove(MNAN1.getKey()));

        c = newCache(5);
        assertEquals(M1.getValue(), c.remove(M1.getKey()));
        assertEquals(4, c.size());
        assertFalse(c.containsKey(M1.getKey()));

        c = newCache(1);
        assertEquals(M1.getValue(), c.remove(M1.getKey()));
        assertTrue(c.isEmpty());
    }

    @Test
    public void remove2() {
        c = newCache(2);
        assertTrue(c.remove(M2.getKey(), M2.getValue()));
        assertEquals(1, c.size());
        assertFalse(c.remove(M1.getKey(), M2.getValue()));
        assertEquals(1, c.size());
    }

    /**
     * remove(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void remove2KeyNPE() {
        c = newCache(1);
        c.remove(null, M1.getValue());
    }
    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void remove2LazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.remove(M1.getKey(), M2.getValue());
        checkLazystart();
    }
    
    
    
    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void remove2ShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.remove(M1.getKey(), M2.getValue());
    }

    /**
     * remove(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void remove2ValueNPE() {
        c = newCache();
        c.remove(M1.getKey(), null);
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.remove(M1.getKey());
        checkLazystart();
    }

    @Test(expected = NullPointerException.class)
    public void removeNPE() {
        newCache(0).remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeNPE1() {
        newCache(5).remove(null);
    }
    
    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void removeShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.remove(MNAN1.getKey());
    }
}
