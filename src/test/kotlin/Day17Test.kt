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

    fun print() {
        rows.forEach { println(it) }
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
                if (grid[block.y - yidx][block.x + xidx] == '#') {
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
            // grid[block.y - index] = grid[block.y - index].replaceRange(block.x, block.x + s2.length, s2)
            s2.indices.forEach { s2idx ->
                val x = block.x + s2idx
                val y = block.y - index
                if (grid[y][x] == '.') {
                    grid[y] = grid[y].replaceRange(x, x + 1, s2[s2idx].toString())
                }
            }
        }

        // Remove all empty rows from the end of the list.
        while (grid.last().replace(".", "").isEmpty()) {
            grid.removeLast()
        }
    }

    fun print() {
        grid.reversed().forEachIndexed { idx, row: String ->
            val k = String.format("%2d", grid.size - idx)
            println("$k $row")
        }
    }

    fun highestBlockY(): Int {
        // Find topmost row with a rock.
        for (idx in grid.indices.reversed()) {
            if (grid[idx].contains("#")) {
                return idx
            }
        }

        return -1
    }

    fun isCycle(): Int? {
        if (grid.size < 10) {
            return null
        }

        // Iteratively increase potential cycle size and check for a cycle.
        val y = highestBlockY()
        for (cycleSize in 10..y / 2) {
            val first = grid.subList(y - cycleSize, y)
            val second = grid.subList(y - cycleSize*2, y - cycleSize)
            if (first == second) {
                println("Cycle detected at cycleSize = $cycleSize with $y")
                return cycleSize
            }
        }

        return null
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
    fun `is this a cycle`() {
        val area = Area()
        area.grid.add("..###.")
        area.grid.add("..####.")
        area.grid.add("..###.")
        area.grid.add("..####.")
        area.print()
        println(area.isCycle())
    }

    @Test
    fun part2() {
        val movements = Files.readString(Path.of("17.txt")).map { Movement.valueOf(it) }

        val area = Area()
        var movementIndex = 0
        var blockIndex = 0
        var numRocks = 0

        val steps = 20000
        var ms = System.currentTimeMillis()
        var rocksDroppedBefore = 0
        var rocksDroppedInCycle = 0

        var cycleDectected = false
        var afterCycleCount = 0
        // see main function
        var necessaryRocks = 795

        var lastHeight = -1
        while (numRocks++ != steps) {
            // For the example:
            // We know the cycle size is 53 (for the test input).
            // A cycle starts at row 79 and goes until 132. Let's
            // count the number of dropped rocks in a cycle by looking
            // at the highest y once we are at row 79.
            if (area.highestBlockY() in 338..3089) {
                rocksDroppedInCycle++
                // if (lastHeight == -1) {
                //     lastHeight = area.highestBlockY()
                // }
            }
            if (area.highestBlockY() <= 337) {
                rocksDroppedBefore++
            }

            val cycleSize = area.isCycle()?.let {
                println("Rocks dropped before cycle = $rocksDroppedBefore")
                cycleDectected = true
                println("Cycle detected at dropped rocks = $numRocks")
                val cycleSize = it.toBigInteger()
                val expectedRocks = "1000000000000".toBigInteger()
                val highest = area.highestBlockY().toBigInteger()
                val f = (expectedRocks - 217.toBigInteger()) / 1745.toBigInteger()
                // Computed in previous iteration manually. This is the height of the number of
                // remaining rocks when the cycle is not perfect to match expectedRocks.
                val remainingRocksAfterCycle = 1274.toBigInteger()
                val res = f * cycleSize + (highest - 2.toBigInteger()*cycleSize) + remainingRocksAfterCycle
                println(res)
                // area.print()
                return
            }

            if (cycleDectected) {
                necessaryRocks--
                if (necessaryRocks < 0) {
                    // 1274 computed here.
                    println("area.highestBlockY() = ${area.highestBlockY() - 5841}")
                    return
                }
            }

            val nextBlock = blocks[blockIndex]
            val nextY = area.highestBlockY() + nextBlock.size + 3
            var block = Block(2, nextY, nextBlock)
            blockIndex = (blockIndex + 1) % blocks.size

            while (true) {
                // Move left or right.
                val b1 = block.apply(movements[movementIndex])
                movementIndex = (movementIndex + 1) % movements.size
                if (area.isValid(b1)) {
                    block = b1
                }

                // Move down.
                val b2 = block.down()
                if (area.isValid(b2)) {
                    block = b2
                } else {
                    break
                }
            }

            area.store(block)
        }
        println("Nothing found")
        println(area.highestBlockY() + 1)
        // area.print()
        // We start counting at 0.
    }

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
        while (numRocks++ != steps) {
            val nextBlock = blocks[blockIndex]
            val nextY = area.highestBlockY() + nextBlock.size + 3
            var block = Block(2, nextY, nextBlock)
            blockIndex = (blockIndex + 1) % blocks.size

            // println("------------------ $numRocks")
            // val a = area.copy()
            // a.store(block)
            // a.print()
            // println()
            // Thread.sleep(5000)

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
        println(area.highestBlockY() + 1)
        // area.print()
        // We start counting at 0.
    }

    // Test case works, but algorithm is not correct for my specific input.
    // Trying to figure out why...
    @Test
    fun bugfixhunt1() {
        val area = Area()
        val rows = blocks[0]

        repeat(10) {
            val y = area.highestBlockY() + rows.size + 3
            var block = Block(2, y, rows)
            while (true) {
                var tmp = block
                while (area.isValid(tmp)) {
                    tmp = tmp.apply(Movement.Left)
                }

                tmp = block.down()
                if (!area.isValid(tmp)) {
                    area.store(block)
                    break
                }
                block = tmp
            }
        }

        area.print()
    }

    @Test
    fun `fix empty space bug`() {
        val area = Area()

        val block = Block(1, 1, listOf("@@@@"))
        area.store(block)
        area.print()
        println("------")

        val block2 = Block(4, 3, listOf(".@.", "@@@", ".@."))
        area.store(block2)
        area.print()
    }
}

// ------------------ 12
// 21 ...#...
// 20 ..###..
// 19 ...#...
// 18 .......
// 17 .......
// 16 .......
// 15 .####..
// 14 ....###

// ------------------ 13
// 17 .....#.
// 16 ....###
// 15 .###.#.

// the blank from the previous block is overriding the existing rock (line 15)

fun main() {
    val expectedRocks = "1000000000000".toBigInteger()
    val cur = 217.toBigInteger()
    val one = 1.toBigInteger()
    // Adding the "one" since we want the height after the last rock is dropped.
    val r = (expectedRocks - cur + one) - (expectedRocks - cur + one) / 1745.toBigInteger() * 1745.toBigInteger() + one
    println(r)
}