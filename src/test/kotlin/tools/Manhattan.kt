package tools

import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class Manhattan {

  @Test
  fun manhattenRadius() {

    val inner = Point(0, 0)
    val points =
        pointInExactRadius(inner, 1).onEach { assertEquals(it.manhattanDistance(inner), 1) }
    assertEquals(4, points.size)

    val points2 =
        pointInExactRadius(inner, 2).onEach { assertEquals(it.manhattanDistance(inner), 2) }
    assertEquals(8, points2.size)
  }

  @Test
  fun manhattenRadiusInnerChanged() {

    val inner = Point(2, 2)
    val points =
        pointInExactRadius(inner, 1).onEach {
          println(it)
          assertEquals(it.manhattanDistance(inner), 1)
        }
    assertEquals(4, points.size)
  }
}

fun pointInExactRadius(point: Point, radius: Int): HashSet<Point> {
  val points = HashSet<Point>()

  for (x in -radius..radius) {
    val y1 = (radius - kotlin.math.abs(x))
    val y2 = (-1 * (radius - kotlin.math.abs(x)))

    points.add(Point(x + point.x, y1 + point.y, '#'))
    points.add(Point(x + point.x, y2 + point.y, '#'))
  }
  return points
}
