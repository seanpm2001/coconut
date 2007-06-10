/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.ReplacementPolicy;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEvictionConfigurationTest {
    private CacheEvictionConfiguration<Number, Collection<?>> ee;

    private CacheEvictionConfiguration<Number, Collection<?>> DEFAULT = new CacheEvictionConfiguration<Number, Collection<?>>();

    @Before
    public void setUp() {
        ee = new CacheEvictionConfiguration<Number, Collection<?>>();
    }

    @Test
    public void testScheduledEvictionAtFixedRate() {
        assertEquals(ee, ee.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS));
        assertEquals(4000, ee.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetScheduledEvictionAtFixedRateIAE() {
        ee.setScheduledEvictionAtFixedRate(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumCapacity() {
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(ee, ee.setMaximumCapacity(4));
        assertEquals(4, ee.getMaximumCapacity());
        ee.setMaximumCapacity(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumSize() {
        assertEquals(Integer.MAX_VALUE, ee.getMaximumSize());
        assertEquals(ee, ee.setMaximumSize(4));
        assertEquals(4, ee.getMaximumSize());
        ee.setMaximumSize(-1);
    }

    @Test
    public void testPolicy() {
        assertNull(ee.getPolicy());
        ReplacementPolicy<?> p =MockTestCase.mockDummy(ReplacementPolicy.class);
        assertEquals(ee, ee.setPolicy(p));
        assertEquals(p, ee.getPolicy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreferableCapacity() {
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
        assertEquals(ee, ee.setPreferableCapacity(4));
        assertEquals(4, ee.getPreferableCapacity());
        ee.setPreferableCapacity(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreferableSize() {
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(ee, ee.setPreferableSize(4));
        assertEquals(4, ee.getPreferableSize());
        ee.setPreferableSize(-1);
    }

    @Test
    public void testNoop() throws Exception {
        ee = reloadService(ee);
        assertEquals(Integer.MAX_VALUE, ee.getMaximumSize());
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
        assertEquals(DEFAULT.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS), ee
                .getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
    }

    @Test
    public void testEviction() throws Exception {
        ee.setMaximumSize(1);
        ee.setPreferableSize(2);
        ee.setMaximumCapacity(3);
        ee.setPreferableCapacity(4);
        ee.setScheduledEvictionAtFixedRate(360000, TimeUnit.MILLISECONDS);
        ee = reloadService(ee);
        assertEquals(1, ee.getMaximumSize());
        assertEquals(2, ee.getPreferableSize());
        assertEquals(3, ee.getMaximumCapacity());
        assertEquals(4, ee.getPreferableCapacity());
        assertEquals(360, ee.getScheduledEvictionAtFixedRate(TimeUnit.SECONDS));

    }

    @Test
    public void testCornerCase() throws Exception {
        // coverage mostly
        ee.setMaximumSize(2);
        ee = reloadService(ee);
        assertEquals(2, ee.getMaximumSize());
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
//        
// ee = CacheConfiguration.create();
// ee.setPreferableSize(3);
// ee = rw(ee);
// assertEquals(3, ee.getPreferableSize());
    }
}
