package io.github.t45k.fpinjava;

import java.util.function.Function;
import java.util.function.UnaryOperator;

// S => (A, S)
public record State<S, A>(Function<S, Tuple2<A, S>> run) implements Monad<A> {
    public static <S, A> State<S, A> of(final A value) {
        return new State<>(s -> new Tuple2<>(value, s));
    }

    public <B> State<S, B> flatMap(final Function<A, State<S, B>> function) {
        return new State<>(s -> {
            final Tuple2<A, S> tuple = this.run().apply(s);
            return function.apply(tuple.first()).run().apply(tuple.second());
        });
    }

    public <B> State<S, B> map(final Function<A, B> function) {
        return this.flatMap(function.andThen(State::of));
    }

    public State<S, S> get() {
        return new State<>(s -> new Tuple2<>(s, s));
    }

    public State<S, Void> set(final S s) {
        return new State<>(unused -> new Tuple2<>(null, s));
    }

    public State<S, Void> modify(final UnaryOperator<S> function) {
        return this.get().flatMap(function.andThen(this::set));
    }
}
