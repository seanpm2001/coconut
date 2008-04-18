/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import java.io.Serializable;
import java.util.Comparator;

import org.codehaus.cake.ops.Ops.DoubleComparator;
import org.codehaus.cake.ops.Ops.DoubleReducer;
import org.codehaus.cake.ops.Ops.IntComparator;
import org.codehaus.cake.ops.Ops.IntReducer;
import org.codehaus.cake.ops.Ops.LongComparator;
import org.codehaus.cake.ops.Ops.LongReducer;
import org.codehaus.cake.ops.Ops.Reducer;

/**
 * Various implementations of {@link Reducer}, {@link DoubleReducer}, {@link IntReducer}
 * and {@link LongReducer}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Reducers.java 587 2008-02-06 08:21:44Z kasper $
 */
public final class Reducers {

    /** A reducer that adds two double elements. The Reducer is serializable */
    public static final DoubleReducer DOUBLE_ADDER_REDUCER = new DoubleAdder();

    /**
     * A reducer returning the maximum of two double elements, using natural comparator.
     * The Reducer is serializable
     */
    public static final DoubleReducer DOUBLE_MAX_REDUCER = new NaturalDoubleMaxReducer();

    /**
     * A reducer returning the minimum of two double elements, using natural comparator.
     * The Reducer is serializable
     */
    public static final DoubleReducer DOUBLE_MIN_REDUCER = new NaturalDoubleMinReducer();

    /** A reducer that adds two int elements. The Reducer is serializable */
    public static final IntReducer INT_ADDER_REDUCER = new IntAdder();

    /**
     * A reducer returning the maximum of two int elements, using natural comparator. The
     * Reducer is serializable
     */
    public static final IntReducer INT_MAX_REDUCER = new NaturalIntMaxReducer();

    /**
     * A reducer returning the minimum of two int elements, using natural comparator. The
     * Reducer is serializable
     */
    public static final IntReducer INT_MIN_REDUCER = new NaturalIntMinReducer();

    /** A reducer that adds two double elements. The Reducer is serializable */
    public static final LongReducer LONG_ADDER_REDUCER = new LongAdder();

    /**
     * A reducer returning the maximum of two long elements, using natural comparator. The
     * Reducer is serializable
     */
    public static final LongReducer LONG_MAX_REDUCER = new NaturalLongMaxReducer();

    /**
     * A reducer returning the minimum of two long elements, using natural comparator. The
     * Reducer is serializable
     */
    public static final LongReducer LONG_MIN_REDUCER = new NaturalLongMinReducer();

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less
     * than any non-null element. The Reducer is serializable
     */
    public static final Reducer MAX_REDUCER = new NaturalMaxReducer();

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less
     * than any non-null element. The Reducer is serializable
     */
    public static final Reducer MIN_REDUCER = new NaturalMinReducer();

    /** Cannot instantiate. */
    private Reducers() {}

    /**
     * A reducer returning the maximum of two double elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static DoubleReducer doubleMaxReducer(DoubleComparator comparator) {
        return new DoubleMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two double elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static DoubleReducer doubleMinReducer(DoubleComparator comparator) {
        return new DoubleMinReducer(comparator);
    }

    /**
     * A reducer returning the maximum of two int elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static IntReducer intMaxReducer(IntComparator comparator) {
        return new IntMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two int elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static IntReducer intMinReducer(IntComparator comparator) {
        return new IntMinReducer(comparator);
    }

    /**
     * A reducer returning the maximum of two long elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static LongReducer longMaxReducer(LongComparator comparator) {
        return new LongMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two long elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static LongReducer longMinReducer(LongComparator comparator) {
        return new LongMinReducer(comparator);
    }

    /**
     * Returns a reducer returning the maximum of two Comparable elements, treating null
     * as less than any non-null element.
     *
     * @return a maximum reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T extends Comparable<? super T>> Reducer<T> maxReducer() {
        return MAX_REDUCER;
    }

    /**
     * A reducer returning the maximum of two elements, using the given comparator, and
     * treating null as less than any non-null element.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created Reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T> Reducer<T> maxReducer(Comparator<? super T> comparator) {
        return new MaxReducer(comparator);
    }

    /**
     * Returns a reducer that returns the minimum of two Comparable elements, treating
     * null as less than any non-null element.
     *
     * @return a minimum reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T extends Comparable<? super T>> Reducer<T> minReducer() {
        return MIN_REDUCER;
    }

    /**
     * A reducer returning the minimum of two elements, using the given comparator, and
     * treating null as greater than any non-null element.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created Reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T> Reducer<T> minReducer(Comparator<? super T> comparator) {
        return new MinReducer(comparator);
    }

    /** A reducer that adds two double elements. */
    static final class DoubleAdder implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5291652398158425686L;

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_ADDER_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two double elements, using the given comparator.
     */
    static final class DoubleMaxReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2065097741025480432L;

