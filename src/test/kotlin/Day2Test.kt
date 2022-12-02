package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Path

class Day2Test {
    // Pragmatic solution w/o enums.
    private fun score(strategy: List<String>): Int {
        val opp = strategy[0]
        val you = strategy[1]

        return when (opp) {
            // A Rock, B Paper, C Scissor
            "A" -> {
                when (you) {
                    // X Rock / 1, Y Paper / 2, Z Scissor / 3
                    // 6 win, 3 draw, 0 lost
                    "X" -> 3 + 1
                    "Y" -> 6 + 2
                    "Z" -> 0 + 3
                    else -> throw IllegalArgumentException("you=$you")
                }
            }
            "B" -> {
                when (you) {
                    "X" -> 0 + 1
                    "Y" -> 3 + 2
                    "Z" -> 6 + 3
                    else -> throw IllegalArgumentException("you=$you")
                }
            }
            "C" -> {
                when (you) {
                    "X" -> 6 + 1
                    "Y" -> 0 + 2
                    "Z" -> 3 + 3
                    else -> throw IllegalArgumentException("you=$you")
                }
            }
            else -> throw IllegalArgumentException("opp=$opp")
        }
    }

    @Test
    fun part1() {
        val lines = Files
            .readAllLines(Path.of("2.txt"))
            .map { it.split(" ") }
            .sumOf { score(it) }
        println(lines)
    }

    @Test
    fun part2() {
    }
}
