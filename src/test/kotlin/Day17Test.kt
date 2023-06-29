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

data class Block(
    val x: Int,
    val y: Int,
    val rows: List<String>,
) {
    fun apply(movement: Movement): Block {
        return when (movement) {
            Movement.Left -> copy(x = x - 1)
            Movement.Right -> copy(x = x + 1)
        }
    }

    fun down(): Block {
        return copy(y = y + 1)
    }
}


class Area {
    private val width = 7
    private val grid = mutableListOf<String>()

    fun validate(block: Block): Boolean {
        return true
    }

    fun store(block: Block) {
        while (grid.size <= block.y ) {
            grid.add(".".repeat(width))
        }
        block.rows.forEachIndexed { index, s ->
            val s2 = s.replace("@", "#")
            grid[block.y - index] = grid[block.y - index].replaceRange(block.x, block.x + s2.length, s2)
        }
    }

    fun print() {
        grid.reversed().forEachIndexed { idx, row: String ->
            val k = String.format("%2d", grid.size - idx - 1)
            println("$k $row") }
    }
}

class Day17Test {
    @Test
    fun part1() {
        val movements = Files.readString(Path.of("17.txt"))
            .map { Movement.valueOf(it) }
            // .also { println(it) }

        val area = Area()

        val block = Block(0, 0, listOf("@@@@")).also {
            area.store(it)
            area.print()
        }
        println("---")
        Block(0, 4, listOf("@@@@", ".@@")).also {
            area.store(it)
            area.print()
        }
    }
}
