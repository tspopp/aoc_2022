package day17

import java.io.File
import java.lang.UnsupportedOperationException
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import tools.Direction
import tools.Map
import tools.Point

class Day17 {
  @Test
  fun silverSample() {
    val tetris =
        Tetris(
            read_puzzle_input("sample")
                .first()
                .windowed(1, 1)
                .map { it.toCharArray().first() }
                .toList())

    while (tetris.blocksInGame != 2023L) {
      tetris.playRound()
    }

    assertEquals(3068, tetris.settledMap.database.maxOf { it.y })
  }

  @Test
  fun silver() {
    val tetris =
        Tetris(
            read_puzzle_input("input")
                .first()
                .windowed(1, 1)
                .map { it.toCharArray().first() }
                .toList())

    while (tetris.blocksInGame != 2023L) {
      tetris.playRound()
    }

    assertEquals(3219, tetris.settledMap.database.maxOf { it.y })
  }

  @Test
  fun sampleGold() {
    val tetris =
        Tetris(
            read_puzzle_input("sample")
                .first()
                .windowed(1, 1)
                .map { it.toCharArray().first() }
                .toList())
    assertEquals(1514285714288, tetris.getHeightForNumOfBlocks(1000000000001L))
  }

  @Test
  fun gold() {
    val tetris =
        Tetris(
            read_puzzle_input("input")
                .first()
                .windowed(1, 1)
                .map { it.toCharArray().first() }
                .toList())
    assertEquals(1582758620701, tetris.getHeightForNumOfBlocks(1000000000001L))
  }
}

class Tetris(
    private var moves: List<Char>,
    private var cycleSearchPatternLength: Int = 32,
    private var shrinkMap: Boolean = false
) {

  private var cycleHeight: Int = 0
  private var round: Int = 0
  private var activeShape: Shape? = null
  private var cycleSequenceCollection = ArrayDeque<Pair<Int, Int>>()
  private var cycleSearchPattern = ArrayDeque<Int>()
  private var cycleEndBlock: Int = 0
  private var cycleStartBlock: Int = 0

  private var shapeSequenceCount: Int = 0

  private val shapeSequence =
      arrayOf(
          ShapeType.HorizontalLine,
          ShapeType.Plus,
          ShapeType.InvertedL,
          ShapeType.VerticalLine,
          ShapeType.Rectangle)
  val settledMap = Map()
  var blocksInGame: Long = 0

  init {
    settledMap.setBounds(x_min = 0, x_max = 6)
  }

  fun getHeightForNumOfBlocks(blocks: Long): Long {

    // play until we found the sequence
    while (cycleStartBlock == 0) {
      playRound()
    }

    val blocksPerCycle = cycleEndBlock - cycleStartBlock

    val numOfCycles = (blocks - cycleStartBlock) / blocksPerCycle

    val blocksEnd = blocks - (numOfCycles * blocksPerCycle + cycleStartBlock)

    val startHeight = cycleSequenceCollection[cycleStartBlock - 1].second

    val endHeight =
        cycleSequenceCollection[cycleStartBlock + blocksEnd.toInt()].second - startHeight - 1

    return startHeight + numOfCycles * cycleHeight + endHeight
  }

  fun playRound() {
    if (activeShape == null) {
      blocksInGame++
      val mapHeight = settledMap.database.maxOfOrNull { it.y } ?: 0
      val type = shapeSequence[shapeSequenceCount % shapeSequence.size]
      activeShape = Shape(type, mapHeight + 5)

      runCycleDetection(mapHeight, type)

      shapeSequenceCount++
    }

    activeShape?.moveWithCollisionDetection(Direction.Down, settledMap)

    activeShape?.moveWithCollisionDetection(
        if (moves[round % moves.size] == '<') {
          Direction.Left
        } else {
          Direction.Right
        },
        settledMap)

    if (activeShape!!.isSettled(settledMap)) {
      val shapeAsPoints = activeShape!!.points()
      settledMap.database.addAll(shapeAsPoints)

      if (shrinkMap) {
        shrinkMap(shapeAsPoints)
      }
      activeShape = null
    }

    round++
  }

  private fun runCycleDetection(mapHeight: Int, type: ShapeType) {
    if (cycleStartBlock == 0) {
      val thisCycleHeight = settledMap.database.filter { it.x == 2 }.maxOfOrNull { it.y } ?: 0
      val thisCycleValue = (mapHeight + 5 - thisCycleHeight).xor(type.ordinal)
      cycleSequenceCollection.add(thisCycleValue to thisCycleHeight)

      cycleSearchPattern.add(thisCycleValue)
      if (cycleSearchPattern.size > cycleSearchPatternLength) {
        cycleSearchPattern.removeFirst()
      }

      val duplicates =
          cycleSequenceCollection
              .windowed(cycleSearchPatternLength, 1)
              .withIndex()
              .filter { pair -> pair.value.map { it.first } == cycleSearchPattern }
              .toList()

      if (duplicates.size == 2) {
        cycleStartBlock = duplicates.first().index
        cycleEndBlock = duplicates.last().index
        cycleHeight =
            duplicates.last().value.first().second - duplicates.first().value.first().second
      }
    }
  }

  @Deprecated(message = "No longer needed, since we found cycles within the input params")
  private fun shrinkMap(points: List<Point>) {
    val shapeMinY = points.minOf { it.y }

    if (isCompleteLine(shapeMinY)) {
      settledMap.database.removeIf { it.y < shapeMinY }
    } else if (isTwoLayeredCompletedLine(shapeMinY)) {
      settledMap.database.removeIf { it.y < shapeMinY - 1 }
    } else {
      singleHolesInCurrentLine(shapeMinY)
    }
  }

  private fun singleHolesInCurrentLine(y: Int) {
    // reduce to single y line
    val values =
        (0..6).filter { x -> settledMap.database.contains(Point(x, y)) }.map { x -> Point(x, y) }

    if (values.count() != 6) {
      return
    }

    // find missing
    val xHole = (0..6).first { x -> !values.map { it.x }.contains(x) }
    for (depth in 1..4) {
      if (settledMap.database.contains(Point(xHole, y - depth))) {
        settledMap.database.removeIf { it.y < y - depth }
        break
      }
    }
  }

  private fun isCompleteLine(y: Int): Boolean {
    return settledMap.database.count { it.y == y } == 7
  }

  private fun isTwoLayeredCompletedLine(y: Int): Boolean {
    return settledMap.database
        .asSequence()
        .filter { it.y == y || it.y == (y - 1) }
        .map { it.x }
        .sorted()
        .distinct()
        .count() == 7
  }
}

