package day4

import java.io.File
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class Day4 {

  @Test
  fun sampleSilver() {
    val solution =
        readPuzzleInput("sample")
            .map { createPairOfCollections(it) }
            .count { (left, right) -> left.containsAll(right) || right.containsAll(left) }

    assertEquals(solution, 2)
  }

  @Test
  fun sampleGold() {
    val solution =
        readPuzzleInput("sample")
            .map { createPairOfCollections(it) }
            .map { (left, right) -> left.intersect(right.toSet()) }
            .count { it.isNotEmpty() }

    assertEquals(solution, 4)
  }

  @Test
  fun silver() {
    val solution =
        readPuzzleInput("input")
            .map { createPairOfCollections(it) }
            .count { (left, right) -> left.containsAll(right) || right.containsAll(left) }

    assertEquals(solution, 562)
  }

  @Test
  fun gold() {
    val solution =
        readPuzzleInput("input")
            .map { createPairOfCollections(it) }
            .map { (left, right) -> left.intersect(right.toSet()) }
            .count { it.isNotEmpty() }

    assertEquals(solution, 924)
  }
}

fun createPairOfCollections(line: String): Pair<List<Int>, List<Int>> {
  val (leftStart, leftEnd, rightStart, rightEnd) = line.split(",", "-")
  return (leftStart.toInt()..leftEnd.toInt()).toList() to
      (rightStart.toInt()..rightEnd.toInt()).toList()
}

fun readPuzzleInput(filename: String): List<String> {
  return File("src/test/kotlin/day4" + "/$filename").readLines()
}
