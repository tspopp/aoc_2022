package day10

import java.io.File
import java.lang.UnsupportedOperationException
import java.util.*
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.junit.jupiter.api.Test

class Day10 {
  @Test
  fun sampleSilver() {
    val cpu = CPU(parseCommands("sample"))

    cpu.cycle()
    assertEquals(1, cpu.getRegister())

    cpu.cycle()
    assertEquals(1, cpu.getRegister())

    cpu.cycle()
    assertEquals(4, cpu.getRegister())

    cpu.cycle()
    assertEquals(4, cpu.getRegister())

    cpu.cycle()
    assertEquals(-1, cpu.getRegister())

    assertFalse(cpu.cycle())
  }

  @Test
  fun sampleSilverLarge() {
    val cpu = CPU(parseCommands("large"))

    val history =
        (0..220).map {
          cpu.cycle()
          cpu.getRegister()
        }

    assertEquals(21, history[18])
    assertEquals(19, history[58])
    assertEquals(18, history[98])
    assertEquals(21, history[138])
    assertEquals(16, history[178])
    assertEquals(18, history[218])

    val solution =
        history
            .withIndex()
            .filterIndexed { index, _ ->
              index == 18 ||
                  index == 58 ||
                  index == 98 ||
                  index == 138 ||
                  index == 178 ||
                  index == 218
            }
            .fold(0) { acc, i -> acc.plus((i.index + 2) * i.value) }

    assertEquals(13140, solution)
  }

  @Test
  fun silver() {
    val cpu = CPU(parseCommands("input"))

    val history =
        (0..220).map {
          cpu.cycle()
          cpu.getRegister()
        }

    val solution =
        history
            .withIndex()
            .filterIndexed { index, _ ->
              index == 18 ||
                  index == 58 ||
                  index == 98 ||
                  index == 138 ||
                  index == 178 ||
                  index == 218
            }
            .fold(0) { acc, i -> acc.plus((i.index + 2) * i.value) }

    assertEquals(13740, solution)
  }

  @Test
  fun gold() {
    val cpu = CPU(parseCommands("input"))

    val sprite = "###....................................."
    var spriteForEachCycle =
        (0..250).map {
          cpu.cycle()
          moveSprite(sprite, cpu.getRegister() - 1)
        }

    spriteForEachCycle = listOf(sprite) + spriteForEachCycle

    val line1 =
        (0..39)
            .mapIndexed { idx, it -> spriteForEachCycle[it][idx] }
            .fold("") { acc, c -> acc.plus(c) }
    val line2 =
        (40..79)
            .mapIndexed { idx, it -> spriteForEachCycle[it][idx] }
            .fold("") { acc, c -> acc.plus(c) }
    val line3 =
        (80..119)
            .mapIndexed { idx, it -> spriteForEachCycle[it][idx] }
            .fold("") { acc, c -> acc.plus(c) }
    val line4 =
        (120..159)
            .mapIndexed { idx, it -> spriteForEachCycle[it][idx] }
            .fold("") { acc, c -> acc.plus(c) }
    val line5 =
        (160..199)
            .mapIndexed { idx, it -> spriteForEachCycle[it][idx] }
            .fold("") { acc, c -> acc.plus(c) }
    val line6 =
        (200..239)
            .mapIndexed { idx, it -> spriteForEachCycle[it][idx] }
            .fold("") { acc, c -> acc.plus(c) }

    assertEquals(line1, "####.#..#.###..###..####.####..##..#....")
    assertEquals(line2, "...#.#..#.#..#.#..#.#....#....#..#.#....")
    assertEquals(line3, "..#..#..#.#..#.#..#.###..###..#....#....")
    assertEquals(line4, ".#...#..#.###..###..#....#....#....#....")
    assertEquals(line5, "#....#..#.#....#.#..#....#....#..#.#....")
    assertEquals(line6, "####..##..#....#..#.#....####..##..####.")
  }
}

fun moveSprite(sprite: String, modifier: Int): String {
  return if (modifier > 0) {
    (".".repeat(modifier) + sprite).substring(0..39)
  } else if (modifier < 0) {
    sprite.substring(abs(modifier)) + (".".repeat(abs(modifier)))
  } else {
    sprite
  }
}

enum class CommandType(val cycles: Int) {
  NOOP(1),
  ADDX(2);

  companion object {
    fun fromString(command: String): CommandType {
      return when (command) {
        "noop" -> NOOP
        "addx" -> ADDX
        else -> throw UnsupportedOperationException()
      }
    }
  }
}

class Command(pair: Pair<CommandType, Optional<Int>>) {
  private var type = pair.first
  private var optional = pair.second
  private var cycle = 0

  fun cycle(): Boolean {
    cycle += 1
    return type.cycles == cycle
  }

  fun execute(register: Int): Int {
    if (optional.isPresent) {
      return register.plus(optional.get())
    }
    return register
  }
}

class CPU(private val commandQueue: List<Command>) {
  private var register: Int = 1
  private var cycle: Long = 0
  private var commandIndex: Int = 0

  fun cycle(): Boolean {
    if (commandQueue.size == commandIndex) {
      return false
    }
    cycle++

    val currentCommand = commandQueue[commandIndex]

    if (currentCommand.cycle()) {
      commandIndex += 1
      register = currentCommand.execute(register)
    }
    return true
  }

  fun getRegister(): Int {
    return register
  }
}

private fun parseCommands(filename: String): List<Command> {
  return File("src/test/kotlin/day10/$filename")
      .readLines()
      .map {
        val split = it.split(" ")
        split.first() to split.last()
      }
      .map { (command, value) ->
        val optionalValue = value.toIntOrNull()
        Command(
            CommandType.fromString(command) to
                if (optionalValue == null) {
                  Optional.empty()
                } else {
                  Optional.of(optionalValue)
                })
      }
}
