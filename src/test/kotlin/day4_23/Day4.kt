package day4_23

import java.io.File
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.pow

data class Card(val idx: Int, val cards: Set<Int>, val winningCards: Set<Int>) {
    val dups = getDuplicates()
    val points = getPoints()
}

private fun Card.getDuplicates(): Int {
    return cards.intersect(winningCards).count()
}

private fun Card.getPoints(): Int {
    if (getDuplicates() == 0) {
        return 0
    }
    return 2.0.pow((getDuplicates() - 1).toDouble()).toInt()
}

private fun unwrapCard(card: Card, deck: List<Card>): Int {
    var counter = 1
    val range = card.idx + 1..card.idx + card.dups
    for (i in range) {
        counter += unwrapCard(deck[i], deck)
    }
    return counter
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
