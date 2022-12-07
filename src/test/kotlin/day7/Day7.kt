package day7

import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.assertEquals

class Day7 {
    @Test
    fun sampleSilver() {
        val lines = read_puzzle_input("sample")

        val database = createStateful(lines)
        val solution = listDirectorySizes(database)
            .filter { it < 100000 }
            .sumOf { it }

        assertEquals(solution, 95437)
    }


    @Test
    fun sampleGold() {
        val lines = read_puzzle_input("sample")

        val totalDiskSpace = 70000000
        val updateSpaceSize = 30000000
        val database = createStateful(lines)

        val usedSpace = database.sumOf { it.size }
        assertEquals(usedSpace, 48381165)

        val unusedSpace = totalDiskSpace - usedSpace
        assertEquals(unusedSpace, 21618835)

        val neededSpace = updateSpaceSize - unusedSpace
        assertEquals(neededSpace, 8381165)

        val folderToDelete = listDirectorySizes(database)
            .filter { it >= neededSpace }
            .minOf { it }
        assertEquals(folderToDelete, 24933642)
    }


    @Test
    fun silver() {
        val lines = read_puzzle_input("input")

        val database = createStateful(lines)
        val solution = listDirectorySizes(database)
            .filter { it < 100000 }
            .sumOf { it }
        assertEquals(1306611, solution)
    }

    @Test
    fun gold() {
        val lines = read_puzzle_input("input")
        val database = createStateful(lines)

        val totalDiskSpace = 70000000
        val updateSpaceSize = 30000000

        val usedSpace = database.sumOf { it.size }
        val unusedSpace = totalDiskSpace - usedSpace
        val neededSpace = updateSpaceSize - unusedSpace

        val folderToDelete = listDirectorySizes(database)
            .filter { it >= neededSpace }
            .minOf { it }
        assertEquals(13210366, folderToDelete)

    }
}

data class Descriptor(val name: String, val size: Int, val directory: Path)

private fun listDirectorySizes(database: MutableList<Descriptor>): List<Int> {
    return database.map { it.directory }.sorted().distinct().map {
        database.filter { entry -> entry.directory.startsWith(it) }.sumOf { it.size }
    }
}

private fun createStateful(lines: List<String>): MutableList<Descriptor> {
    val database = mutableListOf<Descriptor>()
    var workDir = Path("")
    lines
        .filter { !it.startsWith("$ ls") }
        .forEach {
            when {
                it.startsWith("$ cd /") -> {
                    // absolute path
                    workDir = Path(it.substringAfter("cd").trim())
                }

                it.startsWith("$ cd ..") -> {
                    // relative folder up
                    workDir = workDir.parent
                }

                it.startsWith("$ cd") -> {
                    // relative folder down
                    workDir = Path(workDir.toString(), it.substringAfter("cd").trim())
                }

                it.startsWith("dir") -> {
                    // entry directory
                    database.add(Descriptor("", 0, workDir))
                }
                else -> {
                    // entry file
                    val (size, name) = it.split(" ")
                    database.add(Descriptor(name, size.toInt(), workDir))
                }
            }
        }
    return database
}

fun read_puzzle_input(filename: String): List<String> {
    return File("src/test/kotlin/day7/$filename")
        .readLines()
}
