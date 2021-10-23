package streamonce


interface StreamOnce<T, SubStreamOnceT> {

    fun findFirst(): T?

    fun peek(f: (T) -> Unit): SubStreamOnceT

    fun filter(f: (T) -> Boolean): SubStreamOnceT

    fun dropExceptions(): SubStreamOnceT

    fun dropWhile(f: (T) -> Boolean): SubStreamOnceT

    fun drop(n: Long): SubStreamOnceT

    fun take(count: Long): BoundedStreamOnce<T>

    fun takeWhile(f: (T) -> Boolean): SubStreamOnceT

    fun recover(f: (Exception) -> T): SubStreamOnceT

    fun recoverWhenNotNull(f: (Exception) -> T?): SubStreamOnceT

    fun toThrowingIterator(): Iterator<T>

    fun toTryIterator(): Iterator<Try<T>>

    fun toThrowingJavaStream(): java.util.stream.Stream<T>

    fun toTryJavaStream(): java.util.stream.Stream<Try<T>>
}