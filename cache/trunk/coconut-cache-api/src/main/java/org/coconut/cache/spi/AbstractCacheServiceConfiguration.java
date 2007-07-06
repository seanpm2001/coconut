/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.ByteArrayOutputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * All service bundles should define a Configuration class that is used to configure the
 * service.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheServiceConfiguration<K, V> {

    private final ResourceBundle bundle;

    private CacheConfiguration<K, V> conf;

    private final String serviceName;

    /**
     * Creates a new AbstractCacheServiceConfiguration.
     * 
     * @param serviceName
     *            the name of the service
     */
    public AbstractCacheServiceConfiguration(String serviceName) {
        this(serviceName,  CacheSPI.DEFAULT_CACHE_BUNDLE);
    }

    /**
     * Creates a new AbstractCacheServiceConfiguration.
     * 
     * @param serviceName
     *            the name of the service
     * @param bundle
     *            a ResourceBundle used for looking up text strings
     */
    public AbstractCacheServiceConfiguration(String serviceName,
           ResourceBundle bundle) {
        if (serviceName == null) {
            throw new NullPointerException("serviceName is null");
        }
        this.serviceName = serviceName;
        this.bundle = bundle;
    }

    /**
     * The parent {@link CacheConfiguration}, or <tt>null</tt> if this configuration
     * has not been registered yet.
     */
    public CacheConfiguration<K, V> c() {
        return conf;
    }

    /**
     * Returns the unique name of this service. For example,
     * {@link org.coconut.cache.service.expiration.CacheExpirationConfiguration} returns '{@value}
     * CacheExpirationConfiguration#SERVICE_NAME}'.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement(serviceName);
            doc.appendChild(root);
            toXML(doc, root);
            XmlConfigurator.prettyprint(doc, sos);
            return new String(sos.toByteArray());
        } catch (Exception e) {
            if (conf.getClass().getPackage().getName().startsWith(
                    Cache.class.getPackage().getName())) {
                // speciel exception for Coconut Cache implementations.
                throw new CacheException(CacheSPI.HIGHLY_IRREGULAR, e);
            } else {
                throw new CacheException(
                        "A String representation of this package could not be obtained, because it could not be serialized to XML",
                        e);
            }
        }
    }

    /**
     * Reads this configuration from an XML element.
     * 
     * @param element
     *            The XML Element to read the configuration from
     * @throws Exception
     *             if the configuration could not be properly read
     */
    protected abstract void fromXML(Element element) throws Exception;

    /**
     * Returns the ResourceBundle that is used by this configuration, or <code>null</code>
     * if no resource bundle is used.
     */
    protected ResourceBundle getResourceBundle() {
        return bundle;
    }

    /**
     * Attempts to lookup a resource bundle entry for the specified key.
     * 
     * @param key
     *            the key to lookup
     * @return the string matching the key
     * @throws IllegalStateException
     *             if no resource bundle has been specified when calling the constructor
     *             of this class
     * @throws CacheException
     *             if no entry could be found for specified key
     */
    protected String lookup(String key) {
        if (bundle == null) {
            throw new IllegalStateException("No bundle has been defined");
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            throw new CacheException(
                    "Could not find an entry for the specified resource bundle key "
                            + key, e);
        }
    }

    /**
     * Saves this configuration to xml.
     * 
     * @param doc
     *            the Document to write to
     * @param parent
     *            the top element of this configuration
     * @throws Exception
     *             this configuration could not be probably saved
     */
    protected abstract void toXML(Document doc, Element parent) throws Exception;

    void setConfiguration(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

}
