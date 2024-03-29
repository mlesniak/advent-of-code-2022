package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.absoluteValue

data class Pos(val x: Int, val y: Int) {
    fun manhattanDistance(p: Pos) =
        (x - p.x).absoluteValue + (y - p.y).absoluteValue
}

data class BfsState(
    val pos: Pos,
    val cost: Int = 0,
    val path: List<Char> = emptyList()
)

class Day12Test {
    private fun find(grid: Array<CharArray>, target: Char): Pos {
        grid.forEachIndexed { row, chars ->
            chars.forEachIndexed { col, c ->
                if (c == target) {
                    return Pos(col, row)
                }
            }
        }

        throw IllegalArgumentException("$target not found")
    }

    @Test
    fun part1() {
        val grid = Files
            .readAllLines(Path.of("12.txt"))
            .filter { it.isNotBlank() }
            .map { line ->
                line.toCharArray()
            }
            .toTypedArray()

        // BFS with visited.
        // State contains distance.
        val start = find(grid, 'S')
        val goal = find(grid, 'E')

        val states = mutableListOf(BfsState(start, cost = 0))
        val visited = mutableSetOf<Pos>()
        while (states.isNotEmpty()) {
            val cur = states.removeFirst()
            val v = grid[cur.pos.y][cur.pos.x]
            // println("cur=$cur val=$v")
            if (cur.pos == goal) {
                println("Solved: $cur")
                println(cur.cost)
                break
            }

            // Find next states.
            states += findNext(cur, v, grid, visited)
        }
    }

    private fun findNext(cur: BfsState, v: Char, grid: Array<CharArray>, visited: MutableSet<Pos>): List<BfsState> {
        val res = mutableListOf<BfsState>()
        val deltas = listOf(Pos(-1, 0), Pos(1, 0), Pos(0, -1), Pos(0, 1))

        deltas.forEach { delta ->
            val dx = cur.pos.x + delta.x
            val dy = cur.pos.y + delta.y
            val newPos = Pos(dx, dy)

            if (newPos in visited) {
                return@forEach
            }

            if (dx < 0 || dy < 0 || dx >= grid[0].size || dy >= grid.size) {
                return@forEach
            }

            val gv = grid[dy][dx]
            // if (gv == 'm') {
            //     println("M at $newPos")
            // }

            if (gv == 'E' && v == 'z') {
                res += BfsState(Pos(dx, dy), cur.cost + 1, cur.path + v + gv)
                return@forEach
            }
            if (gv == 'E') {
                return@forEach
            }

            if (v != 'S') {
                // the elevation of the destination square can be at most one higher than the elevation of your current square;
                // This also means that the elevation of the destination square can be much lower than the elevation of your current square.)

                // println("  looking at $gv")
                var ret = true
                if (gv.code - 1 == v.code) {
                    ret = false
                }
                if (gv.code <= v.code) {
                    ret = false
                }
                if (ret) {
                    return@forEach
                }

                // if ((gv.code - v.code).absoluteValue > 1) {
                //     return@forEach
                // }
            }

            val newState = BfsState(newPos, cur.cost + 1, cur.path + v)
            res += newState
            visited += newPos

            if (gv > v) {
                println("advanced to: $gv with $newState")
            }

        }

        return res
    }

    @Test
    fun part2() {
        val grid = Files
            .readAllLines(Path.of("12.txt"))
            .filter { it.isNotBlank() }
            .map { line ->
                line.toCharArray()
            }
            .toTypedArray()

        // BFS with visited.
        // State contains distance.
        val starts = findAll(grid, 'a')
        val goal = find(grid, 'E')

        val res = starts.minOfOrNull { start ->
            findGoal(grid, start, goal)
        }
        println(res)
    }

    private fun findGoal(
        grid: Array<CharArray>,
        start: Pos,
        goal: Pos
    ): Int {
        val states = mutableListOf(BfsState(start, cost = 0))
        val visited = mutableSetOf<Pos>()
        while (states.isNotEmpty()) {
            val cur = states.removeFirst()
            val v = grid[cur.pos.y][cur.pos.x]
            // println("cur=$cur val=$v")
            if (cur.pos == goal) {
                return cur.cost
            }

            // Find next states.
            states += findNext(cur, v, grid, visited)
        }

        // No path found. Ignoring it for now.
        return Int.MAX_VALUE
    }

    private fun findAll(grid: Array<CharArray>, target: Char): List<Pos> {
        val res = mutableListOf<Pos>()

        grid.forEachIndexed { row, chars ->
            chars.forEachIndexed { col, c ->
                if (c == target) {
                    res += Pos(col, row)
                }
            }
        }

        return res
    }
}
