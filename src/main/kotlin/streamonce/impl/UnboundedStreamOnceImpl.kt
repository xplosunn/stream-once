package streamonce.impl

import streamonce.BoundedStreamOnce
import streamonce.StreamOnce
import streamonce.Try
import streamonce.UnboundedStreamOnce

class UnboundedStreamOnceImpl<T>(
    javaStream: java.util.stream.Stream<Try<T>>
) : StreamOnceImpl<T, UnboundedStreamOnce<T>>(javaStream, ::UnboundedStreamOnceImpl),
    UnboundedStreamOnce<T> {

    override fun <B> map(f: (T) -> B): UnboundedStreamOnce<B> =
        UnboundedStreamOnceImpl(javaStream.map {
            it.map(f)
        })

    override fun <B, S> flatMap(f: (T) -> StreamOnce<B, S>): UnboundedStreamOnce<B> =
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

    override fun <B> zip(stream: UnboundedStreamOnce<B>): UnboundedStreamOnce<Pair<T, B>> {
        val thisIterator = toThrowingIterator()
        return stream.takeWhile { thisIterator.hasNext() }.map { thisIterator.next() to it }
    }
}

