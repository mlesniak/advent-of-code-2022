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
        return copy(y = y - 1)
    }
}

class Area {
    private val width = 7

    // Not private for debugging and hacking around.
    val grid = mutableListOf<String>()

    fun isValid(block: Block): Boolean {
        // Hit floor?
        if (block.y - block.rows.size + 1 < 0) {
            return false
        }

        // Check for out of bounds.
        var outOfOBounds = false
        if (block.x < 0) {
            outOfOBounds = true
        }
        block.rows.forEachIndexed { index, s ->
            if (block.x + s.length > width) {
                outOfOBounds = true
            }
        }
        if (outOfOBounds) {
            return false
        }

        // 3 ...#...
        // 2 ..###..
        // 1 ...#...
        // 0 ..####.

        // Check for other blocks at the same positions.
        // println("---------- Checking block at ${block.x}, ${block.y}")
        block.rows.forEachIndexed { yidx, row ->
            // println("*** Checking row $yidx")
            if (grid.size <= block.y - yidx) {
                // We are outside the grid for this row.
                // println("Outside grid (${grid.size}")
                return@forEachIndexed
            }
            // println("Checking row ${grid[block.y - yidx]}")
            row.forEachIndexed { xidx, c ->
                if (c == '.') {
                    // Can be ignored.
                    // println("Ignoring char $c at ${block.x + xidx}, ${block.y + yidx}")
                    return@forEachIndexed
                }
                // println("Checking char $c at ${block.x + xidx}, ${block.y + yidx}")
                if (grid[block.y-yidx][block.x + xidx] == '#') {
                    // println("Found rock at ${block.x + xidx}, ${block.y + yidx}")
                    return false
                }
            }
        }

        return true
    }

    fun copy(): Area {
        val area = Area()
        area.grid.addAll(grid)
        return area
    }

    fun store(block: Block) {
        while (grid.size <= block.y + block.rows.size) {
            grid.add(".".repeat(width))
        }
        block.rows.forEachIndexed { index, s ->
            val s2 = s.replace("@", "#")
            grid[block.y - index] = grid[block.y - index].replaceRange(block.x, block.x + s2.length, s2)
        }
    }

    fun print() {
        grid.reversed().forEachIndexed { idx, row: String ->
            val k = String.format("%2d", grid.size - idx)
            println("$k $row")
        }
    }

    fun nextY(): Int {
        // Find topmost row with a rock.
        for (idx in grid.indices.reversed()) {
            if (grid[idx].contains("#")) {
                return idx
            }
        }

        return -1
    }
}

class Day17Test {
    private val blocks = listOf(
        listOf("@@@@"),
        listOf(".@.", "@@@", ".@."),
        listOf("..@", "..@", "@@@"),
        listOf("@", "@", "@", "@"),
        listOf("@@", "@@"),
    )

    @Test
    fun part1() {
        val movements = Files.readString(Path.of("17.txt"))
            .map { Movement.valueOf(it) }
        // .also { println(it) }

        val area = Area()
        var movementIndex = 0
        var blockIndex = 0
        // area.grid.add("..####.")

        // var block = Block(2, 4, listOf("@@@@", "..@@", "...@"))
        var numRocks = 0

        val steps = 2022
        while (numRocks++ < steps) {
            val nextBlock = blocks[blockIndex]
            val nextY = area.nextY() + nextBlock.size + 3
            var block = Block(2, nextY, nextBlock)
            blockIndex = (blockIndex + 1) % blocks.size

            // println("------------------ $numRocks")
            // println("grid size = ${area.grid.size}")
            // val a = area.copy()
            // a.store(block)
            // a.print()
            // println()

            while (true) {
                // Move left or right.
                // println("Applying movement ${movements[movementIndex]}")
                val b1 = block.apply(movements[movementIndex])
                movementIndex = (movementIndex + 1) % movements.size
                if (area.isValid(b1)) {
                    block = b1
                }

                // Move down.
                // println("Applying movement down")
                val b2 = block.down()
                if (area.isValid(b2)) {
                    block = b2
                } else {
                    break
                }
            }

            area.store(block)
        }
        area.print()
        // We start counting at 0.
        println(area.nextY()+1)
    }
}
