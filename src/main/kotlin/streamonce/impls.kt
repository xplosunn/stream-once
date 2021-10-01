package streamonce

import kotlin.streams.toList

sealed class Try<T> {
    data class Value<T>(val value: T): Try<T>()
    data class Error<T>(val error: Exception): Try<T>()

    fun getOrThrow(): T =
        when(this) {
         is Value -> {this.value}
         is Error -> {throw this.error}
        }

    fun <B> map(f: (T) -> B): Try<B> =
        try {
            Value(f(getOrThrow()))
        } catch (e: Exception) {
            Error(e)
        }

    fun recover(f: (Exception) -> T): Try<T> =
        when(this) {
            is Value -> Value(this.value)
            is Error -> try {
                Value(f(error))
            } catch (e: Exception) {
                Error(e)
            }
        }
}

data class UnboundedStreamOnceImpl<T>(private val javaStream: java.util.stream.Stream<Try<T>>): UnboundedStreamOnce<T> {
    override fun recover(f: (Exception) -> T): UnboundedStreamOnce<T> =
        UnboundedStreamOnceImpl(javaStream.map { it.recover(f) })

    override fun take(count: Long): BoundedStreamOnce<T> =
        BoundedStreamOnceImpl(javaStream.limit(count))

}

data class BoundedStreamOnceImpl<T>(private val javaStream: java.util.stream.Stream<Try<T>>): BoundedStreamOnce<T> {
    override fun <B> map(f: (T) -> B): BoundedStreamOnce<B> =
        BoundedStreamOnceImpl(javaStream.map { it.map(f) })

    override fun toList(): List<T> =
        javaStream.map { it.getOrThrow() }.toList()

    override fun findFirst(): T? =
        javaStream.findFirst().let {
            if (it.isPresent) it.get().getOrThrow() else null
        }

    override fun recover(f: (Exception) -> T): BoundedStreamOnce<T> =
        BoundedStreamOnceImpl(javaStream.map { it.recover(f) })

}