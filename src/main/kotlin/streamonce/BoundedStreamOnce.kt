package streamonce

import streamonce.impl.BoundedStreamOnceImpl

interface BoundedStreamOnce<T>: StreamOnce<T, BoundedStreamOnce<T>> {

    fun toList(): List<T>

    fun reduce(combine: (T, T) -> T): T?

    fun <B> map(f: (T) -> B): BoundedStreamOnce<B>

    fun <B> flatMap(f: (T) -> BoundedStreamOnce<B>): BoundedStreamOnce<B>

    fun <B> flatMapUnbounded(f: (T) -> UnboundedStreamOnce<B>): UnboundedStreamOnce<B>

    fun <B> zip(stream: BoundedStreamOnce<B>): BoundedStreamOnce<Pair<T, B>>

    fun <B> zipWithIndex(): BoundedStreamOnce<Pair<T, Long>>

    fun <B> zip(stream: UnboundedStreamOnce<B>): BoundedStreamOnce<Pair<T, B>>

    companion object {

        fun <T> fromList(list: List<T>): BoundedStreamOnce<T> =
            BoundedStreamOnceImpl(list.stream().map { Try.Value(it) })

        fun incrementingFromZero(): BoundedStreamOnce<Long> =
            incrementingBetween(0L, Long.MAX_VALUE)

        fun incrementingFromOne(): BoundedStreamOnce<Long> =
            incrementingBetween(1L, Long.MAX_VALUE)

        fun incrementingBetween(startInclusive: Long, endExclusive: Long): BoundedStreamOnce<Long> =
            BoundedStreamOnceImpl(
                java.util.stream.Stream
                    .iterate(startInclusive, { it != endExclusive }, { it + 1 })
                    .map { Try.Value(it) }
            )

        fun linesFromFile(path: java.nio.file.Path): BoundedStreamOnce<String> =
            BoundedStreamOnceImpl(java.nio.file.Files.lines(path).map { Try.Value(it) })
    }
}