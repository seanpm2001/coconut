/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.ReplacementPolicy;
import org.coconut.cache.spi.AbstractPolicy;
import org.coconut.core.AttributeMap;
import org.coconut.internal.util.IndexedStack;

/**
 * A MRU based replacement policy.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@NotThreadSafe
public class MRUPolicy<T> extends AbstractPolicy<T> implements ReplacementPolicy<T>, Serializable {

    /** A unique policy name. */
    public static final String NAME = "MRU";

    /** serialVersionUID */
    private static final long serialVersionUID = 7334611172293116644L;

    /** The list used for bookkeeping. */
    private final IndexedStack<T> list;

    /**
     * Constructs a new MRUPolicy with an initial size of 100.
     */
    public MRUPolicy() {
        this(100);
    }

    /**
     * Constructs a new MRUPolicy with a specified initial size.
     * 
     * @param initialCapacity
     *            the initial size of the internal list, must be 0 or greater
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     */
    public MRUPolicy(int initialCapacity) throws IllegalArgumentException {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be a positive number or 0");
        }
        list = new IndexedStack<T>(initialCapacity);
    }

    /**
     * Constructs a new MRUPolicy by copying an existing MRUPolicy.
     * 
     * @param policy
     *            the MRUPolicy to copy from
     */
    public MRUPolicy(MRUPolicy policy) {
        list = new IndexedStack<T>(policy.list);
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#add(java.lang.Object, org.coconut.core.AttributeMap)
     */
    public int add(T data, AttributeMap ignore) {
        return list.add(data);
    }


    /**
     * @see org.coconut.cache.ReplacementPolicy#clear()
     */
    public void clear() {
        while (evictNext() != null) {
            /* ignore */
        }
    }


    @Override
    public MRUPolicy<T> clone() {
        return new MRUPolicy<T>(this);
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#evictNext()
     */
    public T evictNext() {
        return list.remove();
    }

    /**
     * @return the number of entries in currently held by the policy.
     */
    public int getSize() {
        return list.getSize();
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#peek()
     */
    public T peek() {
        return list.peek();
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#peekAll()
     */
    public List<T> peekAll() {
        return list.peekAll();
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#remove(int)
     */
    public T remove(int index) {
        return list.remove(index);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MRU Policy with " + list.getSize() + " entries";
    }

    /**
     * @see org.coconut.cache.ReplacementPolicy#touch(int)
     */
    public void touch(int index) {
        list.touch(index);
    }


    /**
     * @see org.coconut.cache.ReplacementPolicy#update(int, java.lang.Object, org.coconut.core.AttributeMap)
     */
    public boolean update(int index, T newElement, AttributeMap ignore) {
        list.replace(index, newElement);
        return true; // MRU never rejects an entry
    }

}
