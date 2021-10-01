package streamonce


interface UnboundedStreamOnce<T>: StreamOnce<T, UnboundedStreamOnce<T>> {

    fun take(count: Long): BoundedStreamOnce<T>

    companion object {
        fun <T> repeatConstant(value: T): UnboundedStreamOnce<T> =
            UnboundedStreamOnceImpl(java.util.stream.Stream.generate { value }.map { Try.Value(it) })
    }

}