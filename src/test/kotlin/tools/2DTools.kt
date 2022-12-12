package tools

typealias Point = Pair<Int, Int>

fun Point.movePoint(direction: Direction): Point {
    return when (direction) {
        Direction.Right -> this.first + 1 to this.second
        Direction.Up -> this.first to this.second + 1
        Direction.Left -> this.first - 1 to this.second
        Direction.Down -> this.first to this.second - 1
    }
}

fun Point.directNeighbors(): List<Point>{
    return Direction.values()
        .map { this.movePoint(it) }
        .filter { it -> it.first >= 0 && it.second >= 0 }
        .toList()
}

enum class Direction {
    Right,
    Up,
    Left,
    Down;
}