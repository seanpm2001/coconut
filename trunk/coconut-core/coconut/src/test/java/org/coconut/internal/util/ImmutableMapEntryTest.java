/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.internal.util;

import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.coconut.test.MavenDummyTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class ImmutableMapEntryTest extends MavenDummyTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ImmutableMapEntryTest.class);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testMapEntryTest() {
        
        Map.Entry<Integer, Integer> me = MapUtils.newMapEntry(
                0, 1);
        assertEquals(0, me.getKey().intValue());
        assertEquals(1, me.getValue().intValue());
        me.toString(); // doesn't throw exception
        me.setValue(2); //throws UnsupportedOperationException
    }

    @Test
    public void testHashcode() {
        assertEquals(0, MapUtils.newMapEntry(null, null)
                .hashCode());
        assertEquals(100 ^ 200, MapUtils.newMapEntry(100,
                200).hashCode());
    }

    @Test
    public void testEquals() {
        Map.Entry me = MapUtils.newMapEntry(0, 1);
        assertFalse(me.equals(null));
        assertFalse(me.equals(new Object()));
        assertTrue(me.equals(me));
        assertFalse(me.equals(MapUtils.newMapEntry(0, 0)));
        assertFalse(me.equals(MapUtils.newMapEntry(0, null)));
        assertFalse(me.equals(MapUtils.newMapEntry(1, 1)));
        assertFalse(me.equals(MapUtils.newMapEntry(null, 1)));
        assertTrue(me.equals(MapUtils.newMapEntry(0, 1)));
    }

}
