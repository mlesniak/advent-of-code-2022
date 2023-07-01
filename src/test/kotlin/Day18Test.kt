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

data class SideCount(var count: Int, val neighbors: MutableSet<Coordinate>) {
    fun connectedTo(c: Coordinate) {
        if (neighbors.contains(c)) {
            return
        }
        count--
        neighbors.add(c)
    }
}

// https://adventofcode.com/2022/day/18
// We have all the time we want and need
// to formulate a beautiful solution.
//
// Cultivate a love for learning.
class Day18Test {
    @Test
    fun part1() {
        val input = Files.readAllLines(Path.of("18.txt")).map(Coordinate.Companion::from)

        // Use a set to store the coordinates using their string representation
        // for fast lookups of existing coordinates.
        val coordinates = input.map { it.toString() }.toSet()
        println(coordinates)

        // For every cube, start with six open sides. We iterate over all sides and reduce
        // the number of open sides based on the existence of potential neighbors which
        // are directly adjacent (i.e. not diagonally connected).
        val sideCounts = input.associateWith { 6 }.toMutableMap()
        for (coordinate in input) {
            for (direction in listOf(-1, 1)) {
                // Either x, y or z changes.
                val x = Coordinate(coordinate.x + direction, coordinate.y, coordinate.z)
                val y = Coordinate(coordinate.x, coordinate.y + direction, coordinate.z)
                val z = Coordinate(coordinate.x, coordinate.y, coordinate.z + direction)
                if (coordinates.contains(x.toString())) {
                    sideCounts[coordinate] = sideCounts[coordinate]!! - 1
                }
                if (coordinates.contains(y.toString())) {
                    sideCounts[coordinate] = sideCounts[coordinate]!! - 1
                }
                if (coordinates.contains(z.toString())) {
                    sideCounts[coordinate] = sideCounts[coordinate]!! - 1
                }
            }
        }

        // Sum number of still open sides.
        val result = sideCounts.values.sum()
        println(result)
    }
}
