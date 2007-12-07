package org.coconut.cache.test.util.lifecycle;

import java.util.ArrayList;
import java.util.LinkedList;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;

public class LifecycleVerifierContext {
    private int id = 0;;

    final CacheConfiguration conf;

    final ArrayList<String> logging = new ArrayList<String>();

    private final LinkedList<LifecycleVerifier> list = new LinkedList<LifecycleVerifier>();

    public LifecycleVerifierContext(CacheConfiguration conf) {
        this.conf = conf;
    }

    public LifecycleVerifier create() {
        return create(new AbstractCacheLifecycle() {});
    }
    public LifecycleVerifier createNever() {
        LifecycleVerifier lw = create(new AbstractCacheLifecycle() {});
        lw.finished();
        return lw;
    }
    
    public LifecycleVerifier create(CacheLifecycle cl) {
        LifecycleVerifier lw = new LifecycleVerifier(this, cl, ++id);
        if (list.size() > 0) {
            lw.previous = list.getLast();
            list.getLast().next = lw;
        }
        conf.serviceManager().add(lw);
        list.add(lw);
        return lw;
    }

    public void verify() {
        for (LifecycleVerifier lv : list) {
            lv.verify();
        }
    }

    public void printDebug() {
        System.out.println("---------LifecycleVerifierContext Debug-------------");
        for (String s : logging) {
            System.out.println(s);
        }
        System.out.println("----------------------------------------------------");
        System.out.println();
    }
}
