package streamonce.example

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import streamonce.BoundedStreamOnce
import java.lang.RuntimeException


class ErrorHandlingTests {

    @Test
    fun dropExceptions() {
        val expected = listOf(0L, 1L, 2L, 3L)

        val streamResult = BoundedStreamOnce.incrementingBetween(0, 10)
            .map { if (it > 3) throw RuntimeException("unexpected") else it }
            .dropExceptions()
            .toList()

        Assertions.assertEquals(expected, streamResult)
    }

    @Test
    fun recoverPairs() {
        val expected = listOf(0L, 1L, 2L, 3L, 4L, 6L, 8L)

        val streamResult = BoundedStreamOnce.incrementingBetween(0, 10)
            .map { if (it > 3) throw RuntimeException("$it") else it }
            .recoverWhenNotNull { it.message?.toLong()?.takeIf { it % 2 == 0L } }
            .dropExceptions()
            .toList()

        Assertions.assertEquals(expected, streamResult)
    }

    @Test
    fun recoverAll() {
        val expected = listOf(0L, 1L, 2L, 3L)

        val streamResult = BoundedStreamOnce.incrementingBetween(0, 4)
            .map { if (it > 1) throw RuntimeException("$it") else it }
            .recover { it.message!!.toLong() }
            .dropExceptions()
            .toList()

        Assertions.assertEquals(expected, streamResult)
    }
}