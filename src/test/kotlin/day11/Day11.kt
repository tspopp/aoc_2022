package day11

import java.io.File
import java.nio.charset.Charset
import java.util.LinkedList
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class Day11 {
  @Test
  fun sampleSilver() {
    val game = Game(parseApes("sample"))

    repeat(20) { game.round() }

    game.inspect()

    val solution =
        game.apes.map { it.count() }.sortedDescending().take(2).fold(1L) { acc, i -> acc * i }
    assertEquals(10605, solution)
  }

  @Test
  fun silver() {
    val game = Game(parseApes("input"))

    repeat(20) { game.round() }
    val solution =
        game.apes.map { it.count() }.sortedDescending().take(2).fold(1L) { acc, i -> acc * i }
    assertEquals(55216, solution)
  }

  @Test
  fun sampleGold() {
    val game = Game(parseApes("sample", noWorries = true))

    repeat(10000) { game.round() }

    game.inspect()

    val solution =
        game.apes.map { it.count() }.sortedDescending().take(2).fold(1L) { acc, i -> acc * i }
    assertEquals(2713310158, solution)
  }

  @Test
  fun gold() {
    val game = Game(parseApes("input", noWorries = true))

    repeat(10000) { game.round() }

    val solution =
        game.apes.map { it.count() }.sortedDescending().take(2).fold(1L) { acc, i -> acc * i }
    assertEquals(12848882750, solution)
  }
}

interface ApeCallback {
  fun throwItem(destination: Int, value: Long)
}

class Game(val apes: List<Ape>) : ApeCallback {
  private var dividor: Long = 0

  init {
    // common dividor is the product of all module parameters
    dividor = apes.map { it.testDivisible }.fold(1) { acc, value -> acc * value }
    apes.forEach { it.setCallback(this) }
  }

  fun round() {
    apes.sortedBy { it.apeId }.forEach { it.play() }
  }

  override fun throwItem(destination: Int, value: Long) {
    // println("forwarding item to ape $destination with $value")
    // shrink the values with common dividor, same modulo logic still applies
    // but values are significantly smaller
    apes.first { it.apeId == destination }.addItem(value % dividor)
  }

  fun inspect() {
    apes.forEach { it.printInformation() }
  }
}

class Ape(
    val apeId: Int,
    private val operation: Pair<Operator, Int>,
    val testDivisible: Long,
    private val goodMonkey: Int,
    private val badMonkey: Int,
    private val noWorries: Boolean
) {
  private val items = LinkedList<Long>()
  private var callback: ApeCallback? = null
  private var inspectionCount: Long = 0

  fun setCallback(cb: ApeCallback) {
    callback = cb
  }

  fun play() {
    while (!items.isEmpty()) {
      val it = items.pop()
      // println("monkey $apeId started with item $it")
      val inspectionBegin = beginInspection(it)
      // println("monkey begins inspection: worry value changed to $inspectionBegin")

      val inspectionEnd = endInspection(inspectionBegin)
      // println("monkey ends inspection: worry value changed to $inspectionEnd")

      if (inspectionEnd % testDivisible == 0L) {
        // println("$inspectionEnd is dividable by $testDivisible - will throw to monkey with id
        // $goodMonkey")
        callback?.throwItem(goodMonkey, inspectionEnd)
      } else {
        // println("$inspectionEnd is NOT dividable by $testDivisible - will throw to monkey with id
        // $badMonkey")
        callback?.throwItem(badMonkey, inspectionEnd)
      }
      inspectionCount++
    }
  }

  private fun beginInspection(value: Long): Long {
    return when (operation.first) {
      Operator.Plus -> value + operation.second
      Operator.Multiply -> value * operation.second
      Operator.Square -> value * value
    }
  }

  private fun endInspection(value: Long): Long {
    if (noWorries) {
      return value
    }
    return value / 3
  }

  fun addItem(item: Long) {
    items.push(item)
  }

  fun printInformation() {
    println(
        "monkey $apeId: seen: ${count()} items $items, operation $operation, test $testDivisible, good monkey: $goodMonkey, bad monkey: $badMonkey")
  }

  fun count(): Long {
    return inspectionCount
  }
}

enum class Operator {
  Plus,
  Multiply,
  Square
}

private fun parseApes(filename: String, noWorries: Boolean = false): List<Ape> {
  return File("src/test/kotlin/day11/$filename")
      .readText(Charset.defaultCharset())
      .split("\n\n")
      .mapIndexed { monkey_id, monkey ->
        val description = monkey.lines().drop(1)

        val operationLine = description[1].substringAfter("new = ")
        val operation =
            if (operationLine.contains("+")) {
              Operator.Plus to operationLine.substringAfter("+").trim().toInt()
            } else if (operationLine == "old * old") {
              Operator.Square to 2
            } else {
              Operator.Multiply to operationLine.substringAfter("*").trim().toInt()
            }

        val disableBy = description[2].substringAfter("by").trim().toLong()
        val goodMonkey = description[3].substringAfter("monkey").trim().toInt()
        val badMonkey = description[4].substringAfter("monkey").trim().toInt()
        val ape = Ape(monkey_id, operation, disableBy, goodMonkey, badMonkey, noWorries)

        description[0]
            .substringAfter(":")
            .split(",")
            .map { it.trim().toLong() }
            .forEach { ape.addItem(it) }

        ape
      }
      .toList()
}
