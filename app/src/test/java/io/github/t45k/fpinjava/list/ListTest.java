package io.github.t45k.fpinjava.list;


import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListTest {

    @Test
    public void testOfWithValues() {
        final List<Integer> list = List.of(1, 2, 3, 4);
        assertTrue(list instanceof List.Cons<Integer>);
    }

    @Test
    public void testOfWithoutValue() {
        final List<Integer> list = List.of();
        assertEquals(List.Nil.INSTANCE, list);
    }

    @Test
    public void testReduceRightWithValues() {
        final List<Integer> list = List.of(1, 2, 3, 4);
        assertEquals(1 + 2 + 3 + 4, list.reduceRight(0, Math::addExact));
    }

    @Test
    public void testReduceRightWithoutValue() {
        final List<Integer> list = List.of();
        assertEquals(0, list.reduceRight(0, Math::addExact));
    }

    @Test
    public void testAppendWithTwoCons() {
        final List<Integer> list1 = List.of(1, 2, 3, 4);
        final List<Integer> list2 = List.of(5, 6, 7, 8);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), list1.append(list2));
    }

    @Test
    public void testAppendWithConsAndNil() {
        final List<Integer> list1 = List.of(1, 2, 3, 4);
        final List<Integer> list2 = List.Nil.INSTANCE;
        assertEquals(List.of(1, 2, 3, 4), list1.append(list2));
    }

    @Test
    public void testAppendWithNilAndCons() {
        final List<Integer> list1 = List.Nil.INSTANCE;
        final List<Integer> list2 = List.of(5, 6, 7, 8);
        assertEquals(List.of(5, 6, 7, 8), list1.append(list2));
    }

    @Test
    public void testAppendWithTwoNil() {
        final List<Integer> list1 = List.Nil.INSTANCE;
        final List<Integer> list2 = List.Nil.INSTANCE;
        assertEquals(List.Nil.INSTANCE, list1.append(list2));
    }

    @Test
    public void testAppendWithInterposingNil() {
        final List<Integer> list1 = List.of(1, 2, 3, 4);
        final List<Integer> list2 = List.of(5, 6, 7, 8);
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), list1.append(List.Nil.INSTANCE).append(list2));
    }

    @Test
    public void testFlatMap() {
        final List<Integer> list = List.of(1, 2, 3, 4);
        final Function<Integer, List<Integer>> multiplyList = v -> Stream.generate(() -> v)
            .limit(v)
            .reduce(
                (List<Integer>) List.Nil.INSTANCE,
                (a, b) -> a.append(List.of(b)),
                List::append);
        assertEquals(List.of(1, 2, 2, 3, 3, 3, 4, 4, 4, 4), list.flatMap(multiplyList));
    }
}
