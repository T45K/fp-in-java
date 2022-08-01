package io.github.t45k.fpinjava;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForTest {
    @Test
    public void testYield2OnOption() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        final Option<Integer> option1 = Option.of(1);
        final Option<Integer> option2 = Option.of(2);
        final Option<Integer> option3 = Option.None.INSTANCE;

        assertEquals(Option.of(3), For.yield2(option1, option2, Math::addExact));
        assertEquals(Option.None.INSTANCE, For.yield2(option2, option3, Math::addExact));
    }

    @Test
    public void testYield2OnList() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        final List<Integer> list1 = List.of(1, 2, 3);
        final List<Integer> list2 = List.of(4, 5);
        final List<Integer> list3 = List.Nil.INSTANCE;

        assertEquals(List.of(5, 6, 6, 7, 7, 8), For.yield2(list1, list2, Math::addExact));
        assertEquals(List.Nil.INSTANCE, For.yield2(list2, list3, Math::addExact));
    }
}