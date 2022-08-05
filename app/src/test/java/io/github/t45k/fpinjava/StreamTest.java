package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamTest {

    @Test
    public void testOf() {
        final Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        assertTrue(Stream.equals(Stream.of(1, 2, 3, 4, 5), stream));
    }

    @Test
    public void testToList() {
        final Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        assertEquals(List.of(1, 2, 3, 4, 5), stream.toList());
    }

    @Test
    public void testTake() {
        final Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        assertTrue(Stream.equals(Stream.of(1, 2, 3), stream.take(3)));
    }

    @Test
    public void testDrop() {
        final Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        assertTrue(Stream.equals(Stream.of(4, 5), stream.drop(3)));
    }

    @Test
    public void testAnyIsShortCircuitEvaluation() {
        final Set<Integer> evaluated = new HashSet<>();
        final Stream<Integer> stream = Stream.of(
            () -> {
                evaluated.add(1);
                return 1;
            },
            () -> {
                evaluated.add(2);
                return 2;
            },
            () -> {
                evaluated.add(3);
                return 3;
            }
        );

        assertTrue(stream.any(v -> v == 2));
        assertTrue(evaluated.contains(1));
        assertTrue(evaluated.contains(2));
        assertFalse(evaluated.contains(3));
    }

    @Test
    public void testUnfold() {
        final Stream<Integer> infiniteStream = Stream.unfold(1, v -> Option.of(new Tuple2<>(1, 1)));
        assertTrue(Stream.equals(Stream.of(1, 1, 1), infiniteStream.take(3)));
    }

    @Test
    public void testFlatMap() {
        final Function<Integer, Stream<Integer>> function = v -> Stream.unfold(v, it -> Option.of(new Tuple2<>(v, v))).take(v);
        final Stream<Integer> stream = Stream.of(1, 2, 3);
        assertTrue(Stream.equals(Stream.of(1, 2, 2, 3, 3, 3), stream.flatMap(function)));
    }

    @Test
    public void testFlatMapWithEmpty() {
        final Function<Integer, Stream<Integer>> function = v -> Stream.Empty.INSTANCE;
        final Stream<Integer> stream = Stream.of(1, 2, 3);
        assertEquals(Stream.Empty.INSTANCE, stream.flatMap(function));
    }
}