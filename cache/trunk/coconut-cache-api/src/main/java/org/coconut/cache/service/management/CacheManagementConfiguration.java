/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import static org.coconut.internal.util.XmlUtil.addAndSetText;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure how the cache can be remotely monitored and
 * controlled (via JMX).
 * <p>
 * If for some reason the cache fails to properly register with the MBeanServer
 * at construction time a {@link CacheException} is thrown.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheManagementConfiguration extends AbstractCacheServiceConfiguration {

    private final static CacheManagementConfiguration DEFAULT = new CacheManagementConfiguration();

    final static String DOMAIN_TAG = "domain";

    final static String MANAGEMENT_TAG = "management";

    private String domain = CacheMXBean.DEFAULT_JMX_DOMAIN;

    private MBeanServer mBeanServer;

    public CacheManagementConfiguration() {
        super(MANAGEMENT_TAG, CacheManagementService.class);
    }

    public String getDomain() {
        return domain;
    }

    /**
     * @return the configured MBeanServer or the platform MBeanServer if no
     *         server has been set
     */
    public MBeanServer getMBeanServer() {
        if (mBeanServer == null) {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
        return mBeanServer;
    }

    /**
     * Sets the specific domain that this cache should register under. If no
     * domain is specified domain the cache will use is
     * {@link CacheMXBean#DEFAULT_JMX_DOMAIN}.
     * 
     * @param name
     *            the domain name
     * @return this configuration
     * @throws NullPointerException
     *             if domain is <tt>null</tt>
     */
    public CacheManagementConfiguration setDomain(String domain) {
        if (domain == null) {
            throw new NullPointerException("domain is null");
        }
        // TODO validate domain name
        this.domain = domain;
        return this;
    }

    /**
     * Sets the {@link MBeanServer}} that the cache should register with. If no
     * value is set the platform {@link MBeanServer} will be used.
     * 
     * @param server
     *            the server that the cache should register with
     * @return this configuration
     * @throws NullPointerException
     *             if server is <tt>null</tt>
     */
    public CacheManagementConfiguration setMbeanServer(MBeanServer server) {
        if (server == null) {
            throw new NullPointerException("server is null");
        }
        mBeanServer = server;
        return this;
    }

    /**
     * @see org.coconut.cache.service.spi.AbstractCacheConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element e) {
        Element domainTag = getChild(DOMAIN_TAG, e);
        if (domainTag != null) {
            domain = domainTag.getTextContent();
        }
    }

    /**
     * @see org.coconut.cache.service.spi.AbstractCacheConfiguration#toXML(org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) {
        /* Domain Filter */
        if (!domain.equals(DEFAULT.domain)) {
            addAndSetText(doc, DOMAIN_TAG, base, domain);
        }
        /* MBeanServer */
        if (mBeanServer != null
                || mBeanServer != ManagementFactory.getPlatformMBeanServer()) {
            addComment(doc, "management.cannotPersistMBeanServer", base);
        }
    }

    //Register event notification
}
