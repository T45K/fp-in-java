package io.github.t45k.fpinjava;

import java.util.function.Function;
import java.util.function.Predicate;

public sealed interface Option<A> extends Monad<A> permits Option.Some, Option.None {

    record Some<A>(A value) implements Option<A> {
    }

    final class None implements Option {
        private None() {
        }

        public static final None INSTANCE = new None();
    }

    static <B> Option<B> of(final B value) {
        return new Some<>(value);
    }

    // @formatter:off
    default <B> Option<B> flatMap(final Function<A, Option<B>> function) {
        return switch (this) {
            case None none -> None.INSTANCE;
            case Some<A>(var value) -> function.apply(value);
        };
    }
    // @formatter:on

    default <B> Option<B> map(final Function<A, B> function) {
        return this.flatMap(function.andThen(Option::of));
    }

    // @formatter:off
    default <B extends A> A getOrElse(final B alt) {
        return switch (this) {
            case None none -> alt;
            case Some<A>(var value) -> value;
        };
    }
    // @formatter:on

    default <B extends A> Option<? extends A> orElse(final Option<B> alt) {
        return switch (this) {
            case None none -> alt;
            case Some<A> some -> some;
        };
    }

    default Option<A> filter(final Predicate<A> predicate) {
        return this.flatMap(a -> {
            if (predicate.test(a)) {
                return Option.of(a);
            } else {
                return None.INSTANCE;
            }
        });
    }
}
