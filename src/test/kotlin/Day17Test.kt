package com.mlesniak.changeme

import org.junit.jupiter.api.Test

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

class Area {
    private val grid = mutableListOf<String>()
    private val emptyLine = ".".repeat(7)

    fun debug(rows: List<String>) {
        rows.forEach {
            grid.add(0, it)
        }
    }

    fun add(block: List<String>) {
        repeat(block.size + 3) {
            grid.add(0, emptyLine)
        }

        val x = 2
        val y = 0
        simulate(block, x, y)
    }

    private fun simulate(block: List<String>, x: Int, y: Int) {
        // Check if we are at the bottom.
        if (y + block.size == grid.size) {
            // Fix block.
            block.forEachIndexed { index, s ->
                grid[y + index] = grid[y + index].replaceRange(x, x + s.length, s.replace('@', '#'))
            }
            return
        }
        // Check if the bottom line of the block collides with an existing block.
        val bottomLine = block.last()
        val bottomLineOfArea = grid[y + block.size]
        // For every character in the bottom line of the block, check if it collides with a
        // non-occupied piece in the current bottom line.
        var collided = false
        bottomLine.forEachIndexed { index, c ->
            if (collided) {
                return@forEachIndexed
            }
            if (c == '.') {
                return@forEachIndexed
            }
            if (bottomLineOfArea[x + index] != '.') {
                collided = true
            }
        }
        if (collided) {
            block.forEachIndexed { index, s ->
                grid[y + index] = grid[y + index].replaceRange(x, x + s.length, s.replace('@', '#'))
            }
            return
        }

        simulate(block, x, y + 1)
    }

    override fun toString(): String {
        return grid.joinToString("\n")
    }
}

class Day17Test {
    @Test
    fun part1() {
        // val movements = Files.readString(Path.of("17.txt"))
        //     .map { Movement.valueOf(it) }
        //     .also { println(it) }

        val emptyLine = ".".repeat(7)
        val area = Area()
        // area.debug(listOf(emptyLine))
        // area.debug(listOf("..##..."))
        // area.debug(listOf("......."))

        area.add(listOf("@@@@"))
        // area.add("""
        //     @@
        //     @
        //     @
        // """.trimIndent().split("\n"))
        println(area)
    }

    @Test
    fun part2() {
    }
}
