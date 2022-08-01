package io.github.t45k.fpinjava;

import java.util.function.Function;

public sealed interface Either<E, A> extends Monad<A> permits Either.Left, Either.Right {
    record Left<E, A>(E value) implements Either<E, A> {
    }

    record Right<E, A>(A value) implements Either<E, A> {
    }

    // @formatter:off
    default <B> Either<E, B> flatMap(final Function<A, Either<E, B>> function) {
        return switch (this) {
            case Right<E, A>(var value) -> function.apply(value);
            case Left left -> left;
        };
    }
    // @formatter:on

    default <B extends A> Either<E, ? extends A> orElse(final Either<E, B> alt) {
        return switch (this) {
            case Right<E, A> right -> right;
            case Left<E, A> left -> alt;
        };
    }
}
