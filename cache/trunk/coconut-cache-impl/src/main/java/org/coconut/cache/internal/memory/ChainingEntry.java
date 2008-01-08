package org.coconut.cache.internal.memory;

import org.coconut.cache.internal.service.cache.InternalCacheEntry;

public interface ChainingEntry<K, V> extends InternalCacheEntry<K, V> {
    ChainingEntry next();

    void setNext(ChainingEntry entry);

    int getHash();
}
