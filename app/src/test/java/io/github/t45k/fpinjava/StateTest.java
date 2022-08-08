package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import java.util.Random;

class StateTest {
    @Test
    public void testOverview() {
        final Random random = new Random(0);
        random.nextInt();
        final State<Random, Integer> state = State.<Random, Integer>of(0)
            .map(i -> i + 1)
            .map(i -> i * 2);
        System.out.println(state.run().apply(random));
    }
}