enum class ShapeType {
  HorizontalLine,
  Plus,
  InvertedL,
  VerticalLine,
  Rectangle
}

class Shape(private val type: ShapeType, spawnY: Int, spawnX: Int = 2) {
  private var xPos = spawnX
  private var yPos = spawnY

  fun moveWithCollisionDetection(direction: Direction, map: Map) {
    val renderedShape = this.points()

    when (direction) {
      Direction.Right -> {
        if (renderedShape.maxOf { it.x } != 6) {
          val testPoints = Shape(this.type, yPos, xPos + 1).points()
          if (!testPoints.map { map.database.contains(it) }.any { it }) {
            xPos += 1
          }
        }
      }
      Direction.Left -> {
        if (renderedShape.minOf { it.x } != 0) {
          val testPoints = Shape(this.type, yPos, xPos - 1).points()
          if (!testPoints.map { map.database.contains(it) }.any { it }) {
            xPos += -1
          }
        }
      }
      Direction.Down -> {
        // first
        if (map.database.size == 0) {
          yPos += -1
        } else {
          val testPoints = Shape(this.type, yPos - 1, xPos).points()
          if (!testPoints.map { map.database.contains(it) }.any { it }) {
            yPos += -1
          }
        }
      }
      else -> throw UnsupportedOperationException()
    }
  }

  fun points(): List<Point> {
    val char = '#'

    // 0/0 is bottom left
    return when (type) {
      ShapeType.HorizontalLine -> {
        listOf(
            Point(xPos, yPos, char),
            Point(xPos + 1, yPos, char),
            Point(xPos + 2, yPos, char),
            Point(xPos + 3, yPos, char))
      }
      ShapeType.Plus -> {
        listOf(
            Point(xPos + 1, yPos, char),
            Point(xPos, yPos + 1, char),
            Point(xPos + 1, yPos + 1, char),
            Point(xPos + 2, yPos + 1, char),
            Point(xPos + 1, yPos + 2, char))
      }
      ShapeType.InvertedL -> {
        listOf(
            Point(xPos, yPos, char),
            Point(xPos + 1, yPos, char),
            Point(xPos + 2, yPos, char),
            Point(xPos + 2, yPos + 1, char),
            Point(xPos + 2, yPos + 2, char))
      }
      ShapeType.VerticalLine -> {
        listOf(
            Point(xPos, yPos, char),
            Point(xPos, yPos + 1, char),
            Point(xPos, yPos + 2, char),
            Point(xPos, yPos + 3, char),
        )
      }
      ShapeType.Rectangle -> {
        listOf(
            Point(xPos, yPos, char),
            Point(xPos + 1, yPos, char),
            Point(xPos, yPos + 1, char),
            Point(xPos + 1, yPos + 1, char))
      }
    }
  }

  fun isSettled(map: Map): Boolean {
    if (map.database.size == 0 && yPos == 1) {
      return true
    }

    return Shape(type, yPos - 1, xPos).points().map { map.database.contains(it) }.any { it }
  }
}

fun read_puzzle_input(filename: String): List<String> {
  return File("src/test/kotlin/day17/$filename").readLines()
}
