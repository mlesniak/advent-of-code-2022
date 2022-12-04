package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.lang.Integer.max
import java.lang.Integer.min
import java.nio.file.Files
import java.nio.file.Path

class Day4Test {
    @Test
    fun part1() {
        val lines = Files
            .readAllLines(Path.of("4.txt"))
            .map { it.split(",") }
            .map {
                it.map { range ->
                    val parts = range.split("-")
                    val start = parts[0].toInt()
                    val end = parts[1].toInt()
                    start..end
                }
            }
            .count { inside(it) }
        println(lines)
    }

    private fun inside(ranges: List<IntRange>): Boolean {
        val a = ranges[0]
        val b = ranges[1]

        return a.inside(b) || b.inside(a)
    }

    private fun IntRange.inside(other: IntRange): Boolean {
        return this.first >= other.first && this.last <= other.last
    }

    @Test
    fun part2() {
    }
}

