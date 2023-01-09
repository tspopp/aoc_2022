package day13

import java.io.File
import java.util.*
import kotlin.test.assertEquals
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Test

class Day13 {
  @Test
  fun sampleSilver() {
    val lines = read_puzzle_input("sample")
    val solution =
        lines
            .windowed(2, 3)
            .mapIndexed { index, it -> index to Packet(it.first()).compareTo(Packet(it.last())) }
            .filter { (_, it) -> it == 1 }
            .fold(0) { acc, (index, _) -> acc.plus(index.plus(1)) }
    assertEquals(13, solution)
  }

  @Test
  fun silver() {
    val lines = read_puzzle_input("input")
    val solution =
        lines
            .windowed(2, 3)
            .mapIndexed { index, it -> index to Packet(it.first()).compareTo(Packet(it.last())) }
            .filter { (_, it) -> it == 1 }
            .fold(0) { acc, (index, _) -> acc.plus(index.plus(1)) }

    assertEquals(5659, solution)
  }

  @Test
  fun sampleGold() {
    val lines = read_puzzle_input("sample")
    val list = lines + "[[2]]" + "[[6]]"

    val solution =
        list
            .filter { it.isNotBlank() }
            .map { Packet(it) }
            .sorted()
            .reversed()
            .withIndex()
            .filter { (_, packet) -> packet.string == "[[2]]" || packet.string == "[[6]]" }
            .map { (index, _) -> index.plus(1) }
            .onEach { println("found on index $it") }
            .reduce { acc, it -> acc * it }

    assertEquals(140, solution)
  }

  @Test
  fun gold() {
    val lines = read_puzzle_input("input")
    val list = lines + "[[2]]" + "[[6]]"

    val solution =
        list
            .filter { it.isNotBlank() }
            .map { Packet(it) }
            .sorted()
            .reversed()
            .withIndex()
            .filter { (_, packet) -> packet.string == "[[2]]" || packet.string == "[[6]]" }
            .map { (index, _) -> index.plus(1) }
            .onEach { println("found on index $it") }
            .reduce { acc, it -> acc * it }

    assertEquals(22110, solution)
  }
}

class Packet(val string: String) : Comparable<Packet> {
  private var json: JsonElement = Json.parseToJsonElement(string)
  private var children: LinkedList<Packet> = LinkedList()
  private var value: Int? = null
  init {
    if (json is JsonArray) {
      json.jsonArray.forEach { children.add(Packet(it.toString())) }
    } else {
      value = json.jsonPrimitive.int
    }
  }

  override fun compareTo(other: Packet): Int {
    println("- Compare ${this.json} vs ${other.json}")
    if (value == null && other.value == null) {
      for ((index, item) in children.withIndex()) {
        val otherItem = other.children.getOrNull(index)
        if (otherItem == null) {
          println("- Right side ran out of items, so inputs are not in the right order")
          return -1
        }
        val result = item.compareTo(otherItem)
        if (result != 0) {
          return result
        }
      }
      if (children.size < other.children.size) {
        return 1
      }
      return 0
    } else if (value != null && other.value == null) {
      return Packet("[$value]").compareTo(other)
    } else if (value == null && other.value != null) {
      return this.compareTo(Packet("[${other.value}]"))
    } else {
      if (value!! == other.value!!) {
        return 0
      }
      return if (value!! < other.value!!) {
        println("Left side is smaller, so inputs are in the right order")
        1
      } else {
        println("Right side is smaller, so inputs are not in the right order")
        -1
      }
    }
  }
}

fun read_puzzle_input(filename: String): List<String> {
  return File("src/test/kotlin/day13/$filename").readLines()
}
