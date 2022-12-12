package day2

import java.io.File
import java.lang.RuntimeException
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day2 {

    @Test
    fun sampleSilver() {
        val input = read_puzzle_input("sample_silver")
        val total = rounds(input).sumOf { it.score() }
        assertEquals(total, 15)
    }

    @Test
    fun sampleGold() {
        val input = read_puzzle_input("sample_silver")
        val total = rounds(input).sumOf { it.cheat().score() }
        assertEquals(total, 12)
    }

    @Test
    fun silver() {
        val input = read_puzzle_input("input_silver")
        val total = rounds(input).sumOf { it.score() }
        assertEquals(total, 14297)
    }

    @Test
    fun gold() {
        val input = read_puzzle_input("input_silver")
        val total = rounds(input).sumOf { it.cheat().score() }
        assertEquals(total, 10498)
    }
}

enum class Shape(val score: Int, val defeatedByOrdinal: Int, val beatsOrdinal: Int) {
    ROCK(1, 1, 2),
    PAPER(2, 2, 0),
    SCISSORS(3, 0, 1);

    fun play(shape: Shape): Int {
        if (shape == this) {
            return 3
        } else if (shape == values()[defeatedByOrdinal]) {
            return 0
        }
        return 6
    }

    companion object {
        fun fromChar(c: Char): Shape {
            return when (c) {
                'A' -> ROCK
                'B' -> PAPER
                'C' -> SCISSORS
                'X' -> ROCK
                'Y' -> PAPER
                'Z' -> SCISSORS
                else -> {
                    throw RuntimeException("invalid char")
                }
            }
        }
    }
}

data class Round(val victimMove: Shape, var playerMove: Shape) {
    companion object {
        fun fromList(list: List<String>): Round {
            return Round(Shape.fromChar(list.first().first()), Shape.fromChar(list.last().first()))
        }
    }
}

fun Round.cheat(): Round {
    this.playerMove =
        when (this.playerMove) {
            // NEED TO LOSE
            Shape.ROCK -> {
                Shape.values()[this.victimMove.beatsOrdinal]
            }
            // DRAW
            Shape.PAPER -> {
                this.victimMove
            }
            // NEED TO WIN
            Shape.SCISSORS -> {
                Shape.values()[this.victimMove.defeatedByOrdinal]
            }
        }
    return this
}

fun Round.score(): Int {
    return playerMove.play(victimMove) + this.playerMove.score
}

fun rounds(raw: String): List<Round> {
    return raw.split("\n").map { Round.fromList(it.split(" ")) }
}

fun read_puzzle_input(filename: String): String {
    return File("src/test/kotlin/day2/$filename").readText(Charsets.UTF_8)
}
