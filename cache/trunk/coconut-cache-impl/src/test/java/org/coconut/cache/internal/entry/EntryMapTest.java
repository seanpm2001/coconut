/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.internal.entry;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.EntryMap;
import org.coconut.cache.internal.service.entry.UnsynchronizedCacheEntry;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheEntryMapTest.java 186 2007-01-24 12:30:38Z kasper $
 */
public class EntryMapTest {

    @Test
    public void testNoting() {
        // CacheEntryMapStub s = new CacheEntryMapStub();
        // Random r = new Random();
        // ArrayList<Integer> al = new ArrayList<Integer>();
        // for (int i = 0; i < 5; i++) {
        // int ia = r.nextInt();
        // al.add(ia);
        // s.put(new EntryStub(ia, "foo" + ia));
        // }
        // ArrayList<Integer> removeOrder = new ArrayList<Integer>();
        //
        // Collections.shuffle(al);
        // while (al.size() > 0) {
        // int take = al.remove(al.size() - 1);
        // Map.Entry o = s.remove(take);
        // assertEquals("foo" + take, o.getValue());
        // }
    }

    @Test
    public void testNotsa() {
        EntryMap<Integer, String> s = new EntryMap<Integer, String>(false);
        s.put(new EntryStub(-348653132, "a"));
        s.put(new EntryStub(772636595, "b"));
        AbstractCacheEntry<?,?> ac1 = s.remove(-348653132);
        AbstractCacheEntry<?,?> ac2 = s.remove(772636595);
        assertNotNull(ac1);
        assertNotNull(ac2);
    }

    public void testNotsd() {
        // HashMap<K, V>
        while (testNots())
            ;
    }

    public boolean testNots() {
        EntryMap<Integer, String> s = new EntryMap<Integer, String>(false);
        Random r = new Random();
        ArrayList<Integer> al = new ArrayList<Integer>();
        for (int i = 0; i < 30; i++) {
            int ia = r.nextInt();
            al.add(ia);
            s.put(new EntryStub(ia, "foo" + ia));
        }
        ArrayList<Integer> removeOrder = new ArrayList<Integer>(al);
        Collections.shuffle(removeOrder);
        ArrayList<Integer> removeOrderCopy = new ArrayList<Integer>(removeOrder);
        while (removeOrder.size() > 0) {
            int take = removeOrder.remove(removeOrder.size() - 1);
            Map.Entry o = s.remove(take);
            if (o == null) {
                System.out.println(al);
                System.out.println(removeOrderCopy);
                return false;
            }
        }
        return true;
    }

    // protected CacheEntryMapStub c0;
    //
    // protected CacheEntryMapStub c1;
    //
    // protected CacheEntryMapStub c2;
    //
    // protected CacheEntryMapStub c3;
    //
    // protected CacheEntryMapStub c4;
    //
    // protected CacheEntryMapStub c5;
    //
    // protected CacheEntryMapStub c6;
    //
    // public static junit.framework.Test suite() {
    // return new JUnit4TestAdapter(CacheEntryMapTest.class);
    // }
    //    
    // @Before
    // public void setUp() throws Exception {
    // c0 = new CacheEntryMapStub();
    // c1 = new CacheEntryMapStub(CacheTestBundle.createMap(1));
    // c2 = new CacheEntryMapStub(CacheTestBundle.createMap(2));
    // c3 = new CacheEntryMapStub(CacheTestBundle.createMap(3));
    // c4 = new CacheEntryMapStub(CacheTestBundle.createMap(4));
    // c5 = new CacheEntryMapStub(CacheTestBundle.createMap(5));
    // c6 = new CacheEntryMapStub(CacheTestBundle.createMap(6));
    // }

    // /**
    // * size returns the correct values.
    // */
    // @Test
    // public void testSize() {
    // assertEquals(0, c0.size());
    // assertEquals(5, c5.size());
    // }
    //
    // /**
    // * isEmpty is true of empty map and false for non-empty.
    // */
    // @Test
    // public void testIsEmpty() {
    // assertTrue(c0.isEmpty());
    // assertFalse(c5.isEmpty());
    // }
    //
    // /**
    // * containsKey returns true for contained key.
    // */
    // @Test
    // public void testContainsKey() {
    // assertTrue(c5.containsKey(1));
    // assertFalse(c5.containsKey(6));
    // }
    //
    // /**
    // * containsKey(null) throws NPE.
    // */
    // @Test(expected = NullPointerException.class)
    // public void testContainsKey_NullPointerException() {
    // c5.containsKey(null);
    // }
    //
    // /**
    // * containsValue returns true for held values.
    // */
    // @Test
    // public void testContainsValue() {
    // assertTrue(c5.valueContainsValue("A"));
    // assertFalse(c5.valueContainsValue("Z"));
    // }
    //
    // /**
    // * containsValue(null) throws NPE.
    // */
    // @Test(expected = NullPointerException.class)
    // public void testContainsValue_NullPointerException() {
    // c5.valueContainsValue(null);
    // }

    // /**
    // * Just test that the toString() method works.
    // */
    // @Test
    // public void testToString() {
    // String s = c5.toString();
    // try {
    // for (int i = 1; i < 6; i++) {
    // assertTrue(s.indexOf(String.valueOf(i)) >= 0);
    // assertTrue(s.indexOf("" + (char) (i + 64)) >= 0);
    // }
    // } catch (AssertionError ar) {
    // throw ar;
    // }
    // }

    /**
     * get(null) throws NPE.
     */
    // @Test(expected = NullPointerException.class)
    // public void testGetNull() {
    // c5.get(null);
    // }
    //
    // /**
    // * Test simple get.
    // */
    // @Test
    // public void testGet() {
    // assertEquals(M1.getValue(), c5.getValue(M1.getKey()));
    // assertEquals(M5.getValue(), c5.getValue(M5.getKey()));
    // }
    //
    static class EntryStub extends UnsynchronizedCacheEntry<Integer, String> {

        public EntryStub(Integer key, String value) {
            super(null, key, value, -1, 0, 0, 0, 0);
        }
    }
}
