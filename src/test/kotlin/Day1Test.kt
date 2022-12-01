package com.mlesniak.changeme

import org.junit.jupiter.api.Test

class Day1Test {
    @Test
    fun part1() {
        val lineGroup = readLineGroups("1.txt")
        val intGroup = lineGroup.map { group ->
            group.map { it.toInt() }
        }
        val maxCal = intGroup.maxOfOrNull { it.sum() }
        println(maxCal)
    }

    @Test
    fun part2() {
        val lineGroup = readLineGroups("1.txt")
        val intGroup = lineGroup.map { group ->
            group.map { it.toInt() }
        }
        val maxCal = intGroup.map { it.sum() }.sorted().reversed().take(3).sum()
        println(maxCal)
    }
}
