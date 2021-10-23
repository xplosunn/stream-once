package streamonce.test.streamonce.test

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import streamonce.BoundedStreamOnce
import streamonce.UnboundedStreamOnce

class StreamOnceTests {

    @Test
    fun fromListToList() {
        val list = listOf(1, 2, 3)

        Assertions.assertEquals(
            list,
            BoundedStreamOnce.fromList(list).toList()
        )
    }

    @Test
    fun map() {
        val list = listOf(1, 2, 3)

        Assertions.assertEquals(
            list.map { it + 1},
            BoundedStreamOnce.fromList(list).map { it + 1 }.toList()
        )
    }

    @Test
    fun findFirst() {
        val list = listOf(1, 2, 3)

        Assertions.assertEquals(
            1,
            BoundedStreamOnce.fromList(list).findFirst()
        )
    }

    @Test
    fun repeatConstant() {
        Assertions.assertEquals(
            listOf(1, 1, 1),
            UnboundedStreamOnce.repeatConstant(1).take(3).toList()
        )
    }
}