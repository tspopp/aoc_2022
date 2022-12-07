package day6

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day6 {
    @Test
    fun sampleSilver() {
        val lines = read_puzzle_input("sample")
        assertEquals(7, findMessage(lines.first(), 4))
    }

    @Test
    fun sampleGold() {
        val lines = read_puzzle_input("sample")
        assertEquals(19, findMessage(lines.first(), 14))
    }

    @Test
    fun silver() {
        val lines = read_puzzle_input("input")
        assertEquals(1578, findMessage(lines.first(), 4))
    }

    @Test
    fun gold() {
        val lines = read_puzzle_input("input")
        assertEquals(2178, findMessage(lines.first(), 14))
    }
}

fun findMessage(line: String, length: Int): Int {
    return line
        .withIndex()
        .windowed(length, 1)
        .first { value ->
            val values = value.map { it.value }
                .toList()
                .sortedBy { it }
                .distinct()
            values.count() == length
        }.last().index + 1
}

fun read_puzzle_input(filename: String): List<String> {
    return File("src/test/kotlin/day6/$filename")
        .readLines()
}
