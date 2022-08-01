package io.github.t45k.fpinjava;


/**
 * We cannot declare any methods to be implemented in derived classes due to poor type system of Java.
 * They must have the following methods.
 * <br>
 * <p>
 * {@snippet :
 * import java.util.function.Function;
 * Derived<A> of(A a);
 * <B> Derived<B> flatMap(Function<A,Derived<B>> function);
 *}
 **/
public interface Monad<A> {
}
