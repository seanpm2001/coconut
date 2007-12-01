/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.event.CacheEntryEvent;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
abstract class InternalEntryEvent<K, V> implements CacheEntryEvent<K, V> {
    private final Cache<K, V> cache;

    private final CacheEntry<K, V> entry;

    InternalEntryEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
        this.cache = cache;
        this.entry = entry;
    }

    /** {@inheritDoc} */
    public Cache<K, V> getCache() {
        return cache;
    }

    /** {@inheritDoc} */
    public K getKey() {
        return entry.getKey();
    }

    /** {@inheritDoc} */
    public V getValue() {
        return entry.getValue();
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    /** {@inheritDoc} */
    public V setValue(V value) {
        return entry.setValue(value);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(":");
        builder.append("[key = ");
        builder.append(getKey());
        builder.append(", value = ");
        builder.append(getValue());
        builder.append("]");
        return builder.toString();
    }

    private boolean equals(CacheEntryEvent<K, V> event) {
        return this.getKey().equals(event.getKey()) && this.getValue().equals(event.getValue());
    }

    static <K, V> CacheEntryEvent.ItemAdded<K, V> added(Cache<K, V> cache, CacheEntry<K, V> entry,
            boolean isLoaded) {
        return new AddedEvent<K, V>(cache, entry);
    }

    static <K, V> CacheEntryEvent<K, V> evicted(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, false);
    }

    static <K, V> CacheEntryEvent<K, V> expired(Cache<K, V> cache, CacheEntry<K, V> entry) {
        return new RemovedEvent<K, V>(cache, entry, true);
    }

    static <K, V> CacheEntryEvent<K, V> removed(Cache<K, V> cache, CacheEntry<K, V> entry,
            boolean isExpired) {
        return new RemovedEvent<K, V>(cache, entry, isExpired);
    }

    static <K, V> CacheEntryEvent<K, V> updated(Cache<K, V> cache, CacheEntry<K, V> entry,
            V previous, boolean isLoaded, boolean isExpired) {
        return new ChangedEvent<K, V>(cache, entry, previous, isExpired);
    }

    static class AddedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemAdded<K, V> {

        /**
         * @param cache
         * @param entry
         */
        AddedEvent(Cache<K, V> cache, CacheEntry<K, V> entry) {
            super(cache, entry);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CacheEntryEvent.ItemAdded) {
                CacheEntryEvent.ItemAdded<K, V> event = (ItemAdded<K, V>) obj;
                return super.equals(event); // && isLoaded() == event.isLoaded();
            }
            return false;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemAdded.NAME;
        }

        @Override
        public int hashCode() {
            return super.hashCode() ^ getName().hashCode();
        }

    }

    static class ChangedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemUpdated<K, V> {
        private boolean isExpired;

        private V previous;

        /**
         * @param cache
         * @param entry
         */
        ChangedEvent(Cache<K, V> cache, CacheEntry<K, V> entry, V previous, boolean isExpired) {
            super(cache, entry);
            this.previous = previous;
            this.isExpired = isExpired;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemUpdated.NAME;
        }

        /** {@inheritDoc} */
        public V getPreviousValue() {
            return previous;
        }

        /** {@inheritDoc} */
        public boolean hasExpired() {
            return isExpired;
        }
    }

    static class RemovedEvent<K, V> extends InternalEntryEvent<K, V> implements
            CacheEntryEvent.ItemRemoved<K, V> {

        private boolean hasExpired;

        /**
         * @param cache
         * @param entry
         */
        RemovedEvent(Cache<K, V> cache, CacheEntry<K, V> entry, boolean hasExpired) {
            super(cache, entry);
            this.hasExpired = hasExpired;
        }

        /** {@inheritDoc} */
        public String getName() {
            return CacheEntryEvent.ItemRemoved.NAME;
        }

        /** {@inheritDoc} */
        public boolean hasExpired() {
            return hasExpired;
        }

    }
}
