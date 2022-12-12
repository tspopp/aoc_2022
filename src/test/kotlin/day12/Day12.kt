package day12

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.SimpleDirectedGraph
import org.junit.jupiter.api.Test
import tools.Point
import tools.directNeighbors
import java.io.File
import kotlin.test.assertEquals

class Day12 {

    @Test
    fun sampleSilver() {
        val (network, start, end) = read_puzzle_input("sample")
        val graph = simpleDirectedGraph(network)

        assertEquals(31, DijkstraShortestPath.findPathBetween(graph, start.toString(), end.toString()).length)
    }


    @Test
    fun goldSample() {
        val (network, _, end) = read_puzzle_input("sample")
        val startPoints = network.entries.filter { (_, value) -> value == lookup.indexOf('a') }.map { it.key }
        val graph = simpleDirectedGraph(network)

        assertEquals(
            29,
            startPoints.mapNotNull { DijkstraShortestPath.findPathBetween(graph, it.toString(), end.toString()) }
                .minOf { it.length })
    }

    @Test
    fun silver() {
        val (network, start, end) = read_puzzle_input("input")
        val graph = simpleDirectedGraph(network)

        assertEquals(391, DijkstraShortestPath.findPathBetween(graph, start.toString(), end.toString()).length)
    }

    @Test
    fun gold() {
        val (network, _, end) = read_puzzle_input("input")
        val start = network.entries.filter { (_, value) -> value == lookup.indexOf('a') }.map { it.key }
        val graph = simpleDirectedGraph(network)

        assertEquals(
            386,
            start.mapNotNull { DijkstraShortestPath.findPathBetween(graph, it.toString(), end.toString()) }
                .minOf { it.length })
    }
}

var lookup = "SabcdefghijklmnopqrstuvwxyzE"

data class Edge(val source: String, val target: String, val weight: Int)

fun simpleDirectedGraph(network: Map<Point, Int>): SimpleDirectedGraph<String, Edge> {
    val graph = SimpleDirectedGraph<String, Edge>(Edge::class.java)

    for (point in network.keys) {
        graph.addVertex(point.toString())
    }

    for (entry in network) {
        entry.key.directNeighbors().filter { network.containsKey(it) }.forEach {
            if (network[it]!! <= entry.value.plus(1)) {
                val edge = Edge(entry.key.toString(), it.toString(), 1)
                graph.addEdge(edge.source, edge.target, edge)
            }
        }
    }

    return graph
}

fun read_puzzle_input(filename: String): Triple<Map<Point, Int>, Point, Point> {
    val database = mutableMapOf<Point, Int>()
    var start = Point(0,0)
    var end = Point(0,0)

    File("src/test/kotlin/day12/$filename")
        .readLines()
        .withIndex()
        .map { (row, line) ->
            line.withIndex()
                .forEach() { (column, level) ->
                    database[Point(column, row)] = lookup.indexOf(level)

                    if(level == 'S'){
                        start = Point(column, row)
                    } else if(level == 'E'){
                        end = Point(column, row)
                    }
                }
        }

    return Triple(database.toMap(), start, end)