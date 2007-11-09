/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

/**
 * A Filter class that can be used to filter elements. Can be used in the following way.
 * 
 * <pre>
 * class FileIsDirectoryFilter implements Filter&lt;File&gt; {
 *     public boolean accept(File file) {
 *         return file.isDirectory();
 *     }
 * }
 * </pre>
 * 
 * A number of files can then be tested against this filter accepting only the files that
 * are directories. A Filter should be stateless. Furthermore an application of the
 * <tt>accept</tt> method should not have any side effects.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 * @param <E>
 *            the type of elements acccepted by the accept method
 */
public interface Predicate<E> {

    /**
     * Tests the given element for acceptance.
     * 
     * @param element
     *            The element to check
     * @return <code>true</code> if the filter accepts the element; <code>false</code>
     *         otherwise.
     * @throws ClassCastException
     *             class of the specified element prevents it from being evaluated by this
     *             filter.
     * @throws NullPointerException
     *             if the specified element is null and this filter does not support null
     *             elements
     * @throws IllegalArgumentException
     *             some aspect of this element prevents it from being evaluated by this
     *             filter.
     */
    boolean evaluate(E element);
}
