package day11

import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.LinkedList
import kotlin.test.assertEquals

class Day11 {
    @Test
    fun sampleSilver() {
        val game = Game(parseApes("sample"))

        repeat(20) {
            game.round()
        }
        val solution = game.apes.map { it.count() }.sortedDescending().take(2).fold(1) { acc, i -> acc * i };
        assertEquals(10605, solution)
    }

    @Test
    fun silver() {
        val game = Game(parseApes("input"))

        repeat(20) {
            game.round()
        }
        val solution = game.apes.map { it.count() }.sortedDescending().take(2).fold(1) { acc, i -> acc * i };
        assertEquals(55216, solution)
    }

    @Test
    fun sampleGold() {
        val game = Game(parseApes("sample"))

        var i = 0;
        repeat(10000) {
            i++
            println("running round $i of 10000")
            game.round()

        }
        val solution = game.apes.map { it.count() }.sortedDescending().take(2).fold(1) { acc, i -> acc * i };
        assertEquals(10605, solution)
    }

}

interface ApeCallback {
    fun throwItem(destination: Int, value: BigInteger)
}

class Game(val apes: List<Ape>) : ApeCallback {
    init {
        apes.forEach {
            it.setCallback(this)
        }
    }

    fun round() {
        apes.sortedBy { it.apeId }.forEach {
            it.play()
        }
    }

    override fun throwItem(destination: Int, value: BigInteger) {
        //println("forwarding item to ape $destination with $value")
        apes.first { it.apeId == destination }.addItem(value)
    }

    fun inspect() {
        apes.forEach { println(it.printInformation()) }
    }

}

class Ape(
    val apeId: Int,
    private val operation: Pair<Operator, Int>,
    private val testDivisible: Long,
    private val goodMonkey: Int,
    private val badMonkey: Int,
    private val noWorries: Boolean,
) {
    private val items = LinkedList<BigInteger>()
    private var callback: ApeCallback? = null
    private var inspectionCount: Int = 0

    fun setCallback(cb: ApeCallback) {
        callback = cb
    }

    fun play() {
        while (!items.isEmpty()) {
            val it = items.pop();
            //println("monkey $apeId started with item $it")
            var inspectionBegin = beginInspection(it)
            //println("monkey begins inspection: worry value changed to $inspectionBegin")

            var inspectionEnd = endInspection(inspectionBegin)
            //println("monkey ends inspection: worry value changed to $inspectionEnd")

            if (inspectionEnd % testDivisible.toBigInteger() == BigInteger.ZERO) {
                //println("$inspectionEnd is dividable by $testDivisible - will throw to monkey with id $goodMonkey")
                callback?.throwItem(goodMonkey, inspectionEnd)
            } else {
                //println("$inspectionEnd is NOT dividable by $testDivisible - will throw to monkey with id $badMonkey")
                callback?.throwItem(badMonkey, inspectionEnd)
            }
            inspectionCount++
        }
    }

    private fun beginInspection(value: BigInteger): BigInteger {
        return when (operation.first) {
            Operator.Plus -> value + operation.second.toBigInteger()
            Operator.Multiply -> value * operation.second.toBigInteger()
            Operator.Square -> value * value
        }
    }

    private fun endInspection(value: BigInteger): BigInteger {
        if (noWorries) {
            return value;
        }
        return value / BigInteger.valueOf(3)
    }

    fun addItem(item: BigInteger) {
        items.push(item)
    }

    fun printInformation() {
        println("monkey $apeId: items $items, operation $operation, test $testDivisible, good monkey: $goodMonkey, bad monkey: $badMonkey")
    }

    fun count(): Int {
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
        .readText(Charset.defaultCharset()).split("\n\n")
        .mapIndexed { monkey_id, monkey ->

            val description = monkey.lines().drop(1);

            val operationLine = description[1].substringAfter("new = ")
            val operation = if (operationLine.contains("+")) {
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
                .map { it.trim().toBigInteger() }
                .forEach { ape.addItem(it) }

            ape
        }.toList()
}