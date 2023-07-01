package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

data class Coordinate(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun from(s: String): Coordinate {
            val parts = s.split(",")
            return Coordinate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }

    override fun toString(): String {
        return "[$x,$y,$z]"
    }
}

// https://adventofcode.com/2022/day/18
// We have all the time we want and need
// to formulate a beautiful solution.
class Day18Test {
    @Test
    fun part1() {
        val input = Files.readAllLines(Path.of("18.txt")).map(Coordinate.Companion::from)
        input.forEach(::println)
    }
}
