package io.github.t45k.fpinjava;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public interface For {

    static <A, B> Monad<B> yield1(final Monad<A> monadA,
                                  final Function<A, B> function) {
        try {
            return (Monad<B>) monadA.getClass()
                .getMethod("map", Function.class)
                .invoke(monadA, function);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <A, B, C> Monad<C> yield2(final Monad<A> monadA,
                                     final Monad<B> monadB,
                                     final BiFunction<A, B, C> function) {
        try {
            return (Monad<C>) monadA.getClass()
                .getMethod("flatMap", Function.class)
                .invoke(monadA, shelve((A a) -> yield1(monadB, b -> function.apply(a, b))));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <A, B, C, D> Monad<D> yield3(final Monad<A> monadA,
                                        final Monad<B> monadB,
                                        final Monad<C> monadC,
                                        final TriFunction<A, B, C, D> function) {
        try {
            return (Monad<D>) monadA.getClass()
                .getMethod("flatMap", Function.class)
                .invoke(monadA, shelve((A a) -> yield2(monadB, monadC, (b, c) -> function.apply(a, b, c))));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <A, B> Function<A, B> shelve(final ThrowingFunction<A, B> function) {
        return a -> {
            try {
                return function.apply(a);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    interface ThrowingFunction<A, B> {
        B apply(final A a) throws Exception;
    }

    @FunctionalInterface
    interface TriFunction<A, B, C, D> {
        D apply(final A a, final B b, final C c);
    }
}
