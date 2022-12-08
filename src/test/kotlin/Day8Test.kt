package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.lang.Integer.max
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
        val grid = Files
            .readAllLines(Path.of("8.txt"))
            .map { it.map { c -> c.code - '0'.code  } }

        // There's a functional solution here as well, similar
        // to the grid functions I did last year for AoC. Not
        // enough time that I'd like to spend for now.
        var maxScore = 0
        for (row in 1 until grid.size - 1) {
            for (col in 1 until grid[0].size - 1) {
                val treeHeight = grid[row][col]
                // println("Checking $col / $row with height $treeHeight")

                var leftCount = 0
                for (left in col - 1 downTo  0) {
                    leftCount++
                    if (grid[row][left] >= treeHeight) {
                        break
                    }
                }

                var rightCount = 0
                for (right in (col+1) until grid[0].size) {
                    rightCount++
                    if (grid[row][right] >= treeHeight) {
                        break
                    }
                }

                var topCount = 0
                for (top in (row-1) downTo  0) {
                    topCount++
                    if (grid[top][col] >= treeHeight) {
                        break
                    }
                }

                var bottomCount = 0
                for (bottom in (row+1) until grid.size) {
                    bottomCount++
                    if (grid[bottom][col] >= treeHeight) {
                        break
                    }
                }

                val scenicScore = leftCount * rightCount * topCount * bottomCount
                // println("left=$leftCount")
                // println("right=$rightCount")
                // println("top=$topCount")
                // println("bottom=$bottomCount")
                // println("Count=$scenicScore")
                maxScore = max(maxScore, scenicScore)
            }
        }

        println(maxScore)
    }
}
