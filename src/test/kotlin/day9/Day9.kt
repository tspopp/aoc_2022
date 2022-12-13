package day9

import java.io.File
import kotlin.collections.HashSet
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import tools.Direction
import tools.Point
import tools.movePoint

class Day9 {
  @Test
  fun sampleSilver() {
    val turns: List<Pair<Direction, Int>> = read_puzzle_input("sample")
    val playfield = Playfield(2)
    turns.forEach { playfield.playTurn(it) }

    assertEquals(13, playfield.seenByTail())
  }

  @Test
  fun silver() {
    val turns: List<Pair<Direction, Int>> = read_puzzle_input("input")
    val playfield = Playfield(2)
    turns.forEach { playfield.playTurn(it) }

    assertEquals(5710, playfield.seenByTail())
  }

  @Test
  fun sampleGold() {
    val turns: List<Pair<Direction, Int>> = read_puzzle_input("sample")
    val playfield = Playfield(10)
    turns.forEach { playfield.playTurn(it) }

    assertEquals(1, playfield.seenByTail())
  }

  @Test
  fun sampleGoldLarge() {
    val turns: List<Pair<Direction, Int>> = read_puzzle_input("sample2")
    val playfield = Playfield(10)
    turns.forEach { playfield.playTurn(it) }

    assertEquals(36, playfield.seenByTail())
  }

  @Test
  fun gold() {
    val turns: List<Pair<Direction, Int>> = read_puzzle_input("input")
    val playfield = Playfield(10)
    turns.forEach { playfield.playTurn(it) }

    assertEquals(2259, playfield.seenByTail())
  }
}

class SnakeElem(child: SnakeElem?, isHead: Boolean = false) {
  var mIsHead: Boolean = isHead

  // TODO: Instead of tracking childs on our own, we might want to use a data structure which does
  // it for free?
  var mChild: SnakeElem? = child
  var mPosition: Point = 0 to 0

  fun isTail(): Boolean {
    return mChild == null
  }
}

class Playfield(snakeLength: Int) {
  private val snake: ArrayList<SnakeElem> = ArrayList()
  private var seen: HashSet<Point> = hashSetOf(0 to 0)

  init {
    // TODO: Well this doesn't look very nice
    (0..snakeLength).forEach {
      snake.add(SnakeElem(snake.getOrNull(it - 1), snakeLength == (it + 1)))
    }
  }

  fun playTurn(turn: Pair<Direction, Int>) {
    val (direction, range) = turn

    repeat(range) {
      // find head of snake
      // TODO: Well, there must be a better way
      val head = snake.first { it.mIsHead }

      // modify position of head
      // TODO: Would be nice if movePoint mutates mPosition?
      head.mPosition = head.mPosition.movePoint(direction)

      syncTailWithHead(head)
    }
  }

  private fun syncTailWithHead(parent: SnakeElem) {
    val child = parent.mChild!!

    val (xParent, yParent) = parent.mPosition
    val (xChild, yChild) = child.mPosition

    // TODO: We might want to go for something simpler. No one really needs to know about distance
    // sqrt(2) :)
    if (sqrt((xChild - xParent).toDouble().pow(2) + (yChild - yParent).toDouble().pow(2)) < 2.0) {
      return
    }

    val xChildNew =
        if (xParent > xChild) {
          xChild + 1
        } else if (xParent < xChild) {
          xChild - 1
        } else {
          xChild
        }

    val yChildNew =
        if (yParent > yChild) {
          yChild + 1
        } else if (yParent < yChild) {
          yChild - 1
        } else {
          yChild
        }

    child.mPosition = xChildNew to yChildNew

    if (child.isTail()) {
      seen.add(child.mPosition)
    } else {
      syncTailWithHead(child)
    }
  }

  fun seenByTail(): Int {
    return seen.count()
  }
}

fun parseDirection(identifier: String): Direction {
  return when (identifier) {
    "R" -> Direction.Right
    "U" -> Direction.Up
    "L" -> Direction.Left
    "D" -> Direction.Down
    else -> {
      throw UnsupportedOperationException("not a valid identifier $identifier")
    }
  }
}

fun read_puzzle_input(filename: String): List<Pair<Direction, Int>> {
  return File("src/test/kotlin/day9/$filename").readLines().map {
    val split = it.split(" ")
    parseDirection(split.first()) to split.last().toInt()
  }
}
