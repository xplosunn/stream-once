package streamonce.example

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import streamonce.BoundedStreamOnce


class GettingStartedTests {

    @Test
    fun upToThree() {
        val expected = listOf(0L, 1L, 2L, 3L)

        val streamResult = BoundedStreamOnce.incrementingFromZero()
            .take(4)
            .toList()

        Assertions.assertEquals(expected, streamResult)
    }

    @Test
    fun fizzBuzz() {
        val expected =
            "1,2,Fizz,4,Buzz,Fizz,7,8,Fizz,Buzz,11,Fizz,13,14,FizzBuzz,16"

        val streamResult = BoundedStreamOnce.incrementingFromOne()
            .map { n ->
                if (n % 15 == 0L) "FizzBuzz"
                else if (n % 3 == 0L) "Fizz"
                else if (n % 5 == 0L) "Buzz"
                else n.toString()
            }
            .take(16)
            .reduce { a, b -> "$a,$b" }

        Assertions.assertEquals(expected, streamResult)
    }
}