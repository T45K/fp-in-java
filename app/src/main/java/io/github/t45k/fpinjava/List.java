package io.github.t45k.fpinjava;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public sealed interface List<T> extends Monad<T> permits List.Nil, List.Cons {
    final class Nil implements List {
        private Nil() {
        }

        public static Nil INSTANCE = new Nil();
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
            case Nil nil -> initialValue;
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
                    case Nil nil -> acc;
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

    default <A> List<A> flatMap(final Function<T, List<A>> function) {
        return this.foldRight((List<A>) Nil.INSTANCE, (a, acc) -> function.apply(a).append(acc));
    }

    default <A> List<A> map(final Function<T, A> function) {
        return this.flatMap(function.andThen(List::of));
    }

    // @formatter:off
    default <A, B> List<B> zipWith(final List<A> list, final BiFunction<T, A, B> function) {
        return switch (this) {
            case Nil nil -> Nil.INSTANCE;
            case Cons<T>(var head, var tail) -> switch (list) {
                case Nil nil -> Nil.INSTANCE;
                case Cons<A>(var head2, var tail2) -> new Cons<>(function.apply(head, head2), (tail.zipWith(tail2, function)));
            };
        };
    }
    // @formatter:on

    default List<T> filter(final Predicate<T> predicate) {
        return this.flatMap(a -> {
            if (predicate.test(a)) {
                return List.of(a);
            } else {
                return Nil.INSTANCE;
            }
        });
    }
}
