package streamonce


interface UnboundedStreamOnce<T> {

    fun take(count: Long): BoundedStreamOnce<T>

    companion object {
        fun <T> repeatConstant(value: T): UnboundedStreamOnce<T> =
            StreamOnceImpl(java.util.stream.Stream.generate { value })
    }

}