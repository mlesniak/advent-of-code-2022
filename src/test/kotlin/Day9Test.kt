package com.mlesniak.changeme

import com.mlesniak.changeme.Direction.D
import com.mlesniak.changeme.Direction.L
import com.mlesniak.changeme.Direction.R
import com.mlesniak.changeme.Direction.U
import com.mlesniak.changeme.Direction.valueOf
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

private enum class Direction {
    R, U, L, D,
}

private data class Command(
    val dir: Direction,
    val steps: Int,
)

private data class Position(
    var x: Int,
    var y: Int,
)

class Day9Test {
    @Test
    fun part1() {
        val commands = parse()

        val positions = Array<Position>(10) {
            Position(0, 0)
        }
        var visited = mutableSetOf<Position>()

        commands.forEach { c ->
            when (c.dir) {
                R -> steps(c.steps, 1, 0, positions, visited)
                U -> steps(c.steps, 0, 1, positions, visited)
                L -> steps(c.steps, -1, 0, positions, visited)
                D -> steps(c.steps, 0, -1, positions, visited)
            }
        }

        println()
        println("${visited.size}")
        println(visited)
        debugVisited(visited)
    }

    private fun debug(positions: Array<Position>) {
        for (row in 5 downTo 0) {
            for (col in 0..5) {
                val p = Position(col, row)
                var c: Char
                val pos = positions.indexOf(p)
                if (pos == -1) {
                    c = '.'
                } else {
                    c = "$pos"[0]
                }
                print(c)
            }
            println()
        }
    }

    private fun debugVisited(visited: MutableSet<Position>) {
        for (row in 5 downTo 0) {
            for (col in 0..5) {
                val p = Position(col, row)
                if (p in visited) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }


    private fun steps(steps: Int, dx: Int, dy: Int, positions: Array<Position>, visited: MutableSet<Position>) {
        debug(positions)
        println("STEPS $steps dx=$dx, dy=$dy, pos=$positions")
        repeat(steps) {
            for (idx in positions.size - 1 downTo 1) {
                var h = positions[idx]
                var t = positions[idx-1]
                println("h=$h, r=$t")
                h.x += dx
                h.y += dy
                align(t, h)
                visited += positions[0].copy()
                debug(positions)
            }
        }
    }

    private fun align(t: Position, h: Position) {
        if (t == h) {
            println("  Overlapping")
            return
        }

        var dx = h.x - t.x
        var dy = h.y - t.y

        // If still touching, do nothing.
        if (dx in -1..1 && dy in -1..1) {
            println("  Touching")
            return
        }

        if (dx > 1) dx = 1
        if (dx < -1) dx = -1
        if (dy > 1) dy = 1
        if (dy < -1) dy = -1

        t.x += dx
        t.y += dy
        println("  Moving dx=$dx, dy=$dy")
    }

    private fun parse(): List<Command> {
        val commands = Files.readAllLines(Path.of("9.txt")).map {
            val ps = it.split(" ")
            Command(
                valueOf(ps[0]), ps[1].toInt()
            )
        }
        return commands
    }

    @Test
    fun part2() {
    }
}
