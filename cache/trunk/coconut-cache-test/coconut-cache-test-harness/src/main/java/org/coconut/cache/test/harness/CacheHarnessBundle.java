/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.test.harness;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.statistics.CacheHitStat;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.Clock.DeterministicClock;
import org.coconut.test.CollectionUtils;
import org.junit.Before;

/**
 * This is base class that all test bundle should extend.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class CacheHarnessBundle extends Assert {

    public static final IntegerToStringLoader DEFAULT_LOADER = new IntegerToStringLoader();

    protected Cache<Integer, String> c;

    protected DeterministicClock clock;

    volatile long start;

    @Before
    public void setUp() throws Exception {
        clock = new DeterministicClock();

        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
    }

    protected void assertGet(Map.Entry<Integer, String> e) {
        assertEquals(e.getValue(), c.get(e.getKey()));
    }

    protected void assertGet(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertEquals(entry.getValue(), c.get(entry.getKey()));
        }
    }

    protected void assertGetAll(Map.Entry<Integer, String>... e) {
        Map<Integer, String> all = getAll(e);
        for (Map.Entry<Integer, String> entry : all.entrySet()) {
            assertEquals(entry.getValue(), CollectionUtils.getValue(entry.getKey()));
        }
    }

    protected void assertGetEntry(Map.Entry<Integer, String> e) {
        CacheEntry<Integer, String> ee = c.getEntry(e.getKey());
        assertEquals(ee.getValue(), e.getValue());
        assertEquals(ee.getKey(), e.getKey());
    }

    protected void assertNullGet(Map.Entry<Integer, String> e) {
        assertNullGet(new Map.Entry[] { e });
    }

    protected void assertNullGet(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertNull(get(entry));
        }
    }

    protected void assertNullGet(String msg, Map.Entry<Integer, String> e) {
        assertNull(msg, get(e));
    }

    protected void assertNullPeek(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertNull(peek(entry));
        }
    }

    protected void assertPeek(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertEquals(entry.getValue(), c.peek(entry.getKey()));
        }
    }

    protected void assertPeekEntry(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            CacheEntry<Integer, String> ee = c.peekEntry(entry.getKey());
            assertEquals(ee.getValue(), entry.getValue());
            assertEquals(ee.getKey(), entry.getKey());
        }
    }

    protected void assertSize(int size) {
        assertEquals(size, c.size());
    }

    protected boolean containsKey(Map.Entry<Integer, String> e) {
        return c.containsKey(e.getKey());
    }

    protected boolean containsValue(Map.Entry<Integer, String> e) {
        return c.containsValue(e.getValue());
    }

    protected void evict() {
        c.evict();
    }

    protected String get(Map.Entry<Integer, String> e) {
        return c.get(e.getKey());
    }

    protected Map<Integer, String> getAll(Map.Entry<Integer, String>... e) {
        return c.getAll(CollectionUtils.asMap(e).keySet());
    }

    protected CacheEntry<Integer, String> getEntry(Map.Entry<Integer, String> e) {
        return c.getEntry(e.getKey());
    }

    protected void incTime() {
        clock.incrementTimestamp(1);
    }

    protected void incTime(int amount) {
        clock.incrementTimestamp(amount);
    }

    protected boolean isDone() {
        return (System.nanoTime() - start) > (360l * 1000 * 1000 * 1000);
    }

    protected Cache<Integer, String> newCache(CacheConfiguration<Integer, String> conf) {
        return conf.newInstance(CacheHarnessRunner.tt);
    }

    @SuppressWarnings("unchecked")
    protected CacheConfiguration<Integer, String> newConf() {
        return CacheConfiguration.create();
    }

    protected String peek(Map.Entry<Integer, String> e) {
        return c.peek(e.getKey());
    }

    protected CacheEntry<Integer, String> peekEntry(Map.Entry<Integer, String> e) {
        return c.peekEntry(e.getKey());
    }

    protected void printTime() {
        System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
    }

    protected String put(Map.Entry<Integer, String> e) {
        return c.put(e.getKey(), e.getValue());
    }
//
//    protected String put(Map.Entry<Integer, String> e, long timeout, TimeUnit unit) {
//        return c.put(e.getKey(), e.getValue(), timeout, unit);
//    }
//
//    protected String put(Map.Entry<Integer, String> e, long timeout) {
//        return c.put(e.getKey(), e.getValue(), timeout, TimeUnit.MILLISECONDS);
//    }

    protected void putAll(Map.Entry<Integer, String>... entries) {
        c.putAll(CollectionUtils.asMap(entries));
    }

    protected String remove(Map.Entry<Integer, String> e) {
        return c.remove(e.getKey());
    }

    protected void start() {
        start = System.nanoTime();
    }

    protected void waitAndAssertGet(Map.Entry<Integer, String>... e)
            throws InterruptedException {
        for (Map.Entry<Integer, String> m : e) {
            for (int i = 0; i < 100; i++) {
                if (c.get(m.getKey()).equals(m.getValue())) {
                    break;
                } else {
                    Thread.sleep(15);
                }
                if (i == 99) {
                    throw new AssertionError("Value did not change");
                }
            }
        }
    }

    // protected CacheQuery<Integer, String> keyQuery(Filter<Integer> filter) {
    // return CacheFilters.queryByKey(c, filter);
    // }
//
//    protected void putAll(long timeout, TimeUnit unit,
//            Map.Entry<Integer, String>... entries) {
//        c.putAll(CollectionUtils.asMap(entries), timeout, unit);
//    }

//    protected void putAll(long timeout, Map.Entry<Integer, String>... entries) {
//        putAll(timeout, TimeUnit.NANOSECONDS, entries);
//    }

    final Cache<Integer, String> newCache(int entries) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.setClock(clock);
        Cache<Integer, String> c=cc.newInstance(CacheHarnessRunner.tt);
        c.putAll(createMap(entries));
        return c;

    }

    public static Map<Integer, String> createMap(int entries) {
        if (entries < 0 || entries > 26) {
            throw new IllegalArgumentException();
        }
        Map<Integer, String> map = new HashMap<Integer, String>(entries);
        for (int i = 1; i <= entries; i++) {
            map.put(i, "" + (char) (i + 64));
        }

        return map;
    }

    /**
     * Assert method for hit statistics.
     * 
     * @param ratio
     *            the expected ratio of the cache
     * @param hits
     *            the expected number of hits
     * @param misses
     *            the expected number of misses
     * @param hitstat
     *            the HitStat to compare against
     */
    protected static void assertHitstat(float ratio, long hits, long misses,
            CacheHitStat hitstat) {
        Assert.assertEquals(ratio, hitstat.getHitRatio(), 0.0001);
        Assert.assertEquals(hits, hitstat.getNumberOfHits());
        Assert.assertEquals(misses, hitstat.getNumberOfMisses());
    }
}
