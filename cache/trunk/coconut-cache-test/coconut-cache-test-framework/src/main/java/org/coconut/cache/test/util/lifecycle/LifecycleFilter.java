package org.coconut.cache.test.util.lifecycle;

import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.predicate.Predicate;

public class LifecycleFilter extends AbstractLifecycleVerifier implements Predicate {
    public boolean evaluate(Object element) {
        return false;
    }
}
