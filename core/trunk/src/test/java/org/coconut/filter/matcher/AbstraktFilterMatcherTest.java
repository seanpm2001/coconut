/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter.matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstraktFilterMatcherTest {

	static final Map m1 = Collections.EMPTY_MAP;

	static final Set s1 = Collections.EMPTY_SET;

	static final List l1 = Collections.EMPTY_LIST;

	Mockery context = new JUnit4Mockery();

	@Test
	public void testAbstraktFilterMatcher() throws Exception {
		final Map m = context.mock(Map.class);

		final AbstractFilterMatcher<Integer, Filter> afm = new DummyMatcher<Integer, Filter>(
				m);
		context.checking(new Expectations() {
			{
				one(m).clear();

				one(m).containsKey(1);
				will(returnValue(true));

				one(m).containsValue("A");
				will(returnValue(true));

				one(m).entrySet();
				will(returnValue(s1));

				one(m).get(2);
				will(returnValue(Filters.TRUE));

				one(m).isEmpty();
				will(returnValue(true));

				one(m).keySet();
				will(returnValue(s1));

				one(m).put(3, Filters.IS_NUMBER);
				will(returnValue(Filters.TRUE));

				one(m).putAll(Collections.EMPTY_MAP);

				one(m).remove(4);
				will(returnValue(Filters.FALSE));

				one(m).size();
				will(returnValue(12));

				one(m).values();
				will(returnValue(l1));
			}
		});
		afm.clear();
		assertTrue(afm.containsKey(1));
		assertTrue(afm.containsValue("A"));
		assertSame(s1, afm.entrySet());
		assertEquals(Filters.TRUE, afm.get(2));
		assertEquals(m, afm.getMap());
		assertTrue(afm.isEmpty());
		assertSame(s1, afm.keySet());
		assertSame(Filters.TRUE, afm.put(3, Filters.IS_NUMBER));
		afm.putAll(Collections.EMPTY_MAP);
		assertSame(Filters.FALSE, afm.remove(4));
		assertEquals(12, afm.size());
		assertSame(l1, afm.values());
		
		assertSame(m, afm.getMap());
	}

	@Test
	public void testEqualsHashCodeToString() {
		HashMap hm = new HashMap();
		hm.put(1111, "foofoo");
		DummyMatcher dm = new DummyMatcher(hm);
		assertEquals(hm.toString(), dm.toString());
		assertEquals(hm.hashCode(), dm.hashCode());
		assertTrue(dm.equals(hm));
		assertFalse(dm.equals(Collections.EMPTY_MAP));
	}

	static class DummyMatcher<K, V> extends AbstractFilterMatcher<K, V> {
		DummyMatcher(Map m) {
			super(m);
		}

		/**
         * @see org.coconut.filter.matcher.FilterMatcher#match(java.lang.Object)
         */
		public List<K> match(V object) {
			throw new UnsupportedOperationException();
		}

		/**
         * @see org.coconut.filter.matcher.AbstractFilterMatcher#getMap()
         */
		@Override
		protected Map<K, Filter<? super V>> getMap() {
			return super.getMap();
		}
	}

}
