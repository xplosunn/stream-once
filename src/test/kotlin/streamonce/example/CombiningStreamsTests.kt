package streamonce.example

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import streamonce.BoundedStreamOnce
import streamonce.UnboundedStreamOnce


class CombiningStreamsTests {

    @Test
    fun allWallClockTimes() {
        data class WallClockTime(val hour: Int, val minute: Int)

        val expected = mutableListOf<WallClockTime>()

        fun hourByIndex(n: Int): Int = if (n == 0) 12 else n

        for (hour in 0..11) {
            for (minute in 0..59) {
                expected.add(WallClockTime(hourByIndex(hour), minute))
            }
        }

        val streamResult = BoundedStreamOnce.incrementingBetween(0, 12)
            .map { it.toInt() }
            .map(::hourByIndex)
            .flatMap { hour ->
                BoundedStreamOnce.incrementingBetween(0, 60)
                    .map { minute ->
                        WallClockTime(hour, minute.toInt())
                    }
            }
            .toList()

        Assertions.assertEquals(expected, streamResult)

        val streamResult2 = UnboundedStreamOnce.repeatList((0..11).toList())
            .map(::hourByIndex)
            .flatMap { UnboundedStreamOnce.repeatConstant(it).take(60) }
            .zip(
                UnboundedStreamOnce.repeatList((0..59).toList())
            )
            .take(12 * 60)
            .map { (hour, minute) -> WallClockTime(hour, minute) }
            .toList()

        Assertions.assertEquals(expected, streamResult2)
    }
}