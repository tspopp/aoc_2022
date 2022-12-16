package day15

import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import tools.Map
import tools.Point
import tools.pointInExactRadius

class Day15 {

  @Test
  fun sampleSilver() {
    val data = parseData("sample")

    val map = Map()
    val lineOfInterest = 10

    data.forEach {
      map.addPoint(it.first)
      map.addPoint(it.second)
      bruteManhattan(
          it.first, it.first.manhattanDistance(it.second), lineOfInterest, lineOfInterest, map)
    }

    map.render(invertY = true)
    assertEquals(26, map.database.count { it.y == lineOfInterest && it.render == '#' })
  }

  @Test
  fun silver() {
    val data = parseData("input")

    val map = Map()
    val lineOfInterest = 2000000

    data.forEach {
      map.addPoint(it.first)
      map.addPoint(it.second)
      bruteManhattan(
          it.first, it.first.manhattanDistance(it.second), lineOfInterest, lineOfInterest, map)
    }
    assertEquals(5181556, map.database.count { it.y == lineOfInterest && it.render == '#' })
  }

  @Test
  fun sampleGold() {
    val unreachablePoint = findUnreachablePoint(parseData("sample"), 20)
    assertNotNull(unreachablePoint)
    assertEquals(56000011, unreachablePoint.x * 4000000L + unreachablePoint.y)
  }

  @Test
  fun gold() {
    val unreachablePoint = findUnreachablePoint(parseData("input"), 4000000)
    assertNotNull(unreachablePoint)
    assertEquals(12817603219131, unreachablePoint.x * 4000000L + unreachablePoint.y)
  }

  // TODO: Refactor this mess
  private fun findUnreachablePoint(data: List<Pair<Point, Point>>, xyLimit: Int): Point? {
    var unreachablePoint1: Point? = null
    for ((sensor, beacon) in data) {
      val sensorRange = sensor.manhattanDistance(beacon)

      // determine all points which are "one away" from this sensor
      val unreachablePointOfThisSensor =
          pointInExactRadius(sensor, sensorRange.plus(1)).filter { p ->
            p.x in 0..xyLimit && p.y in 0..xyLimit
          }

      // check if one of these points, can't be located by the other sensors
      for (point in unreachablePointOfThisSensor) {

        var reached = false
        for ((sensorToCheck, beaconToCheck) in data) {
          if (sensor == sensorToCheck) {
            continue
          }

          if (sensorToCheck.manhattanDistance(beaconToCheck) >=
              sensorToCheck.manhattanDistance(point)) {
            reached = true
            break
          }
        }
        if (!reached) {
          unreachablePoint1 = Point(point.x, point.y)
          break
        }
      }

      if (unreachablePoint1 != null) {
        break
      }
    }
    return unreachablePoint1
  }
}

// TODO: Do not brute the valid manhatten distances, use maths :)
private fun bruteManhattan(point: Point, radius: Int, yMin: Int, yMax: Int, map: Map) {
  for (y in -radius..radius) {
    if (point.y - y in yMin..yMax) {
      for (x in -radius..radius) {
        val new = Point(point.x - x, point.y - y, '#')
        if (map.database.contains(new)) {
          continue
        }

        if (point.manhattanDistance(new) <= radius) {
          map.addPoint(new)
        }
      }
    }
  }
}

private fun parseData(filename: String): List<Pair<Point, Point>> {
  return File("src/test/kotlin/day15/$filename")
      .readLines()
      .map {
        val (sensor, beacon) = it.split(": ").map { it.substringAfter("at ").split(", ") }
        (sensor.first().removePrefix("x=").toInt() to sensor.last().removePrefix("y=").toInt()) to
            (beacon.first().removePrefix("x=").toInt() to beacon.last().removePrefix("y=").toInt())
      }
      .map { (sensor, beacon) ->
        Point(sensor.first, sensor.second, 'S') to Point(beacon.first, beacon.second, 'B')
      }
      .toList()
}
