package day8

import java.io.File
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class Day8 {
  @Test
  fun sampleSilver() {
    val map = Forest.fromInput(read_puzzle_input("sample"))
    assertEquals(21, map.visibleTrees())
  }

  @Test
  fun silver() {
    val map = Forest.fromInput(read_puzzle_input("input"))
    assertEquals(1796, map.visibleTrees())
  }

  @Test
  fun sampleGold() {
    val map = Forest.fromInput(read_puzzle_input("sample"))
    assertEquals(8, map.highestSceneryScore())
  }

  @Test
  fun gold() {
    val map = Forest.fromInput(read_puzzle_input("input"))
    assertEquals(288120, map.highestSceneryScore())
  }
}

enum class Direction {
  LEFT,
  UP,
  DOWN,
  RIGHT
}

typealias Coordinate = Pair<Int, Int>

data class Forest(val database: Map<Coordinate, Int>) {
  companion object {
    fun fromInput(lines: List<String>): Forest {
      return Forest(
          lines
              .map { it.windowed(1, 1) }
              .flatMapIndexed { y: Int, row: List<String> ->
                row.mapIndexed { x, it -> Coordinate(x, y) to it.toInt() }
              }
              .toMap())
    }
  }
}

fun Forest.visibleTrees(): Int {
  return database
      .map { tree ->
        Direction.values()
            .map { visibilityAndScore(tree.key, getValueFromCoord(tree.key), it, 1).first }
            .count { it }
      }
      .count { it > 0 }
}

fun Forest.highestSceneryScore(): Int {
  return database.maxOf { tree ->
    Direction.values()
        .map { visibilityAndScore(tree.key, getValueFromCoord(tree.key), it, 1).second }
        .fold(1) { acc, value -> acc * value }
  }
}

const val HORIZON_VALUE = -1

private fun Forest.getValueFromCoord(coord: Coordinate): Int {
  return database.getOrDefault(coord, HORIZON_VALUE)
}

private fun Forest.visibilityAndScore(
    coord: Coordinate,
    start: Int,
    direction: Direction,
    score: Int
): Pair<Boolean, Int> {
  val (x, y) = coord
  val nextPoint =
      when (direction) {
        Direction.LEFT -> x - 1 to y
        Direction.UP -> x to y - 1
        Direction.DOWN -> x to y + 1
        Direction.RIGHT -> x + 1 to y
      }
  val valueNextPoint = getValueFromCoord(nextPoint)

  // we are not visible, shadowed by a tree
  if (valueNextPoint >= start) {
    return false to score
  }
  // we are visible, but reached the horizon
  else if (valueNextPoint == -1) {
    // reduce score by one, since this is the horizon and no tree
    return true to score - 1
  }
  // check the next point and pre-increment tree score
  return visibilityAndScore(nextPoint, start, direction, score + 1)
}

fun read_puzzle_input(filename: String): List<String> {
  return File("src/test/kotlin/day8/$filename").readLines()
}
