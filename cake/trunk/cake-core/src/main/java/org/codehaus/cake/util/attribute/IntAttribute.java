/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import java.io.Serializable;

import jsr166y.forkjoin.Ops.ObjectToInt;

/**
 * An abstract implementation of an {@link Attribute} mapping to a int. This implementation adds a
 * number of methods that works on primitive ints instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class IntAttribute extends Attribute<Integer> {

    /** The default value of this attribute. */
    private final int defaultIntValue;

    /**
     * A MapperToInteger that takes an AttributeMap and returns the value of this attribute.
     */
    private final ObjectToInt<AttributeMap> mapperToInt = new AttributeMapToInt();

    /**
     * Creates a new AbstractIntegerAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of this attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public IntAttribute(String name, int defaultValue) {
        super(name, Integer.TYPE, defaultValue);
        this.defaultIntValue = defaultValue;
    }
    public int getDefaultValue() {
        return defaultIntValue;
    }
    /**
     * Analogous to {@link #checkValid(Integer)} except taking a primitive Integer.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(int value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Integer o) {
        checkValid(o.intValue());
    }

    /** {@inheritDoc} */
    @Override
    public Integer fromString(String str) {
        return Integer.parseInt(str);
    }

    /**
     * Analogous to {@link #get(AttributeMap)} except returning a primitive <tt>Integer</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public int getValue(AttributeMap attributes) {
        return attributes.getInt(this, defaultIntValue);
    }
    public int getValue(WithAttributes attributes) {
        return getValue(attributes.getAttributes());
    }
    
    public int getValue(WithAttributes attributes, int defaultValue) {
        return getValue(attributes.getAttributes(), defaultValue);
    }
    /**
     * Analogous to {@link #get(AttributeMap, Integer)} except returning a primitive
     * <tt>Integer</tt>.
     * 
     * @param attributes
     *            the attribute map to check for this attribute in
     * @param defaultValue
     *            the value to return if this attribute is not set in the specified attribute map
     * @return the value of this attribute
     */
    public int getValue(AttributeMap attributes, int defaultValue) {
        return attributes.getInt(this, defaultValue);
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive Integer as
     * parameter.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(int value) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Integer value) {
        return isValid(value.intValue());
    }

    /**
     * Returns a mapper that extracts the value of this attribute from an {@link AttributeMap}, or
     * returns {@link #getDefault()} if this attribute is not present.
     * 
     * @return a mapper from an AttributeMap to the value of this attribute
     */
    public ObjectToInt<AttributeMap> mapToInt() {
        return mapperToInt;
    }

    /**
     * Analogous to {@link #set(AttributeMap, Integer)} except taking a primitive Integer as
     * parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Integer)}
     */
    public AttributeMap set(AttributeMap attributes, int value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putInt(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, int value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Integer)} except taking a primitive Integer as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(int value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }

    /**
     * A MapperToInteger that maps from an attribute map to the value of this attribute.
     */
    class AttributeMapToInt implements ObjectToInt<AttributeMap>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -8341885789250723522L;

        /** {@inheritDoc} */
        public int op(AttributeMap t) {
            return getValue(t);
        }
    }
}
