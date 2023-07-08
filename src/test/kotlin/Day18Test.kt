package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

data class Coordinate(val x: Double, val y: Double, val z: Double) : Comparable<Coordinate> {
    companion object {
        fun from(s: String): Coordinate {
            val parts = s.split(",")
            return Coordinate(parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
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

    override fun toString(): String {
        return "[$x,$y,$z]"
    }
}

data class Rectangle(val a: Coordinate, val b: Coordinate, val c: Coordinate, val d: Coordinate): Comparable<Rectangle> {
    constructor(cs: List<Coordinate>) : this(cs[0], cs[1], cs[2], cs[3])

    // Convenience function.
    val vertices: Set<Coordinate> = setOf(a, b, c, d)

    override fun compareTo(other: Rectangle): Int {
        return when {
            a < other.a -> -1
            a > other.a -> 1
            b < other.b -> -1
            b > other.b -> 1
            c < other.c -> -1
            c > other.c -> 1
            d < other.d -> -1
            d > other.d -> 1
            else -> 0
        }
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

        // println("Available sides for each cube")
        // for (cube in input) {
        //     println("${cube.key} -> ${cube.value}")
        // }

        // Transfer cube sides to actual rectangles.
        val allSides = mutableListOf<Rectangle>()
        for (cube in input) {
            val root = cube.key
            for (side in cube.value) {
                // println("Checking $side")
                // We can have three possible orientations, i.e. one
                // of the coordinates is the same.
                if (side.x != root.x) {
                    // println("side: $side, root: $root")
                    // println("x is different")
                    val xpos = (if (side.x < root.x) root.x - side.x else side.x - root.x) / 2.0

                    val vertices = listOf(-0.5, 0.5).flatMap { a ->
                        listOf(-0.5, 0.5).map { b ->
                            val x = (root.x + side.x)  / 2.0
                            val y = root.y + a
                            val z = root.z + b
                            Coordinate(x, y, z)
                        }
                    }
                    // println("Vertices: $vertices for $side and $root")
                    val rect = Rectangle(vertices)
                    allSides += rect
                }
                if (side.y != root.y) {
                    // println("y is different")
                    val ypos = (if (side.y < root.y) root.y - side.y else side.y - root.y) / 2.0
                    val vertices = listOf(-0.5, 0.5).flatMap { a ->
                        listOf(-0.5, 0.5).map { b ->
                            val x = side.x + a
                            val y = (side.y + root.y) / 2.0
                            val z = side.z + b
                            Coordinate(x, y, z)
                        }
                    }
                    val rect = Rectangle(vertices)
                    allSides += rect
                }
                if (side.z != root.z) {
                    // println("z is different")
                    val zpos = (if (side.z < root.z) root.z - side.z else side.z - root.z) / 2.0
                    val vertices = listOf(-0.5, 0.5).flatMap { a ->
                        listOf(-0.5, 0.5).map { b ->
                            val x = side.x + a
                            val y = side.y + b
                            val z = (side.z + root.z) / 2.0
                            Coordinate(x, y, z)
                        }
                    }
                    val rect = Rectangle(vertices)
                    allSides += rect
                }
            }
        }

        // allSides.forEach(::println)
        // println(allSides.size)

        // BFS part.

        // Find a side which is the farthest on the outside
        // to start scanning.
        val outmostSide = allSides.maxOrNull() ?: error("No sides found")
        println("Starting at $outmostSide")

        val queue = mutableListOf<Rectangle>()
        val visited = mutableSetOf<Rectangle>()
        queue += outmostSide

        // allSides.forEach(::println)
        println(allSides.size)

        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            visited += cur

            println("Checking $cur")

            // Find another rectangle in out list. This rectangle must have
            // two vertices in common with the current rectangle and not be
            // in the visited list.
            val next = allSides.filter { it != cur && it.vertices.intersect(cur.vertices).size == 2 && it !in visited }
            next.forEach(::println)

            queue.addAll(next)
        }

        println(visited.size)
    }
}
