package streamonce


interface BoundedStreamOnce<T>: StreamOnce<T, BoundedStreamOnce<T>> {

    fun <B> map(f: (T) -> B): BoundedStreamOnce<B>

    fun toList(): List<T>

    fun findFirst(): T?

    companion object {
        fun <T> fromList(list: List<T>): BoundedStreamOnce<T> =
            BoundedStreamOnceImpl(list.stream().map { Try.Value(it) })
    }
}