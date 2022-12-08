package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class Day8Test {
    @Test
    fun part1() {
        val grid = Files
            .readAllLines(Path.of("8.txt"))
            .map { it.map { c -> c.code - '0'.code  } }

        var visibleTrees = grid.size * 2 + (grid[0].size - 2) * 2
        // println("Visible on edge: $visibleTrees")

        for (row in 1 until grid.size - 1) {
            for (col in 1 until grid[0].size - 1) {
                val treeHeight = grid[row][col]
                // println("Checking $col / $row with height $treeHeight")

                var visible = true
                for (left in 0 until col) {
                    if (grid[row][left] >= treeHeight) {
                        visible = false
                        break
                    }
                }
                if (visible) {
                    // println("Visible from left")
                    visibleTrees++
                    continue
                }

                visible = true
                for (right in (col+1) until grid[0].size) {
                    if (grid[row][right] >= treeHeight) {
                        visible = false
                        break
                    }
                }
                if (visible) {
                    // println("Visible from right")
                    visibleTrees++
                    continue
                }

                visible = true
                for (top in 0 until row) {
                    if (grid[top][col] >= treeHeight) {
                        visible = false
                        break
                    }
                }
                if (visible) {
                    // println("Visible from top")
                    visibleTrees++
                    continue
                }

                visible = true
                for (bottom in (row+1) until grid.size) {
                    if (grid[bottom][col] >= treeHeight) {
                        visible = false
                        break
                    }
                }
                if (visible) {
                    // println("Visible from bottom")
                    visibleTrees++
                    continue
                }
            }
        }

        println(visibleTrees)
    }

    @Test
    fun part2() {
    }
}
