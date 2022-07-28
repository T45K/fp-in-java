package io.github.t45k.fpinjava.list;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public sealed interface List<T> permits List.Nil, List.Cons {
    final class Nil<T> implements List<T> {
        private Nil() {
        }

        public static Nil INSTANCE = new Nil<>();
    }

    record Cons<T>(T head, List<T> tail) implements List<T> {
    }

    @SafeVarargs
    static <A> List<A> of(A... values) {
        return switch (values.length) {
            case 0 -> Nil.INSTANCE;
            default -> new Cons<>(values[0], of(Arrays.copyOfRange(values, 1, values.length)));
        };
    }

    // @formatter:off
    default <R> R foldRight(final R initialValue, final BiFunction<T, R, R> function) {
        return switch (this) {
            case Nil<T> nil -> initialValue;
            case Cons<T>(var head, var tail) -> function.apply(head, tail.foldRight(initialValue, function));
        };
    }
    // @formatter:on

    // @formatter:off
    default <R> R foldLeft(final R initialValue, final BiFunction<T, R, R> function) {
        final BiFunction<List<T>, R, R> loop = new BiFunction<>() {
            @Override
            public R apply(final List<T> list, final R acc) {
                return switch (list) {
                    case Nil<T> nil -> acc;
                    case Cons<T>(var head, var tail) -> apply(tail, function.apply(head, acc));
                };
            }
        };

        return loop.apply(this, initialValue);
    }
    // @formatter:on

    default List<T> append(final List<T> list) {
        return this.foldRight(list, Cons::new);
    }

    default List<T> append(final T value) {
        return append(List.of(value));
    }

    // @formatter:off
    default <A> List<A> flatMap(final Function<T, List<A>> function){
        return switch (this) {
            case Nil<T> nil -> Nil.INSTANCE;
            case Cons<T>(var head, var tail) -> function.apply(head).append(tail.flatMap(function));
        };
    }
}
