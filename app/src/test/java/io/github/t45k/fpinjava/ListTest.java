package io.github.t45k.fpinjava;


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
    public void testFoldRightWithValues() {
        final List<Integer> list = List.of(1, 2, 3, 4);
        assertEquals(1 + 2 + 3 + 4, list.foldRight(0, Math::addExact));
    }

    @Test
    public void testFoldRightWithoutValue() {
        final List<Integer> list = List.of();
        assertEquals(0, list.foldRight(0, Math::addExact));
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

    @Test
    public void testFoldRightReverseOrder() {
        final List<Integer> list = List.of(1, 2, 3, 4);
        assertEquals(List.of(4, 3, 2, 1), list.foldRight((List<Integer>) List.Nil.INSTANCE, (a, b) -> b.append(a)));
    }

    @Test
    public void testFoldLeftNaturalOrder() {
        final List<Integer> list = List.of(1, 2, 3, 4);
        assertEquals(List.of(1, 2, 3, 4), list.foldLeft((List<Integer>) List.Nil.INSTANCE, (a, b) -> b.append(a)));
    }

    @Test
    public void testZipWith() {
        final List<Integer> list1 = List.of(1, 2, 3, 4);
        final List<Integer> list2 = List.of(5, 6, 7, 8);
        assertEquals(List.of(6, 8, 10, 12), list1.zipWith(list2, Math::addExact));
    }

    @Test
    public void testListIsMonad() {
        final Function<Integer, List<Integer>> multiplyList = v -> Stream.generate(() -> v)
            .limit(v)
            .reduce(
                (List<Integer>) List.Nil.INSTANCE,
                (a, b) -> a.append(List.of(b)),
                List::append);

        // return x >>= f == f x
        assertEquals(multiplyList.apply(4), List.of(4).flatMap(multiplyList));

        // m >>= return == m
        assertEquals(List.of(1, 2, 3, 4), List.of(1, 2, 3, 4).flatMap(List::of));

        //(m >>= f) >>= g == m >>= (\x -> f x >>= g)
        final Function<Integer, List<Integer>> multiplyListTwice = v -> Stream.generate(() -> v)
            .limit(v * 2)
            .reduce(
                (List<Integer>) List.Nil.INSTANCE,
                (a, b) -> a.append(List.of(b)),
                List::append);
        assertEquals(
            List.of(1, 2, 3, 4).flatMap(v -> multiplyList.apply(v).flatMap(multiplyListTwice)),
            List.of(1, 2, 3, 4).flatMap(multiplyList).flatMap(multiplyListTwice)
        );
    }
}
