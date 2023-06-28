package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

enum class Movement {
    Left,
    Right;

    companion object {
        fun valueOf(c: Char): Movement {
            return when (c) {
                '<' -> Left
                '>' -> Right
                else -> throw IllegalArgumentException("Unknown movement $c")
            }
        }
    }
}

class Day17Test {
    @Test
    fun part1() {
        val movements = Files.readString(Path.of("17.txt"))
            .map { Movement.valueOf(it) }
            .also { println(it) }

        val grid = mutableListOf<String>()
        val block = """
            @@@@    
            @@@@
        """.trimIndent().split("\n")
        // val block = listOf<String>("@@@@")
        var movement = 0
        // grid.add("...##..")
        // grid.add("...##..")
        step(grid, block, movements, movement)
    }

    // Stateless function.
    // Returns true if we can continue, and also the new movement index.
    // If false is returned, the block is already finalized (by switching from
    // @ to # and storing it in the grid).
    fun step(grid: List<String>, block: List<String>, movements: List<Movement>, movement: Int): Pair<Boolean, Int> {
        val x = 2
        // Determine initial block position (three rows from the top until the next block counting
        // from the bottom). We have definetely some space at the bottom of the block now.
        val y = grid.indexOfLast { it.contains("#") } + 3 + block.size

        // Check if we are at the bottom.

        // Until we can not move further:
        // Simulate steam.
        // Simulate falling down.
        // Then: fix the block into the grid.
    }

    @Test
    fun part2() {
    }
}
