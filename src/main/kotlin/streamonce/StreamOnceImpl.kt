package streamonce

import kotlin.streams.toList


data class StreamOnceImpl<T>(private val javaStream: java.util.stream.Stream<T>): BoundedStreamOnce<T>, UnboundedStreamOnce<T> {
    override fun <B> map(f: (T) -> B): BoundedStreamOnce<B> =
        StreamOnceImpl(javaStream.map(f))

    override fun toList(): List<T> =
        javaStream.toList()

    override fun findFirst(): T? =
        javaStream.findFirst().let {
            if (it.isPresent) it.get() else null
        }

    override fun take(count: Long): BoundedStreamOnce<T> =
        StreamOnceImpl(javaStream.limit(count))
}