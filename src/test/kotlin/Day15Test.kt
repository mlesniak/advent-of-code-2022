package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

data class Sensor(
    val pos: Pos,
    val beacon: Pos,
)

class Day15Test {
    @Test
    fun part1() {
        val sensors = Files
            .readAllLines(Path.of("15.txt"))
            .map(::parseSensor)
        sensors.forEach { println(it) }
    }

    @Test
    fun part2() {
    }

    private fun parseSensor(line: String): Sensor {
        // Sensor at x=20, y=1: closest beacon is at x=15, y=3
        val parts = line.split("=")
        return Sensor(
            Pos(parts[1].split(",")[0].toInt(), parts[2].split(":")[0].toInt()),
            Pos(parts[3].split(",")[0].toInt(), parts[4].toInt()))
    }
}

fun main() {
    Day15Test().part1()
}