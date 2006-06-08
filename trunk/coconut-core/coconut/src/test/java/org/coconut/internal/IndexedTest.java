package org.coconut.internal;

import static org.coconut.test.CollectionUtils.seq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.JUnit4TestAdapter;

import org.coconut.internal.IndexedHeap;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class IndexedTest {

    private int plenty = 1000;// magic number

    private int many = plenty * 10; // magic number

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(IndexedTest.class);
    }

    private IndexedHeap<Integer> create() {
        return new IndexedHeap<Integer>(5);
    }

    @Test
    public void testNoArgConstructor() {
        IndexedHeap<Integer> heap = new IndexedHeap<Integer>();
        heap.add(1);
        assertEquals(1, heap.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIAE() {
        new IndexedHeap<Integer>(-1);
    }

    @Test
    public void testAdd() {
        IndexedHeap<Integer> heap = create();
        heap.add(1);
        assertEquals(1, heap.size());
        heap.add(2);
        assertEquals(2, heap.size());
        heap.add(3);
        assertEquals(3, heap.size());
    }

    @Test
    public void testAddPriority() {
        IndexedHeap<Integer> heap = create();
        heap.add(1, 1);
        assertEquals(1, heap.size());
        heap.add(2, 2);
        assertEquals(2, heap.size());
        heap.add(3, 3);
        assertEquals(3, heap.size());
    }

    @Test
    public void testAddMany() {
        IndexedHeap<Integer> heap = create();
        for (int i = 0; i < many; i++) {
            heap.add(i);
        }
        assertEquals(many, heap.size());
    }

    @Test
    public void testRemove() {
        IndexedHeap<Integer> heap = create();
        heap.add(0);
        heap.add(1);
        heap.add(2);
        Set<Integer> set = new TreeSet<Integer>();
        set.add(heap.poll());
        set.add(heap.poll());
        set.add(heap.poll());
        assertEquals(0, heap.size());
        assertEquals(3, set.size());
        assertTrue(set.containsAll(seq(0, 2)));
    }

    @Test
    public void testRemoveMany() {
        IndexedHeap<Integer> heap = create();
        for (int i = 0; i < many; i++) {
            heap.add(i);
        }
        Set<Integer> set = new TreeSet<Integer>();
        for (int i = 0; i < many; i++) {
            set.add(heap.poll());
        }
        assertEquals(0, heap.size());
        assertEquals(many, set.size());
    }

    @Test
    public void testRemovePrioritized() {
        IndexedHeap<Integer> heap = create();
        heap.add(0, 2);
        heap.add(1, 1);
        heap.add(2, 0);
        assertEquals(2, heap.poll());
        assertEquals(1, heap.poll());
        assertEquals(0, heap.poll());
    }

    @Test
    public void testRemoveManyPrioritized() {
        IndexedHeap<Integer> heap = create();

        Collection<Integer> numbers = seq(0, many - 1);
        List<Integer> list = new ArrayList<Integer>(numbers);

        Collections.shuffle(list); // create a random list
        for (int i = 0; i < many; i++) {
            Integer number = list.get(i);
            heap.add(number, number.intValue());
        }
        for (int i = 0; i < many; i++) {
            assertEquals(i, heap.poll());
        }
    }

    @Test
    public void testRemoveManyAddMany() {
        IndexedHeap<Integer> heap = create();

        int numberOfElements = this.many / 20; // magic number

        LinkedList<Integer> list1 = new LinkedList<Integer>(seq(0,
                numberOfElements * 3 - 1));
        LinkedList<Integer> s = new LinkedList<Integer>();
        Collections.shuffle(list1); // create a random list
        for (int i = 0; i < numberOfElements * 2; i++) {
            Integer number1 = list1.removeFirst();
            s.add(number1);
            heap.add(number1, number1.intValue());
        }
        Collections.sort(s);

        for (int i = 0; i < numberOfElements; i++) {
            Integer expected = s.removeFirst();
            assertEquals(expected, heap.poll());
        }

        assertEquals(numberOfElements, list1.size()); // just testing the test
        // logic
        assertEquals(numberOfElements, heap.size());
        assertEquals(numberOfElements, s.size());

        for (int i = 0; i < numberOfElements; i++) {
            Integer number1 = list1.removeFirst();
            s.add(number1);
            heap.add(number1, number1.intValue());
        }
        Collections.sort(s);
        for (int i = 0; i < numberOfElements * 2; i++) {
            Integer expected = s.removeFirst();
            assertEquals(expected, heap.poll());
        }

        assertEquals(0, heap.size());
        assertEquals(0, s.size());
    }

    @Test
    public void testRemoveEmpty() {
        IndexedHeap<Integer> list = create();
        assertNull(list.poll());
    }

    @Test
    public void testRemoveIndexed() {
        IndexedHeap<Integer> heap = create();
        int i1 = heap.add(2, 2);
        int i2 = heap.add(1, 1);
        int i3 = heap.add(0, 0);
        assertEquals(1, heap.remove(i2));
        assertEquals(0, heap.remove(i3));
        assertEquals(2, heap.remove(i1));
        assertEquals(0, heap.size());
    }

    @Test
    public void testRemoveIndexedMany() {
        IndexedHeap<Integer> heap = create();
        int[] ref = new int[many];
        for (int i = 0; i < many; i++) {
            ref[i] = heap.add(i);
        }
        Set<Integer> set = new TreeSet<Integer>();
        for (int i = 0; i < many; i++) {
            set.add(heap.remove(ref[i]));
        }
        assertEquals(0, heap.size());
        assertEquals(many, set.size());
    }

    @Test
    public void testRemoveIndexedPrioritized() {
        IndexedHeap<Integer> heap = create();
        int i1 = heap.add(0, 2);
        int i2 = heap.add(1, 1);
        int i3 = heap.add(2, 0);
        assertEquals(2, heap.remove(i3));
        assertEquals(1, heap.remove(i2));
        assertEquals(0, heap.remove(i1));
    }

    @Test
    public void testRemoveIndexedManyAddMany() {

        IndexedHeap<Integer> heap = create();
        Map<Integer, Integer> indexValMap = new HashMap<Integer, Integer>();
        int numberOfElements = this.many / 20; // magic number

        LinkedList<Integer> list1 = new LinkedList<Integer>(seq(0,
                numberOfElements * 3 - 1));
        LinkedList<Integer> s = new LinkedList<Integer>();
        Collections.shuffle(list1); // create a random list
        for (int i = 0; i < numberOfElements * 2; i++) {
            Integer number1 = list1.removeFirst();
            s.add(number1);
            int index = heap.add(number1, number1.intValue());
            indexValMap.put(index, number1);
        }

        LinkedList<Integer> indexes = new LinkedList<Integer>(indexValMap
                .keySet());

        Collections.shuffle(indexes); // create a random list

        for (int i = 0; i < numberOfElements; i++) {
            Integer index = indexes.removeFirst();
            Integer integer = heap.remove(index.intValue());
            s.remove(integer);
            assertEquals(integer, indexValMap.get(index));
        }

        assertEquals(numberOfElements, indexes.size()); // just testing the test
        // logic
        assertEquals(numberOfElements, heap.size());
        assertEquals(numberOfElements, s.size());

        for (int i = 0; i < numberOfElements; i++) {
            Integer number1 = list1.removeFirst();
            s.add(number1);
            heap.add(number1, number1.intValue());
        }

        Collections.sort(s);
        for (int i = 0; i < numberOfElements * 2; i++) {
            Integer expected = s.removeFirst();
            assertEquals(expected, heap.poll());
        }

        assertEquals(0, heap.size());
        assertEquals(0, s.size());
    }

    @Test
    public void testRemoveIndexEmpty() {
        IndexedHeap<Integer> heap = create();
        assertNull(heap.remove(0));
    }

    @Test
    public void testChangePriority() {
        IndexedHeap<Integer> heap = create();
        heap.add(2, 2);
        int i2 = heap.add(1, 1);
        int i3 = heap.add(0, 0);
        heap.setPriority(i3, 3);
        heap.setPriority(i2, 4);
        assertEquals(2, heap.poll());
        assertEquals(0, heap.poll());
        assertEquals(1, heap.poll());
    }

    @Test
    public void testChangePriorityMany() {
        Random r = new Random();
        IndexedHeap<Integer> heap = create();
        List<Integer> list = new ArrayList<Integer>(seq(0, plenty - 1));
        Collections.shuffle(list);
        EEntry[] entries = new EEntry[list.size()];
        for (int i = 0; i < list.size(); i++) {
            entries[i] = new EEntry(list.get(i));
            entries[i].prio = i;
            entries[i].index = heap.add(entries[i].val, i);
        }
        Collections.shuffle(list);
        for (int i = 0; i < list.size() * 10; i++) {
            EEntry e = entries[list.get(i % plenty).intValue()];
            e.prio += r.nextInt(plenty) - plenty / 2;
            heap.setPriority(e.index, e.prio);
        }
        List<EEntry> le = new ArrayList<EEntry>(Arrays.asList(entries));
        Collections.sort(le);

        long oldPrio = le.get(0).prio;
        Set<Integer> s1 = new TreeSet<Integer>();
        Set<Integer> s2 = new TreeSet<Integer>();

        for (int i = 0; i < plenty; i++) {
            if (le.get(i).prio != oldPrio) {
                s1.equals(s2);
                assertTrue(s1.size() > 0);
                s1.clear();
                s2.clear();
                oldPrio = le.get(i).prio;

            }
            s1.add(le.get(i).val);
            s2.add(heap.poll());

        }
        s1.equals(s2);
        assertEquals(0, heap.size());

    }

    @Test
    public void testPeek() {
        IndexedHeap<Integer> heap = create();
        assertNull(heap.peek());
        heap.add(1, 3);
        heap.add(2, 4);
        assertEquals(1, heap.peek());
        assertEquals(1, heap.poll());
        assertEquals(2, heap.peek());
        assertEquals(2, heap.poll());
    }

    @Test
    public void testPeekAll() {
        Random r = new Random();
        IndexedHeap<Integer> heap = create();
        for (int i = 0; i < many; i++) {
            heap.add(i, r.nextInt());
        }
        List<Integer> l = heap.peekAll();
        List<Integer> l2 = heap.peekAll();
        assertEquals(l, l2);
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < many; i++) {
            list.add(heap.poll());
        }
        assertEquals(l, list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePriorityNonExistingIndex() {
        IndexedHeap<Integer> list = create();
        list.add(1);
        list.setPriority(Integer.MAX_VALUE, 0);
    }

    static class EEntry implements Comparable<EEntry> {
        final Integer val;

        int index;

        long prio;

        public EEntry(Integer value) {
            val = value;
        }

        public int compareTo(EEntry e) {
            return (int) (prio - e.prio);
        }
    }
}
