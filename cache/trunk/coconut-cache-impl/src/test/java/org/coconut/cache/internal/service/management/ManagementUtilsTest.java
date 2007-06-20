package org.coconut.cache.internal.service.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.coconut.cache.Cache;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.management.ManagedGroup;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

public class ManagementUtilsTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void testWrapService() {
        final CacheManagementService mock = context.mock(CacheManagementService.class);
        final ManagedGroup dummy = context.mock(ManagedGroup.class);
        context.checking(new Expectations() {
            {
                one(mock).getRoot();
                will(returnValue(dummy));
            }
        });
        CacheManagementService service = ManagementUtils.wrapService(mock);
        assertSame(dummy, service.getRoot());
    }

    @Test(expected = NullPointerException.class)
    public void testWrapServiceNPE() {
        ManagementUtils.wrapService(null);
    }

    @Test
    public void testWrapMXBean() {
        final Cache mock = context.mock(Cache.class);
        context.checking(new Expectations() {
            {
                one(mock).clear();
                one(mock).evict();
                one(mock).getCapacity();
                will(returnValue(1l));
                one(mock).getName();
                will(returnValue("fooName"));
                one(mock).size();
                will(returnValue(2));
            }
        });
        CacheMXBean mxBean = ManagementUtils.wrapMXBean(mock);
        mxBean.clear();
        mxBean.evict();
        assertEquals(1l, mxBean.getCapacity());
        assertEquals("fooName", mxBean.getName());
        assertEquals(2, mxBean.getSize());
    }

    @Test(expected = NullPointerException.class)
    public void testWrapMXBeanNPE() {
        ManagementUtils.wrapMXBean(null);
    }
}
