package day5

import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class Day5 {

    @Test
    fun sampleSilver() {
        val (stack, commands) = parseStackAndCommands("sample")
        val harbor = Harbor.createFromStack(stack)
        commands.lines().forEach { harbor.executeCommand(it) }

        assertEquals(harbor.message(), "CMZ")
    }

    @Test
    fun sampleGold() {
        val (stack, commands) = parseStackAndCommands("sample")
        val harbor = Harbor.createFromStack(stack)
        commands.lines().forEach { harbor.executeCommand(it, true) }

        assertEquals(harbor.message(), "MCD")
    }

    @Test
    fun silver() {
        val (stack, commands) = parseStackAndCommands("input")
        val harbor = Harbor.createFromStack(stack)
        commands.lines().forEach { harbor.executeCommand(it) }

        assertEquals(harbor.message(), "CNSZFDVLJ")
    }

    @Test
    fun gold() {
        val (stack, commands) = parseStackAndCommands("input")
        val harbor = Harbor.createFromStack(stack)
        commands.lines().forEach { harbor.executeCommand(it, true) }

        assertEquals(harbor.message(), "QNDWLMGNS")
    }
}

data class Harbor(val mDatabase: List<LinkedList<Char>>) {
    companion object {
        fun createFromStack(stack: String): Harbor {
            val reversedStack = stack.lines().asReversed()
            val numOfStacks = (reversedStack.first().length / 4) + 1
            val database: List<LinkedList<Char>> = List(numOfStacks) { LinkedList<Char>() }

            reversedStack
                .drop(1)
                .map { item ->
                    item
                        .windowed(3, 4)
                        .map { it.removeSurrounding("[", "]") }
                        .map { it.toCharArray().first() }
                }
                .forEach { items ->
                    items.withIndex()
                        .filter { !it.value.isWhitespace() }
                        .forEach { (row, item) -> database[row].push(item) };

                }
            return Harbor(database)
        }
    }
}

fun Harbor.executeCommand(command: String, crateMover6001: Boolean = false) {

    val count = command.substringAfter("move").substringBefore("from").trim().toInt()
    val origin = command.substringAfter("from").substringBefore("to").trim().toInt()
    val destination = command.substringAfter("to").trim().toInt()

    if (crateMover6001) {
        (1..count).map { mDatabase[origin - 1].pop() }
            .reversed()
            .forEach {
                mDatabase[destination - 1].push(it)
            }
    } else {
        repeat(count) {
            mDatabase[destination - 1].push(mDatabase[origin - 1].pop())
        }
    }
}

fun Harbor.message(): String {
    return mDatabase.map { it.peek() }
        .fold("") { message, it -> message.plus(it) }
}

fun parseStackAndCommands(filename: String): Pair<String, String> {
    val input = File(
        "src/test/kotlin/day5" +
                "/$filename"
    ).readText(Charsets.UTF_8)

    return Pair(input.substringBefore("\n\n"), input.substringAfter("\n\n"))
}
