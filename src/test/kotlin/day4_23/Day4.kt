package day4_23

import java.io.File
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.pow

data class Card(val idx: Int, val numbers: Set<Int>, val winningNumbers: Set<Int>) {
    val dups = numbers.intersect(winningNumbers).count()

    val points = if (dups == 0) {
        0
    } else {
        2.0.pow((dups - 1).toDouble()).toInt()
    }
}

private fun unwrapCard(card: Card, deck: List<Card>): Int {
    return 1 + (card.idx + 1..card.idx + card.dups).sumOf { unwrapCard(deck[it], deck) };
}

class Day4 {
    @Test
    fun sampleSilver() {
        val input = readPuzzleInput("sample")
        val solution = input.sumOf { it.points }
        assertEquals(solution, 13)
    }


    @Test
    fun sampleGold() {
        val input = readPuzzleInput("sample")
        val solution = input.sumOf { unwrapCard(it, input) }
        assertEquals(solution, 30)
    }

    @Test
    fun silver() {
        val input = readPuzzleInput("input")
        val solution = input.sumOf { it.points }
        assertEquals(solution, 17782)
    }

    @Test
    fun gold() {
        val input = readPuzzleInput("input")
        val solution = input.sumOf { unwrapCard(it, input) }
        assertEquals(solution, 8477787)
    }
}

fun readPuzzleInput(filename: String): List<Card> {
    return File("src/test/kotlin/day4_23/$filename").readLines()
        .map { it.substringAfter(":").substringBefore("|") to it.substringAfter("|") }
        .mapIndexed { idx, it ->
            Card(
                idx, it.first.split(" ").mapNotNull { it.toIntOrNull() }.toSet(), it.second.split(" ")
                    .mapNotNull { it.toIntOrNull() }.toSet()
            )
        }
}
