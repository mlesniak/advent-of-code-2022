package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class Day12Test {
    @Test
    fun part1() {
        val grid = Files
            .readAllLines(Path.of("12.txt"))
            .map { line ->
               line.toCharArray()
            }
            .toTypedArray()
        grid.forEach { row -> println(row) }

    }

    @Test
    fun part2() {
    }
}
