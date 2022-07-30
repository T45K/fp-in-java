package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    public void testOfReturnsSomeInstance() {
        final Option<Integer> option = Option.of(1);
        assertTrue(option instanceof Option.Some<Integer>);
    }

    @Test
    public void testFlatMapOnSome() {
        final Function<String, Option<Integer>> parseInt = s -> {
            try {
                return Option.of(Integer.parseInt(s));
            } catch (final NumberFormatException e) {
                return Option.None.INSTANCE;
            }
        };

        assertEquals(Option.of(1), Option.of("1").flatMap(parseInt));
        assertEquals(Option.None.INSTANCE, Option.of("one").flatMap(parseInt));
    }

    @Test
    public void testFlatMapOnNone() {
        final Function<String, Option<Integer>> parseInt = s -> {
            try {
                return Option.of(Integer.parseInt(s));
            } catch (final NumberFormatException e) {
                return Option.None.INSTANCE;
            }
        };

        assertEquals(Option.None.INSTANCE, Option.None.INSTANCE.flatMap(parseInt));
    }

    @Test
    public void testGetOrElse() {
        assertEquals(1, Option.of(1).getOrElse(2));
        assertEquals(2, Option.None.INSTANCE.getOrElse(2));
        assertEquals(3, Option.of("3").map(Integer::parseInt).getOrElse(2));
    }

    @Test
    public void testOrElse() {
        assertEquals(Option.of(1), Option.of(1).orElse(Option.of(2)));
        assertEquals(Option.of(2), Option.None.INSTANCE.orElse(Option.of(2)));
        assertEquals(Option.of(3), Option.of("3").map(Integer::parseInt).orElse(Option.of(2)));
        assertEquals(Option.None.INSTANCE, Option.None.INSTANCE.orElse(Option.None.INSTANCE));
    }
}