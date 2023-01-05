package day18

import java.io.File
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import tools.ThreeDimenionsalPoint.*

internal class Day18 {

  @Test
  fun sampleSilver() {
    val input = parsePoints("sample")
    val neighbors = input.map { it.directNeighbors() }.flatten()
    val freeSides = neighbors.count { !input.contains(it) }

    assertEquals(64, freeSides)
  }

  @Test
  fun sampleGold() {
    val input = parsePoints("sample")
    val finder = CaveFinder(input)

    val neighbors = input.map { it.directNeighbors() }.flatten().filter { !finder.check(it) }
    val freeSides = neighbors.count { !input.contains(it) }

    assertEquals(58, freeSides)
  }

  @Test
  fun silver() {
    val input = parsePoints("input")
    val neighbors = input.map { it.directNeighbors() }.flatten()
    val freeSides = neighbors.count { !input.contains(it) }

    assertEquals(4608, freeSides)
  }

  @Test
  fun gold() {
    val input = parsePoints("input")
    val finder = CaveFinder(input)

    val neighbors = input.map { it.directNeighbors() }.flatten().filter { !finder.check(it) }
    val freeSides = neighbors.count { !input.contains(it) }

    assertEquals(2652, freeSides)
  }
}

class CaveFinder(var map: List<Point>) {
  private var maxZ: Int = map.maxOf { it.z }
  private var minZ: Int = map.minOf { it.z }
  private var maxY: Int = map.maxOf { it.y }
  private var minY: Int = map.minOf { it.y }
  private var maxX: Int = map.maxOf { it.x }
  private var minX: Int = map.minOf { it.x }
  private var currentHistory = HashSet<Point>()
  private var cache = HashMap<Point, Boolean>()

  fun check(point: Point): Boolean {
    currentHistory.clear()
    val checked = enclosed(point)
    cache[point] = checked
    return checked
  }
  private fun enclosed(point: Point): Boolean {
    val neighbors = point.directNeighbors().filter { !currentHistory.contains(it) }

    for (neighbor in neighbors) {
      currentHistory.add(neighbor)

      if (map.contains(neighbor)) {
        continue
      }

      if (cache.containsKey(neighbor)) {
        return cache[neighbor]!!
      }

      if (neighbor.x <= minX || neighbor.x >= maxX) {
        cache[neighbor] = false
        return false
      }

      if (neighbor.y <= minY || neighbor.y >= maxY) {
        cache[neighbor] = false
        return false
      }

      if (neighbor.z <= minZ || neighbor.z >= maxZ) {
        cache[neighbor] = false
        return false
      }

      if (!enclosed(neighbor)) {
        cache[neighbor] = false
        return false
      }
    }
    return true
  }
}

fun parsePoints(filename: String): List<Point> {

  return File("src/test/kotlin/day18/$filename")
      .readLines()
      .map { it.split(",").take(3) }
      .map { Point(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
      .toList()
}
