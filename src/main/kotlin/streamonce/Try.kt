package streamonce

sealed class Try<T> {
    data class Value<T>(val value: T) : Try<T>()
    data class Error<T>(val error: Exception) : Try<T>()

    fun <B> fold(onError: (Exception) -> B, onValue: (T) -> B): B =
        when (this) {
            is Value -> {
                onValue(this.value)
            }
            is Error -> {
                onError(this.error)
            }
        }

    fun getOrThrow(): T =
        fold({ throw it }, { it })

    fun <B> map(f: (T) -> B): Try<B> =
        try {
            Value(f(getOrThrow()))
        } catch (e: Exception) {
            Error(e)
        }

    fun <B> flatMap(f: (T) -> Try<B>): Try<B> =
        try {
            f(getOrThrow())
        } catch (e: Exception) {
            Error(e)
        }

    fun recover(f: (Exception) -> T): Try<T> =
        when (this) {
            is Value -> Value(this.value)
            is Error -> try {
                Value(f(error))
            } catch (e: Exception) {
                Error(e)
            }
        }

    fun recoverWith(f: (Exception) -> Try<T>): Try<T> =
        when (this) {
            is Value -> Value(this.value)
            is Error -> try {
                f(error)
            } catch (e: Exception) {
                Error(e)
            }
        }

}
