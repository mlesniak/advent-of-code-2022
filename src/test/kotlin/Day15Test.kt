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
        // val row = 11
        // val row = 2000000

        val sensors = Files
            .readAllLines(Path.of("15.txt"))
            .filter(String::isNotBlank)
            .map(::parseSensor)
        // sensors.forEach(::println)

        // you'd like to count the number of positions a beacon cannot possibly exist.
        // Use sensors with max manhattan distance.

        val minx = sensors.minOfOrNull { it.pos.x - it.manhattanDistance }!!
        val maxx = sensors.maxOfOrNull { it.pos.x + it.manhattanDistance }!!
        // println(minx)
        // println(maxx)
        val miny = sensors.minOfOrNull { it.pos.y - it.manhattanDistance }!!
        val maxy = sensors.maxOfOrNull { it.pos.y + it.manhattanDistance }!!

        val potentials = mutableListOf<Pos>()
        for (row in miny..maxy) {
            // println("\n\n--- ROW $row")
            println(row)
            val possibleBeacon = mutableSetOf<Int>()
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
                if (beaconPossible) {
                    // println(" found $x")
                    possibleBeacon += x
                }
            }

            // Remove beacons itself.
            sensors.forEach { s ->
                if (s.beacon.y == row) {
                    possibleBeacon -= s.beacon.x
                }
                if (s.pos.y == row) {
                    possibleBeacon -= s.pos.x
                }
            }

            // println("Beacon can't exist at:")
            // covered.toList().sorted().forEach(::println)
            possibleBeacon.forEach { pot ->
               potentials += Pos(pot, row)
            }
        }

        val m = 20
        // val m = 4000000
        val candidates = potentials.filter { pos ->
            pos.x in 0..m && pos.y in 0..m
        }
        candidates.forEach(::println)
        val f = candidates[0]
        val res = f.x * 4000000 + f.y
        println(res)
    }

    @Test
    fun part2() {
        // See part 1 -- accidentally used part 1 method as well, oops.
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