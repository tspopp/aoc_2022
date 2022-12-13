package day1

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Day1 {

  @Test
  fun sampleSilver() {
    val elves = create_elves(read_puzzle_input("sample_silver"))
    assertEquals(24000, elves.map { it.sum() }.max())
  }

  @Test
  fun sampleGold() {
    val elves = create_elves(read_puzzle_input("sample_silver"))
    assertEquals(45000, elves.map { it.sum() }.sortedDescending().take(3).sum())
  }

  @Test
  fun silver() {
    val elves = create_elves(read_puzzle_input("input_silver"))
    assertEquals(69795, elves.map { it.sum() }.max())
  }

  @Test
  fun gold() {
    val elves = create_elves(read_puzzle_input("input_silver"))
    assertEquals(208437, elves.map { it.sum() }.sortedDescending().take(3).sum())
  }
}

data class Elv(val snacks: List<Int>)

fun Elv.sum(): Int {
  return snacks.sum()
}

fun create_elves(raw: String): List<Elv> {
  return raw.split("\n\n").map { Elv(it.split("\n").map { it.toInt() }) }
}

fun read_puzzle_input(filename: String): String {
  return File("src/test/kotlin/day1/$filename").readText(Charsets.UTF_8)
}
