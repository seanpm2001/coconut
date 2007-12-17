/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import org.coconut.cache.policy.IsCacheable;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.test.TestUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link CacheEvictionConfiguration}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEvictionConfigurationTest {

    static CacheEvictionConfiguration<Integer, String> DEFAULT = new CacheEvictionConfiguration<Integer, String>();

    private CacheEvictionConfiguration<Integer, String> conf;

    @Before
    public void setUp() {
        conf = new CacheEvictionConfiguration<Integer, String>();
    }

    @Test
    public void testMaximumCapacity() {
        assertEquals(0, conf.getMaximumVolume());
        assertSame(conf, conf.setMaximumVolume(4));
        assertEquals(4, conf.getMaximumVolume());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumCapacityIAE() {
        conf.setMaximumVolume(-1);
    }

    @Test
    public void testMaximumCapacityXML() throws Exception {
        conf = reloadService(conf);
        assertEquals(0, conf.getMaximumVolume());
        assertSame(conf, conf.setMaximumVolume(Long.MAX_VALUE));

        conf = reloadService(conf);
        assertEquals(Long.MAX_VALUE, conf.getMaximumVolume());
    }

    @Test
    public void isDisabled() {
        assertFalse(conf.isDisabled());
        assertSame(conf, conf.setDisabled(true));
        assertTrue(conf.isDisabled());
    }

    @Test
    public void isDisabledXML() throws Exception {
        conf = reloadService(conf);
        assertFalse(conf.isDisabled());
        assertSame(conf, conf.setDisabled(true));

        conf = reloadService(conf);
        assertTrue(conf.isDisabled());
    }

    @Test
    public void testMaximumSize() {
        assertEquals(0, conf.getMaximumSize());
        assertSame(conf, conf.setMaximumSize(4));
        assertEquals(4, conf.getMaximumSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumSizeIAE() {
        conf.setMaximumSize(-1);
    }

    @Test
    public void testMaximumSizeXML() throws Exception {
        conf = reloadService(conf);
        assertEquals(0, conf.getMaximumSize());
        assertSame(conf, conf.setMaximumSize(Integer.MAX_VALUE));

        conf = reloadService(conf);
        assertEquals(Integer.MAX_VALUE, conf.getMaximumSize());
    }

    @Test
    public void isCacheable() {
        IsCacheable i = TestUtil.dummy(IsCacheable.class);
        assertNull(conf.getIsCacheableFilter());
        assertSame(conf, conf.setIsCacheableFilter(i));
        assertEquals(i, conf.getIsCacheableFilter());
    }

    @Test
    public void isCacheableXML() {
    // TODO
    }

    @Test
    public void testPolicy() {
        ReplacementPolicy<?> p = TestUtil.dummy(ReplacementPolicy.class);
        assertNull(conf.getPolicy());
        assertEquals(conf, conf.setPolicy(p));
        assertEquals(p, conf.getPolicy());
    }

    @Test
    public void testPolicyXML() {
    // TODO
    }

    // @Test
    // public void testPreferableSize() {
    // assertEquals(0, conf.getPreferableSize());
    // assertSame(conf, conf.setPreferableSize(4));
    // assertEquals(4, conf.getPreferableSize());
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void testPreferableSizeIAE() {
    // conf.setPreferableSize(-1);
    // }
    //
    // @Test
    // public void testPreferableSizeXML() throws Exception {
    // conf = reloadService(conf);
    // assertEquals(0, conf.getPreferableSize());
    // assertSame(conf, conf.setPreferableSize(Integer.MAX_VALUE));
    //
    // conf = reloadService(conf);
    // assertEquals(Integer.MAX_VALUE, conf.getPreferableSize());
    // }

// /**
// * Test default time to live. The default is that entries never needs to be refreshed.
// */
// @Test
// public void testEvictSchedule() {
// // initial values
// assertEquals(0, conf.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
// assertEquals(0, conf.getScheduledEvictionAtFixedRate(TimeUnit.SECONDS));
//
// assertEquals(conf, conf.setScheduledEvictionAtFixedRate(2, TimeUnit.SECONDS));
//
// assertEquals(2l, conf.getScheduledEvictionAtFixedRate(TimeUnit.SECONDS));
// assertEquals(2l * 1000, conf
// .getScheduledEvictionAtFixedRate(TimeUnit.MILLISECONDS));
// assertEquals(2l * 1000 * 1000, conf
// .getScheduledEvictionAtFixedRate(TimeUnit.MICROSECONDS));
// assertEquals(2l * 1000 * 1000 * 1000, conf
// .getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
//
// conf.setScheduledEvictionAtFixedRate(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
// assertEquals(Long.MAX_VALUE, conf
// .getScheduledEvictionAtFixedRate(TimeUnit.SECONDS));
// }
//
// @Test
// public void testDefaultTimeToLiveXML() throws Exception {
// conf = reloadService(conf);
// assertEquals(0, conf.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
// assertEquals(0, conf.getScheduledEvictionAtFixedRate(TimeUnit.SECONDS));
//
// conf.setScheduledEvictionAtFixedRate(60, TimeUnit.SECONDS);
// conf = reloadService(conf);
// assertEquals(60 * 1000, conf
// .getScheduledEvictionAtFixedRate(TimeUnit.MILLISECONDS));
// }
//
// @Test(expected = IllegalArgumentException.class)
// public void testDefaultTimeToLiveIAE() {
// conf.setScheduledEvictionAtFixedRate(-1, TimeUnit.MICROSECONDS);
// }
//
// @Test(expected = NullPointerException.class)
// public void testDefaultTimeToLiveNPE() {
// conf.setScheduledEvictionAtFixedRate(1, null);
// }
}
