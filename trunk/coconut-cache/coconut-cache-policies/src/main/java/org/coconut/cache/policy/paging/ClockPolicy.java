package org.coconut.cache.policy.paging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.spi.AbstractPolicy;
import org.coconut.internal.IndexedList;

/**
 * <a href="http://larch-www.lcs.mit.edu:8001/~corbato/">Frank Corbat�</a>
 * introduced CLOCK in 1968 as a one-bit approximation to LRU in the Multics
 * system.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@ThreadSafe(false)
public class ClockPolicy<T> extends AbstractPolicy<T> implements ReplacementPolicy<T>,
        Serializable, Cloneable {

    /** A unique policy name. */
    public static final String NAME = "Clock";

    /** serialVersionUID */
    private static final long serialVersionUID = -861463593222316896L;

    private final InnerClockPolicy<T> policy;

    public ClockPolicy() {
        this(100);
    }

    public ClockPolicy(ClockPolicy other) {
        policy = new InnerClockPolicy<T>(other.policy);
    }

    public ClockPolicy(int initialCapacity) {
        policy = new InnerClockPolicy<T>(initialCapacity);
    }

    /**
     * @see java.lang.Object#clone()
     */
    public ClockPolicy<T> clone() {
        return new ClockPolicy<T>(this);
    }

    private static class InnerClockPolicy<T> extends IndexedList<T> implements Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -2146530585201851381L;

        /** bits indicating whether or not an entry has been visited. */
        private int[] bits;

        /** the current placement of the clock. */
        private int handPosition;

        /**
         * Constructs a new ClockPolicy with an initial size of 100.
         */
        InnerClockPolicy() {
            this(100);
        }

        /**
         * Constructs a new ClockPolicy by copying an existing ClockPolicy.
         * 
         * @param copyFrom
         *            the clock policy to copy from
         */
        InnerClockPolicy(InnerClockPolicy other) {
            super(other);
            handPosition = other.handPosition;
            bits = new int[other.bits.length];
            System.arraycopy(other.bits, 0, bits, 0, other.bits.length);
        }

        /**
         * Constructs a new ClockPolicy with a specified initial size.
         * 
         * @param initialCapacity
         *            the initial capacity for this policy, must be bigger then
         *            0.
         * @throws IllegalArgumentException
         *             if <tt>initialCapacity</tt> is less than 1
         */
        InnerClockPolicy(int initialCapacity) {
            super(initialCapacity);
            bits = new int[initialCapacity + 1];
            bits[0] = 1;
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
         */
        public T evictNext() {
            if (currentEntryIndex == 0) {
                return null;
            } else {
                for (;;) {
                    if (bits[handPosition] == 0) {
                        freeEntries[--currentEntryIndex] = handPosition; // recycle
                        // entry
                        T removeMe = data[handPosition];
                        data[handPosition] = null;

                        if (currentEntryIndex == 0) { // clear
                            next[0] = 0;
                            prev[0] = 0;

                        } else {
                            prev[next[handPosition]] = prev[handPosition]; // update
                            // next
                            // head
                            next[prev[handPosition]] = next[handPosition];
                        }
                        bits[handPosition] = 0;
                        handPosition = next[handPosition];
                        return removeMe;
                    } else {
                        if (handPosition != 0) // keep head-index at 1
                            bits[handPosition] = 0;
                        handPosition = next[handPosition];
                    }
                }
            }
        }

        /**
         * @see org.coconut.internal.IndexedList#innerAdd(int)
         */
        protected void innerAdd(int index) {
            if (currentEntryIndex == 1) // first element
                handPosition = index;
        }

        /**
         * @see org.coconut.internal.IndexedList#innerRemove(int)
         */
        protected void innerRemove(int index) {
            bits[index] = 0; // lazy recycle
            if (index == handPosition)
                handPosition = next[index];
        }

        /**
         * @see org.coconut.internal.IndexedList#innerResize(int)
         */
        protected void innerResize(int newSize) {
            super.innerResize(newSize);
            int[] oldBits = bits;
            bits = new int[newSize];
            System.arraycopy(oldBits, 0, bits, 0, Math.min(oldBits.length, freeEntries.length));

        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#peek()
         */
        public T peek() {
            if (currentEntryIndex == 0) {
                return null;
            } else {
                return peekAll().get(0); // TODO optimize
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
         */
        public List<T> peekAll() {
            ArrayList<T> col = new ArrayList<T>(currentEntryIndex);
            if (currentEntryIndex == 0) {
                return col;
            } else {
                int tempClock = handPosition;
                int clockCount = 2; // we need to go around two times, one 0's
                // and
                // one for 1's
                for (;;) {
                    if (tempClock == handPosition && clockCount-- == 0)
                        return col; // okay we have been around twice
                    if (clockCount == 1) {
                        // looking for 0's
                        if (bits[tempClock] == 0) {
                            col.add(data[tempClock]);
                        }
                    } else {
                        // looking for 1's
                        if (bits[tempClock] == 1) {
                            col.add(data[tempClock]);
                        }
                    }
                    tempClock = next[tempClock];
                    if (tempClock == 0) {
                        tempClock = next[tempClock];
                    }
                }
            }
        }

        /**
         * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
         */
        public void touch(int index) {
            bits[index] = 1;
        }
    }

    public T remove() {
        return policy.remove();
    }

    public int getSize() {
        return policy.getSize();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Clock Policy with " + getSize() + " entries";
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#add(T)
     */
    public int add(T data) {
        return policy.add(data);
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
     */
    public void touch(int index) {
        policy.touch(index);
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#evictNext()
     */
    public T evictNext() {
        return policy.evictNext();
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#remove(int)
     */
    public T remove(int index) {
        return policy.remove(index);
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
     */
    public List<T> peekAll() {
        return policy.peekAll();
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#peek()
     */
    public T peek() {
        return policy.peek();
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#update(int,
     *      java.lang.Object)
     */
    public boolean update(int index, T newElement) {
        policy.replace(index, newElement);
        return false;
    }
}