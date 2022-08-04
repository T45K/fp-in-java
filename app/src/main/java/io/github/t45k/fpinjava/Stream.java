package io.github.t45k.fpinjava;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Stream<A> extends Monad<A> permits Stream.Empty, Stream.Cons {
    final class Empty<A> implements Stream<A> {
        private Empty() {
        }

        public static final Empty INSTANCE = new Empty<>();
    }

    record Cons<A>(Lazy<A> head, Lazy<Stream<A>> tail) implements Stream<A> {
    }

    @FunctionalInterface
    interface Lazy<T> extends Supplier<T>, Monad<T> {
        default <T2> Lazy<T2> map(final Function<T, T2> function) {
            return () -> function.apply(this.get());
        }

        default <T2> Lazy<T2> flatMap(final Function<T, Lazy<T2>> function) {
            return function.apply(this.get());
        }
    }

    @SafeVarargs
    static <B> Stream<B> of(final B... bs) {
        return switch (bs.length) {
            case 0 -> Empty.INSTANCE;
            default -> new Cons<>(() -> bs[0], () -> of(Arrays.copyOfRange(bs, 1, bs.length)));
        };
    }

    @SafeVarargs
    static <B> Stream<B> of(final Lazy<B>... bs) {
        return switch (bs.length) {
            case 0 -> Empty.INSTANCE;
            default -> new Cons<>(bs[0], () -> of(Arrays.copyOfRange(bs, 1, bs.length)));
        };
    }

    // @formatter:off
    default List<A> toList() {
        return switch (this) {
            case Cons<A>(var head, var tail) -> new List.Cons<>(head.get(), tail.get().toList());
            case Empty<A> empty -> List.Nil.INSTANCE;
        };
    }
    // @formatter:on

    // @formatter:off
    // Maybe Current Java does not support fully guard clause i.e., case Cons && n > 0
    default Stream<A> take(final int n) {
        if(n == 0){
            return Empty.INSTANCE;
        }
        return switch (this) {
            case Cons<A>(var head, var tail) -> new Cons<>(head, tail.map(stream -> stream.take(n - 1)));
            default -> Empty.INSTANCE;
        };
    }
    // @formatter:on

    // @formatter:off
    default Stream<A> drop(final int n) {
        if(n == 0) {
            return this;
        }
        return switch (this) {
            case Cons<A>(var head, var tail) -> tail.get().drop(n - 1);
            case Empty<A> empty -> Empty.INSTANCE;
        };
    }
    // @formatter:on

    // @formatter:off
    default Stream<A> takeWhile(final Predicate<A> predicate) {
        return switch (this) {
            case Cons<A>(var head, var tail) -> predicate.test(head.get()) ? new Cons<>(head, tail.map(it -> it.takeWhile(predicate))) : Empty.INSTANCE;
            case Empty<A> empty -> Empty.INSTANCE;
        };
    }
    // @formatter:on

    // @formatter:off
    default <B> B foldRight(final B initial, final BiFunction<A, Lazy<B>, B> function) {
        return switch (this) {
            case Cons<A>(var head, var tail) -> function.apply(head.get(), tail.map(it -> it.foldRight(initial, function)));
            default -> initial;
        };
    }
    // @formatter:on

    default boolean any(final Predicate<A> predicate) {
        return this.foldRight(false, (a, b) -> predicate.test(a) || b.get());
    }

    default boolean all(final Predicate<A> predicate) {
        return this.foldRight(false, (a, b) -> predicate.test(a) && b.get());
    }

    // @formatter:off
    default <B, C> Stream<C> zipWith(final Stream<B> list, final BiFunction<A, B, C> function) {
        return switch (this) {
            case Empty<A> empty -> Empty.INSTANCE;
            case Cons<A>(var head, var tail) -> switch (list) {
                case Empty<B> empty -> Empty.INSTANCE;
                case Cons<B>(var head2, var tail2) ->
                    new Cons<>(
                        (Lazy<C>) For.yield2(head, head2, function),
                        (Lazy<Stream<C>>) For.yield2(tail, tail2, (Stream<A> a, Stream<B> b) -> a.zipWith(b, function))
                    );
            };
        };
    }
    // @formatter:on


    // @formatter:off
    static <A, B> boolean equals(final Stream<A> a, final Stream<B> b) {
        if(a == b) {
            return true;
        }
        return switch (a) {
            case Empty<A> empty -> false;
            case Cons<A>(var head, var tail) -> switch (b) {
                case Empty<B> empty -> false;
                case Cons<B>(var head2, var tail2) -> {
                    if(head.get() != head2.get()) {
                        yield false;
                    }
                    yield equals(tail.get(), tail2.get());
                }
            };
        };
    }
    // @formatter:on

    static <A, S> Stream<A> unfold(final S seed, final Function<S, Option<Tuple2<A, S>>> function) {
        return switch (function.apply(seed)) {
            case Option.Some<Tuple2<A, S>> some ->
                new Cons<>(() -> some.value().first(), () -> Stream.unfold(some.value().second(), function));
            case Option.None<?> none -> Empty.INSTANCE;
        };
    }

    default Stream<A> append(final Stream<A> stream) {
        return this.foldRight(stream, (a, acc) -> new Cons<>(() -> a, acc));
    }

    default <B> Stream<B> flatMap(final Function<A, Stream<B>> function) {
        return this.foldRight((Stream<B>) Empty.INSTANCE, (a, acc) -> function.apply(a).append(acc.get()));
    }

    default <B> Stream<B> map(final Function<A, B> function) {
        return this.flatMap(function.andThen(Stream::of));
    }
}
