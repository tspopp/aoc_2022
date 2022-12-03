package day3

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day3 {
    @Test
    fun sampleSilver() {
        val lines = read_puzzle_input("sample_silver")
        val solution = silver(lines)
        assertEquals(solution, 157)
    }

    @Test
    fun sampleGold() {
        val lines = read_puzzle_input("sample_silver")
        val solution = gold(lines)
        assertEquals(solution, 70)
    }

    @Test
    fun silver() {
        val lines = read_puzzle_input("input_silver")
        val solution = silver(lines)
        assertEquals(solution, 7763)
    }

    @Test
    fun gold() {
        val lines = read_puzzle_input("input_silver")
        val solution = gold(lines)
        assertEquals(solution, 2569)
    }
}

var lookup = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

fun silver(lines: List<String>): Int {
    return lines.map { line -> Pair(line.substring(0, line.length / 2), line.substring(line.length / 2)) }
        .map { pair ->
            pair.first.toCharArray().filter { pair.second.toCharArray().contains(it) }.distinct()
        }
        .flatten()
        .map {
            lookup.indexOf(it) + 1
        }
        .sum();
}

fun gold(lines: List<String>): Int {
    return lines.windowed(3, 3).map { group ->
        group[0].toCharArray().filter { char -> group[1].contains(char) && group[2].contains(char) }.distinct()
    }
        .flatten()
        .map {
            lookup.indexOf(it) + 1
        }
        .sum()
}

fun read_puzzle_input(filename: String): List<String> {
    return File("src/test/kotlin/day3/$filename")
        .readLines()
}
