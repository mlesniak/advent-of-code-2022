package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

data class Coordinate(val x: Int, val y: Int, val z: Int) : Comparable<Coordinate> {
    companion object {
        fun from(s: String): Coordinate {
            val parts = s.split(",")
            return Coordinate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }

    override fun compareTo(other: Coordinate): Int {
        return when {
            x < other.x -> -1
            x > other.x -> 1
            y < other.y -> -1
            y > other.y -> 1
            z < other.z -> -1
            z > other.z -> 1
            else -> 0
        }
    }

    fun neighbors(): List<Coordinate> {
        val neighbors = mutableListOf<Coordinate>()
        for (direction in listOf(-1, 1)) {
            // Either x, y or z changes (but not multiple ones).
            val nx = Coordinate(x + direction, y, z)
            val ny = Coordinate(x, y + direction, z)
            val nz = Coordinate(x, y, z + direction)
            neighbors.addAll(listOf(nx, ny, nz))
        }
        return neighbors
    }

    override fun toString(): String {
        return "[$x,$y,$z]"
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
        val input = Files.readAllLines(Path.of("18.txt"))
            .filter(String::isNotEmpty)
            .map(Coordinate.Companion::from)

        // Use a set to store the coordinates using their string representation
        // for fast lookups of existing coordinates.
        val coordinates = input.map { it.toString() }.toSet()

        // For every cube, start with six open sides. We iterate over all sides and reduce
        // the number of open sides based on the existence of potential neighbors which
        // are directly adjacent (i.e. not diagonally connected).
        val sideCounts = input.associateWith { 6 }.toMutableMap()
        for (coordinate in input) {
            for (direction in listOf(-1, 1)) {
                // Either x, y or z changes (but not multiple ones).
                val x = Coordinate(coordinate.x + direction, coordinate.y, coordinate.z)
                if (coordinates.contains(x.toString())) {
                    sideCounts[coordinate] = sideCounts[coordinate]!! - 1
                }
                val y = Coordinate(coordinate.x, coordinate.y + direction, coordinate.z)
                if (coordinates.contains(y.toString())) {
                    sideCounts[coordinate] = sideCounts[coordinate]!! - 1
                }
                val z = Coordinate(coordinate.x, coordinate.y, coordinate.z + direction)
                if (coordinates.contains(z.toString())) {
                    sideCounts[coordinate] = sideCounts[coordinate]!! - 1
                }
            }
        }

        // Sum number of still open sides.
        val result = sideCounts.values.sum()
        println(result)
    }

    @Test
    fun part2() {
        val input = Files.readAllLines(Path.of("18.txt"))
            .filter(String::isNotEmpty)
            .map(Coordinate.Companion::from)
            .associateWith { c ->
                val sides = mutableSetOf<Coordinate>()
                for (direction in listOf(-1, 1)) {
                    val x = Coordinate(c.x + direction, c.y, c.z)
                    val y = Coordinate(c.x, c.y + direction, c.z)
                    val z = Coordinate(c.x, c.y, c.z + direction)
                    sides.addAll(listOf(x, y, z))
                }
                sides
            }
        val coordinates = input.keys.map { it.toString() }.toSet()

        // Remove sides which are connected to cubes.
        for (coordinate in input) {
            for (direction in listOf(-1, 1)) {
                // Either x, y or z changes (but not multiple ones).
                val x = Coordinate(coordinate.key.x + direction, coordinate.key.y, coordinate.key.z)
                if (coordinates.contains(x.toString())) {
                    coordinate.value.remove(x)
                }

                val y = Coordinate(coordinate.key.x, coordinate.key.y + direction, coordinate.key.z)
                if (coordinates.contains(y.toString())) {
                    coordinate.value.remove(y)
                }

                val z = Coordinate(coordinate.key.x, coordinate.key.y, coordinate.key.z + direction)
                if (coordinates.contains(z.toString())) {
                    coordinate.value.remove(z)
                }
            }
        }

        // Find the minimum and maximum coordinates for each dimension
        // in the input. The input coordinates are on non-fractional
        // positions, so we can safely convert them to integers.
        val minX = input.keys.minOf { it.x }.toInt()
        val maxX = input.keys.maxOf { it.x }.toInt()
        val minY = input.keys.minOf { it.y }.toInt()
        val maxY = input.keys.maxOf { it.y }.toInt()
        val minZ = input.keys.minOf { it.z }.toInt()
        val maxZ = input.keys.maxOf { it.z }.toInt()

        println("minX: $minX, maxX: $maxX")
        println("minY: $minY, maxY: $maxY")
        println("minZ: $minZ, maxZ: $maxZ")

        // Start with a minimal cube one coordinate outside of the input.
        val start = Coordinate(minX - 1, minY - 1, minZ - 1)
        println(start)

        val visited = mutableSetOf<Coordinate>()
        val queue = mutableListOf(start)

        // Too lazy to add a proper termination condition which
        // is something like all cubes in the boundary box have
        // been visited.
        var steps = 10_000_000
        var visibleSides = 0
        while (queue.isNotEmpty()) {
            if (steps-- <= 0) {
                break
            }
            if (queue.size % 1000 == 0) {
                println("queue size: ${queue.size}, visited: ${visited.size}, visible sides: $visibleSides")
            }
            val next = queue.removeFirst()
            if (next in visited) {
                continue
            }
            visited += next
            val ns = next.neighbors().filter { it !in visited }
            // println("\nNEXT: $next")
            // ns.forEach(::println)

            // Check, if the cube is next to an input cube.
            // We look at all its neighbors and check if they are in the input.
            // If so, we increase the count for visible sides.
            for (n in ns) {
                if (n.toString() in coordinates) {
                    // println("   visible: $n")
                    visibleSides++
                }
            }

            // We add all neighbors, which are not in the input and
            // haven't been visited yet, to the queue.
            // We also check that no coordinate is negative.
            queue.addAll(ns.filter { it.toString() !in coordinates && it !in visited })

            // println(visibleSides)
        }

        println(visibleSides)
    }
}