        /** DoubleComparator used when reducing. */
        private final DoubleComparator comparator;

        /**
         * Creates a DoubleMaxReducer.
         *
         * @param comparator
         *            the DoubleComparator to use
         */
        DoubleMaxReducer(DoubleComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two double elements, using the given comparator.
     */
    static final class DoubleMinReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6109941145562459503L;

        /** DoubleComparator used when reducing. */
        private final DoubleComparator comparator;

        /**
         * Creates a DoubleMinReducer.
         *
         * @param comparator
         *            the DoubleComparator to use
         */
        DoubleMinReducer(DoubleComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }

    /** A reducer that adds two int elements. */
    static final class IntAdder implements IntReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -8988998991050744720L;

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_ADDER_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two int elements, using the given comparator.
     */
    static final class IntMaxReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555681020470083072L;

        /** IntComparator used when reducing. */
        private final IntComparator comparator;

        /**
         * Creates a IntMaxReducer.
         *
         * @param comparator
         *            the IntComparator to use
         */
        IntMaxReducer(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two int elements, using the given comparator.
     */
    static final class IntMinReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -1965332824437951121L;

        /** IntComparator used when reducing. */
        private final IntComparator comparator;

        /**
         * Creates a IntMinReducer.
         *
         * @param comparator
         *            the IntComparator to use
         */
        IntMinReducer(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }

    /** A reducer that adds two double elements. */
    static final class LongAdder implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 3153032156748016348L;

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {return LONG_ADDER_REDUCER;}
    }

    /**
     * A reducer returning the maximum of two long elements, using the given comparator.
     */
    static final class LongMaxReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -3079323426451124529L;

        /** LongComparator used when reducing. */
        private final LongComparator comparator;

        /**
         * Creates a LongMaxReducer.
         *
         * @param comparator
         *            the LongComparator to use
         */
        LongMaxReducer(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two long elements, using the given comparator.
     */
    static final class LongMinReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4447554784861499048L;

        /** LongComparator used when reducing. */
        private final LongComparator comparator;

        /**
         * Creates a LongMinReducer.
         *
         * @param comparator
         *            the LongComparator to use
         */
        LongMinReducer(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two elements, using the given comparator, and
     * treating null as less than any non-null element.
     */
    static final class MaxReducer<T> implements Reducer<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5456282180421493663L;

        /** Comparator used when reducing. */
        private final Comparator<? super T> comparator;

        /**
         * Creates a MaxReducer.
         *
         * @param comparator
         *            the Comparator to use
         */
        MaxReducer(Comparator<? super T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two elements, using the given comparator, and
     * treating null as greater than any non-null element.
     */
    static final class MinReducer<T> implements Reducer<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 223676769245813970L;

        /** Comparator used when reducing. */
        private final Comparator<? super T> comparator;

        /**
         * Creates a MinReducer.
         *
         * @param comparator
         *            the Comparator to use
         */
        MinReducer(Comparator<? super T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /** A reducer returning the maximum of two double elements, using natural comparator. */
    static final class NaturalDoubleMaxReducer implements DoubleReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -5902864811727900806L;

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return Math.max(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_MAX_REDUCER;
        }
    }

    /** A reducer returning the minimum of two double elements, using natural comparator. */
    static final class NaturalDoubleMinReducer implements DoubleReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 9005140841348156699L;

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return Math.min(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_MIN_REDUCER;
        }
    }

    /** A reducer returning the maximum of two int elements, using natural comparator. */
    static final class NaturalIntMaxReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 9077782496369337326L;

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return a >= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_MAX_REDUCER;
        }
    }

    /** A reducer returning the minimum of two int elements, using natural comparator. */
    static final class NaturalIntMinReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6976117010912842877L;

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return a <= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_MIN_REDUCER;
        }
    }

    /** A reducer returning the maximum of two long elements, using natural comparator. */
    static final class NaturalLongMaxReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6933457108004180230L;

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return a >= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_MAX_REDUCER;
        }
    }

    /** A reducer returning the minimum of two long elements, using natural comparator. */
    static final class NaturalLongMinReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7592565629449709106L;

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return a <= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_MIN_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    static final class NaturalMaxReducer<T extends Comparable<? super T>> implements Reducer<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5079675958818175983L;

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || a.compareTo(b) >= 0) ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAX_REDUCER;
        }
    }

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    static final class NaturalMinReducer<T extends Comparable<? super T>> implements Reducer<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6750364835779757657L;

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || a.compareTo(b) <= 0) ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }
}
