package streamonce

import streamonce.impl.UnboundedStreamOnceImpl
import java.util.stream.Stream


interface UnboundedStreamOnce<T>: StreamOnce<T, UnboundedStreamOnce<T>> {

    fun <B> map(f: (T) -> B): UnboundedStreamOnce<B>

    fun <B, S> flatMap(f: (T) -> StreamOnce<B, S>): UnboundedStreamOnce<B>

    fun <B> zip(stream: BoundedStreamOnce<B>): BoundedStreamOnce<Pair<T, B>>

    fun <B> zip(stream: UnboundedStreamOnce<B>): UnboundedStreamOnce<Pair<T, B>>

    companion object {

        fun <T> repeatList(list: List<T>): UnboundedStreamOnce<T> {
            if (list.isEmpty()) return UnboundedStreamOnceImpl(Stream.empty())

            var iterator = list.iterator()

            return repeatConstant(null).map {
                if (!iterator.hasNext()) {
                    iterator = list.iterator()
                }
                iterator.next()
            }
        }


        fun <T> repeatConstant(value: T): UnboundedStreamOnce<T> =
            UnboundedStreamOnceImpl(Stream.generate { value }.map { Try.Value(it) })
    }

}