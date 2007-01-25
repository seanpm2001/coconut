/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services;

import java.util.Map;

import javax.management.JMException;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.cache.spi.service.AbstractCacheService;
import org.coconut.management.Managements;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagementCacheService<K, V> extends AbstractCacheService<K, V> {

    private final ManagedGroup group;

    public ManagementCacheService(CacheConfiguration<K, V> conf) {
        super(conf);
        group = Managements.newGroup(conf.getName(), "Base bean", conf.jmx().getMBeanServer());
    }

    public ManagedGroup getGroup() {
        return group;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheService#doStart(org.coconut.cache.spi.AbstractCache,
     *      java.util.Map)
     */
    @Override
    protected void doStart(AbstractCache<K, V> cache, Map<String, Object> properties)
            throws JMException {
        if (cache.getConfiguration().jmx().isRegister()) {
            group.registerAll(Managements.newRegistrant(cache.getConfiguration().jmx()
                    .getDomain(), "name", "service", "group"));
        }
    }
}
