package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateTest {

    private static class MimicRandom {
        private int value = 0;

        int next() {
            final int retValue = value;
            value++;
            return retValue;
        }

        int getValue() {
            return value;
        }
    }

    @Test
    public void testConstructor() {
        final State<MimicRandom, Integer> state = new State<>(rand -> new Tuple2<>(rand.next(), rand));
        final MimicRandom rand = new MimicRandom();
        final Tuple2<Integer, MimicRandom> mutatedState = state.run().apply(rand);

        assertEquals(0, mutatedState.first());
        assertEquals(1, mutatedState.second().getValue());
        assertEquals(1, rand.getValue()); // Original rand is also mutated because Action has side effects.

        final Tuple2<Integer, MimicRandom> mutatedState2 = state.run().apply(mutatedState.second());

        assertEquals(1, mutatedState2.first());
        assertEquals(2, mutatedState2.second().getValue());
        assertEquals(2, mutatedState.second().getValue());
        assertEquals(2, rand.getValue());
    }


    @Test
    public void testFlatMap() {
        final State<MimicRandom, Integer> state = new State<>(rand -> new Tuple2<>(rand.next(), rand));
        final State<MimicRandom, Integer> flatMappedState = state.flatMap(a -> new State<>(rand -> {
            rand.next();
            rand.next();
            rand.next();
            rand.next();
            return new Tuple2<>(a + 100, rand);
        }));


        final MimicRandom rand = new MimicRandom();
        final Tuple2<Integer, MimicRandom> flatMapped = flatMappedState.run().apply(rand);

        assertEquals(100, flatMapped.first()); // flatMap前に rand#next が一回走る
        assertEquals(5, flatMapped.second().getValue());
        assertEquals(5, rand.getValue());
    }

    @Test
    public void testMap() {
        final State<MimicRandom, Integer> state = new State<>(rand -> new Tuple2<>(rand.next(), rand));
        final State<MimicRandom, Integer> mappedState = state.map(a -> a + 100);

        final MimicRandom rand = new MimicRandom();
        final Tuple2<Integer, MimicRandom> mapped = mappedState.run().apply(rand);

        assertEquals(100, mapped.first());
        assertEquals(1, mapped.second().getValue());
        assertEquals(1, rand.getValue());
    }

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