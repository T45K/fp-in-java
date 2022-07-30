package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EitherTest {

    @Test
    public void testFlatMapOnRight() {
        final Either<String, String> either = new Either.Right<>("Hello, ");

        assertEquals(new Either.Right<>("Hello, world"), either.flatMap(s -> new Either.Right<>(s + "world")));
        assertEquals(new Either.Left<>("Error"), either.flatMap(s -> new Either.Left<>("Error")));
    }
}