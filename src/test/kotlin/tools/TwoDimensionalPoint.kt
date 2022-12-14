package tools

data class Point(val x: Int, val y: Int, var render: Char = 'x') {

  fun directNeighbors(): List<Point> {
    return Direction.values()
        .filter {
          it != Direction.DownRight &&
              it != Direction.UpRight &&
              it != Direction.UpLeft &&
              it != Direction.DownLeft
        }
        .map { this.move(it) }
        .filter { it.x >= 0 && it.y >= 0 }
        .toList()
  }

  fun move(direction: Direction): Point {
    return when (direction) {
      Direction.Right -> Point(this.x + 1, this.y, render)
      Direction.Up -> Point(this.x, this.y + 1, render)
      Direction.Left -> Point(this.x - 1, this.y, render)
      Direction.Down -> Point(this.x, this.y - 1, render)
      Direction.DownLeft -> Point(x - 1, y - 1, render)
      Direction.DownRight -> Point(x + 1, y - 1, render)
      Direction.UpLeft -> Point(x - 1, y + 1, render)
      Direction.UpRight -> Point(x + 1, y + 1, render)
    }
  }

  fun render(): Char {
    return render
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Point

    if (x != other.x) return false
    if (y != other.y) return false

    return true
  }

  override fun hashCode(): Int {
    var result = x
    result = 31 * result + y
    return result
  }
}

enum class Direction {
  Right,
  Up,
  Left,
  Down,
  DownRight,
  DownLeft,
  UpRight,
  UpLeft
}

class Line(private val from: Point, val to: Point) {
  fun points(): List<Point> {
    val points = mutableListOf<Point>()
    // x-axis is stable
    if (from.x == to.x) {
      if (from.y < to.y) {
        for (y in from.y..to.y) {
          points.add(Point(from.x, y, '#'))
        }
      } else if (from.y > to.y) {
        for (y in to.y..from.y) {
          points.add(Point(from.x, y, '#'))
        }
      } else {
        points.add(Point(from.x, from.y, '#'))
      }
    }
    // on y-axis
    else if (from.y == to.y) {
      if (from.x < to.x) {
        for (x in from.x..to.x) {
          points.add(Point(x, from.y, '#'))
        }
      } else if (from.x > to.x) {
        for (x in to.x..from.x) {
          points.add(Point(x, from.y, '#'))
        }
      } else {
        points.add(Point(from.x, from.y, '#'))
      }
    }

    return points
  }
}

class Map {
  private var xMin: Int? = null
  private var xMax: Int? = null
  private var yMin: Int? = null
  private var yMax: Int? = null
  var database = HashSet<Point>()

  fun setBounds(x_min: Int? = null, x_max: Int? = null, y_min: Int? = null, y_max: Int? = null) {
    this.xMin = x_min
    this.xMax = x_max
    this.yMin = y_min
    this.yMax = y_max
  }

  fun addPoint(point: Point) {
    database.add(point)
  }

  fun removePoint(point: Point) {
    database.remove(point)
  }

  fun isOccupied(point: Point, direction: Direction): Boolean {
    val checkPosition = point.move(direction)
    return database.contains(checkPosition)
  }

  fun addLine(line: Line) {
    database.addAll(line.points())
  }

  fun render(invertY: Boolean = false) {
    var xMinDatabase = database.minOf { it.x }
    var xMaxDatabse = database.maxOf { it.x }
    var yMaxDatabase = database.maxOf { it.y }
    var yMinDatabase = database.minOf { it.y }

    if (this.xMin != null) {
      xMinDatabase = this.xMin!!
    }
    if (this.xMax != null) {
      xMaxDatabse = this.xMax!!
    }
    if (this.yMin != null) {
      yMinDatabase = this.yMin!!
    }
    if (this.yMax != null) {
      yMaxDatabase = this.yMax!!
    }

    var yRange: IntProgression = (yMinDatabase..yMaxDatabase)
    if (!invertY) {
      yRange = yRange.reversed()
    }
    for (y in yRange) {
      for (x in xMinDatabase..xMaxDatabse) {
        if (database.contains(Point(x, y))) {
          print(database.find { it == Point(x, y) }!!.render())
        } else {
          print(".")
        }
      }
      println()
    }
  }
}
