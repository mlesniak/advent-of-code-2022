package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

data class Sensor(
    val pos: Pos,
    val beacon: Pos,
) {
    val manhattanDistance: Int = pos.manhattanDistance(beacon);

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
        // you'd like to count the number of positions a beacon cannot possibly exist.
        // Use sensors with max manhattan distance.

        val minx = sensors.minOfOrNull { it.pos.x - it.manhattanDistance }!!
        val maxx = sensors.maxOfOrNull { it.pos.x + it.manhattanDistance }!!
        println(minx)
        println(maxx)

        for (x in minx..maxx) {
            // println("x=$x")
            // A beacon can't exist, if the manhanttan distance of x,y and a potential
            // sensor is lower or equal than the manhattan distance of that sensor.
            val potentialPos = Pos(x, row)
            // If all sensors have a manhattan distance larger than the pos, the pos
            // can't possibly be a beacon.
            val beaconPossible = sensors.all { s ->
                val potentialBaconDistance = s.pos.manhattanDistance(potentialPos)
                // println("  s=$s md=$potentialBaconDistance")
                s.manhattanDistance < potentialBaconDistance
            }
            if (!beaconPossible) {
                // println(" found $x")
                covered += x
            }
        }

        // Remove beacons itself.
        sensors.forEach { s ->
            if (s.beacon.y != row) {
                return@forEach
            }
            covered -= s.beacon.x
        }

        // println("Beacon can't exists at:")
        // covered.toList().sorted().forEach(::println)
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