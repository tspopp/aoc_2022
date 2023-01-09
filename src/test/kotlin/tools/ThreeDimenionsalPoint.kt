package tools

class ThreeDimenionsalPoint {

  data class Point(val x: Int, val y: Int, val z: Int) {

    fun directNeighbors(): List<Point> {
      val neighbors = mutableListOf<Point>()
      neighbors.add(Point(x + 1, y, z))
      neighbors.add(Point(x - 1, y, z))
      neighbors.add(Point(x, y + 1, z))
      neighbors.add(Point(x, y - 1, z))
      neighbors.add(Point(x, y, z + 1))
      neighbors.add(Point(x, y, z - 1))
      return neighbors
    }
  }
}
