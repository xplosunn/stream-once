package streamonce.impl

import streamonce.BoundedStreamOnce
import streamonce.Try
import streamonce.UnboundedStreamOnce
import kotlin.streams.toList

class BoundedStreamOnceImpl<T>(
    javaStream: java.util.stream.Stream<Try<T>>
) : StreamOnceImpl<T, BoundedStreamOnce<T>>(javaStream, ::BoundedStreamOnceImpl),
    BoundedStreamOnce<T> {

    override fun toList(): List<T> =
        toThrowingJavaStream().toList()

    override fun reduce(combine: (T, T) -> T): T? =
        toThrowingJavaStream().reduce(combine).takeIf { it.isPresent }?.get()

    override fun <B> map(f: (T) -> B): BoundedStreamOnce<B> =
        BoundedStreamOnceImpl(javaStream.map {
            it.map(f)
        })

    override fun <B> flatMap(f: (T) -> BoundedStreamOnce<B>): BoundedStreamOnce<B> =
        BoundedStreamOnceImpl(javaStream.flatMap {
            it.fold(
                onError = { java.util.stream.Stream.of( Try.Error(it)) },
                onValue = { f(it).toTryJavaStream() }
            )
        })

    override fun <B> flatMapUnbounded(f: (T) -> UnboundedStreamOnce<B>): UnboundedStreamOnce<B> =
        UnboundedStreamOnceImpl(javaStream.flatMap {
            it.fold(
                onError = { java.util.stream.Stream.of( Try.Error(it)) },
                onValue = { f(it).toTryJavaStream() }
            )
        })

    override fun <B> zip(stream: BoundedStreamOnce<B>): BoundedStreamOnce<Pair<T, B>> {
        val thisIterator = toThrowingIterator()
        return stream.takeWhile { thisIterator.hasNext() }.map { thisIterator.next() to it }
    }

    override fun <B> zipWithIndex(): BoundedStreamOnce<Pair<T, Long>> =
        zip(BoundedStreamOnce.incrementingFromZero())

    override fun <B> zip(stream: UnboundedStreamOnce<B>): BoundedStreamOnce<Pair<T, B>> {
        val streamIterator = stream.toThrowingIterator()
        return takeWhile { streamIterator.hasNext() }.map { it to streamIterator.next() }
    }

}