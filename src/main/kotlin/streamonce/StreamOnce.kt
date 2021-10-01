package streamonce


interface StreamOnce<T, SubStreamOnceT> {

    fun recover(f: (Exception) -> T): SubStreamOnceT
}