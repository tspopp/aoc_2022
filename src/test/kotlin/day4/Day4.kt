package day4

import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Integer.*
import kotlin.test.assertEquals

class Day4 {

    @Test
    fun sampleSilver() {
        val solution = readPuzzleInput("sample")
            .map { createPairOfCollections(it) }
            .count { it.first.containsAll(it.second) || it.second.containsAll(it.first) }

        assertEquals(solution, 2)

    }

    @Test
    fun sampleGold() {
        val solution = readPuzzleInput("sample")
            .map { createPairOfCollections(it) }
            .map { it.first.intersect(it.second.toSet()) }
            .count { it.isNotEmpty() }

        assertEquals(solution, 4)

    }

    @Test
    fun silver() {
        val solution = readPuzzleInput("input")
            .map { createPairOfCollections(it) }
            .count { it.first.containsAll(it.second) || it.second.containsAll(it.first) }

        assertEquals(solution, 562)
    }

    @Test
    fun gold() {
        val solution =
            readPuzzleInput("input")
                .map { createPairOfCollections(it) }
                .map { it.first.intersect(it.second.toSet()) }
                .count { it.isNotEmpty() }

        assertEquals(solution, 924)
    }
}

fun createPairOfCollections(line: String): Pair<List<Int>, List<Int>> {
    return Pair(
        createCollection(line.substringBefore(',')),
        createCollection(line.substringAfter(','))
    )
}

fun createCollection(segment: String): List<Int> {
    return (parseInt(segment.substringBefore('-'))..parseInt(segment.substringAfter('-'))).toList()
}


fun readPuzzleInput(filename: String): List<String> {
    return File(
        "src/test/kotlin/day4" +
                "/$filename"
    )
        .readLines()
}
