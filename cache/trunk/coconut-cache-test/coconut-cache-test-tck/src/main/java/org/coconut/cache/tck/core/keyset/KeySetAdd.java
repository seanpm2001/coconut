/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.keyset;

import java.util.Collections;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySetAdd extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void addNPE() {
        try {
            newCache().keySet().add(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addUOE() {
        newCache().keySet().add(1);
    }

    @Test(expected = NullPointerException.class)
    public void addAllNPE() {
        try {
            newCache().keySet().addAll(null);
        } catch (UnsupportedOperationException e) {
            throw new NullPointerException(); // OK
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAllUOE() {
        newCache().keySet().addAll(Collections.singleton(1));
    }
}
