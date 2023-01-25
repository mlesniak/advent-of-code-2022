package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.absoluteValue

data class Sensor(
    val pos: Pos,
    val beacon: Pos,
) {
    val manhattanDistance: Int;

    init {
        manhattanDistance = (pos.x - beacon.x).absoluteValue + (pos.y - beacon.y).absoluteValue
    }

    override fun toString(): String {
        return "Sensor(pos=$pos, beacon=$beacon, manhattanDistance=$manhattanDistance)"
    }
}

class Day15Test {
    @Test
    fun part1() {
        val row = 10

        val sensors = Files
            .readAllLines(Path.of("15.txt"))
            .map(::parseSensor)
        // sensors.forEach(::println)

        val covered = mutableSetOf<Int>()
        sensors.forEach { s ->
            // if (s.pos.y > row && s.pos.y - s.manhattanDistance > row) {
            //     return@forEach
            // }
            // if (s.pos.y < row && s.pos.y + s.manhattanDistance < row) {
            //     return@forEach
            // }

            // val remaining =
            //     if (s.pos.y > row) {
            //         row - (s.pos.y - (s.manhattanDistance))
            //     } else {
            //         (s.pos.y + (s.manhattanDistance)) - row
            //     } - 1

            // println("$s $remaining")
            // for (xn in (s.pos.x - remaining)..(s.pos.x + remaining)) {
            //     println("  $xn")
            //     covered += xn
            // }
        }

        covered.toList().sorted().forEach(::println)
        println("Size: ${covered.size}")
    }

    @Test
    fun part2() {
    }

    private fun parseSensor(line: String): Sensor {
        // Sensor at x=20, y=1: closest beacon is at x=15, y=3
        val parts = line.split("=")
        return Sensor(
            Pos(parts[1].split(",")[0].toInt(), parts[2].split(":")[0].toInt()),
            Pos(parts[3].split(",")[0].toInt(), parts[4].toInt())
        )
    }
}

fun main() {
    Day15Test().part1()
}