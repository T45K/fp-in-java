package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}