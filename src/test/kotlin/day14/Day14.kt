package day14

import java.io.File
import java.util.LinkedList
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import tools.Direction
import tools.Line
import tools.Map
import tools.Point

class Day14 {

  @Test
  fun sampleSilver() {
    val map = generateCave("sample")
    val sand = LinkedList<Sand>()
    flowWithSandSiler(map, sand)

    map.render(invertY = true)
    assertEquals(24, sand.count { !it.stillMoving })
  }

  @Test
  fun silver() {
    val map = generateCave("input")
    val sand = LinkedList<Sand>()
    flowWithSandSiler(map, sand)

    map.render(invertY = true)
    assertEquals(793, sand.count { !it.stillMoving })
  }

  @Test
  fun sampleGold() {
    val map = generateCave("sample")
    val sand = LinkedList<Sand>()
    flowWithSandGold(sand, map)

    map.render(invertY = true)
    assertEquals(24166, sand.count { !it.stillMoving })
  }

  @Test
  fun gold() {
    val map = generateCave("input")
    val sand = LinkedList<Sand>()
    flowWithSandGold(sand, map)

    map.render(invertY = true)
    assertEquals(24166, sand.count { !it.stillMoving })
  }

  private fun flowWithSandGold(sand: LinkedList<Sand>, map: Map) {
    val yMax = map.database.maxOf { it.y }
    while (true) {
      sand.reversed().forEach {
        map.removePoint(it.point)
        while (it.stillMoving) {
          it.move(map)
        }
        map.addPoint(it.point)
      }

      if (sand.isEmpty() || sand.none { it.stillMoving }) {
        if (map.database.contains(Point(500, 0))) {
          break
        }
        val new = Sand(Point(500, 0, 'o'), yMax + 2)
        map.addPoint(new.point)
        sand.push(new)
      }
    }
  }

  private fun flowWithSandSiler(map: Map, sand: LinkedList<Sand>) {
    val yMaxStart = map.database.maxOf { it.y }
    while (yMaxStart == map.database.maxOf { it.y }) {
      sand.reversed().forEach {
        map.removePoint(it.point)
        it.move(map)
        map.addPoint(it.point)
      }

      if (sand.isEmpty() || sand.none { it.stillMoving }) {
        val new = Sand(Point(500, 0, 'o'))
        map.addPoint(new.point)
        sand.push(new)
      }
    }
  }

  private fun generateCave(filename: String): Map {
    val list: List<Line> =
        File("src/test/kotlin/day14/$filename")
            .readLines()
            .asSequence()
            .map { it.split(" -> ").windowed(2, 1) }
            .flatten()
            .map {
              val start = it.first().split(",")
              val end = it.last().split(",")
              (Point(start.first().toInt(), start.last().toInt())) to
                  (Point(end.first().toInt(), end.last().toInt()))
            }
            .map { Line(it.first, it.second) }
            .toList()

    val map = Map()
    list.forEach { map.addLine(it) }
    return map
  }
}

class Sand(var point: Point, var y_limit: Int? = null) {
  var stillMoving = true

  fun move(map: Map) {
    if (y_limit != null && point.y.plus(1) == y_limit) {
      stillMoving = false
      return
    }
    if (!map.isOccupied(point, Direction.Up)) {
      point = point.move(Direction.Up)
    } else if (!map.isOccupied(point, Direction.UpLeft)) {
      point = point.move(Direction.UpLeft)
    } else if (!map.isOccupied(point, Direction.UpRight)) {
      point = point.move(Direction.UpRight)
    } else {
      stillMoving = false
    }
  }
}
