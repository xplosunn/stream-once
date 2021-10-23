package streamonce.impl

import streamonce.BoundedStreamOnce
import streamonce.StreamOnce
import streamonce.Try
import java.util.stream.Stream

open class StreamOnceImpl<T, SubStreamOnceT>(
    protected val javaStream: Stream<Try<T>>,
    val subConstructor: (Stream<Try<T>>) -> SubStreamOnceT
) : StreamOnce<T, SubStreamOnceT> {

    override fun findFirst(): T? =
        javaStream.findFirst().takeIf { it.isPresent }?.let { it.get().getOrThrow() }

    override fun peek(f: (T) -> Unit): SubStreamOnceT =
        subConstructor(javaStream.peek { it.map(f) })

    override fun filter(f: (T) -> Boolean): SubStreamOnceT =
        subConstructor(javaStream.filter {
            it.fold({ false }, f)
        })

    override fun dropWhile(f: (T) -> Boolean): SubStreamOnceT =
        subConstructor(javaStream.dropWhile {
            it.fold({ false }, f)
        })

    override fun drop(n: Long): SubStreamOnceT =
        subConstructor(javaStream.skip(n))

    override fun take(count: Long): BoundedStreamOnce<T> =
        BoundedStreamOnceImpl(javaStream.limit(count))

    override fun takeWhile(f: (T) -> Boolean): SubStreamOnceT =
        subConstructor(javaStream.takeWhile {
            it.fold({ false }, f)
        })

    override fun recover(f: (Exception) -> T): SubStreamOnceT =
        subConstructor(javaStream.map { it.recover(f) })

    override fun recoverWhenNotNull(f: (Exception) -> T?): SubStreamOnceT =
        subConstructor(javaStream.map { it.recoverWith { err -> f(err)?.let { t -> Try.Value(t) } ?: Try.Error(err) }})

    override fun toThrowingIterator(): Iterator<T> =
        javaStream.map { it.getOrThrow() }.iterator()

    override fun toTryIterator(): Iterator<Try<T>> =
        javaStream.iterator()

    override fun dropExceptions(): SubStreamOnceT =
        subConstructor(javaStream.filter {
            it.fold(onError = { false }, onValue = { true })
        })

    override fun toThrowingJavaStream(): Stream<T> =
        javaStream.map { it.getOrThrow() }

    override fun toTryJavaStream(): Stream<Try<T>> =
        javaStream

}