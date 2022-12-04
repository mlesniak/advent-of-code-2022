package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import org.junit.platform.engine.support.discovery.SelectorResolver.Match.partial
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

    private fun IntRange.partial(other: IntRange): Boolean {
        return this.last >= other.first && this.first <= other.first
    }

    @Test
    fun part2() {
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
            .count { partial(it) }
        println(lines)
    }

    private fun partial(ranges: List<IntRange>): Boolean {
        val a = ranges[0]
        val b = ranges[1]

        return a.partial(b) || b.partial(a)
    }
}

