/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Before;
import org.junit.Test;

@RequireService( { CacheManagementService.class })
public class EvictionMXBean extends AbstractCacheTCKTest {

    static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration();

    CacheEvictionMXBean mxBean;

    MBeanServer mbs;

    // TODO
    // We should test for a default objectname
    // a.la. for cache named foo
    // ObjName=org.coconut.cache:name=343434, service=General

    @Before
    public void setup() {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumCapacity() {
        assertEquals(Long.MAX_VALUE, mxBean.getMaximumVolume());
        mxBean.setMaximumVolume(1000);
        assertEquals(1000, mxBean.getMaximumVolume());
        assertEquals(1000, eviction().getMaximumVolume());

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true).setMBeanServer(mbs).c()
                .eviction().setMaximumVolume(5000));
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
        assertEquals(5000, mxBean.getMaximumVolume());

        // Exception
        try {
            mxBean.setMaximumVolume(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Tests maximum size.
     */
    @Test
    public void maximumSize() {
        assertEquals(Integer.MAX_VALUE, mxBean.getMaximumSize());
        mxBean.setMaximumSize(1000);
        assertEquals(1000, mxBean.getMaximumSize());
        assertEquals(1000, eviction().getMaximumSize());

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true).setMBeanServer(mbs).c()
                .eviction().setMaximumSize(5000));
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
        assertEquals(5000, mxBean.getMaximumSize());

        // Exception
        try {
            mxBean.setMaximumSize(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {
            
        }
    }

    /**
     * Tests trimToSize.
     */
    @Test
    public void trimToSize() {
        put(5);
        assertSize(5);
        mxBean.trimToSize(3);
        assertSize(3);
        put(10, 15);
        assertSize(9);
        mxBean.trimToSize(1);
        assertSize(1);

        // Exception
        try {
            mxBean.trimToSize(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void trimToCapacity() {
    // TODO implement
    }

    @Test
    public void evictIdleElements() {
    // TODO implement
    }
}
