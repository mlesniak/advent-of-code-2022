package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.lang.Integer.max
import java.lang.Integer.min
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

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
    // @Test
    // fun part1() {
    //     // val row = 11
    //     // val row = 2000000
    //
    //     val sensors = Files
    //         .readAllLines(Path.of("15.txt"))
    //         .filter(String::isNotBlank)
    //         .map(::parseSensor)
    //     // sensors.forEach(::println)
    //
    //     // you'd like to count the number of positions a beacon cannot possibly exist.
    //     // Use sensors with max manhattan distance.
    //     val m = 4000000
    //
    //     val minx = sensors.minOfOrNull { it.pos.x - it.manhattanDistance }!!
    //     val maxx = sensors.maxOfOrNull { it.pos.x + it.manhattanDistance }!!
    //     // println(minx)
    //     // println(maxx)
    //     val miny = kotlin.math.max(0, sensors.minOfOrNull { it.pos.y - it.manhattanDistance }!!)
    //     val maxy = min(sensors.maxOfOrNull { it.pos.y + it.manhattanDistance }!!, m)
    //
    //     val potentials = mutableListOf<Pos>()
    //     for (row in miny..maxy) {
    //         // println("\n\n--- ROW $row")
    //         println(row)
    //         val possibleBeacon = mutableSetOf<Int>()
    //         for (x in minx..maxx) {
    //             // println("x=$x")
    //             // A beacon can't exist, if the manhanttan distance of x,y and a potential
    //             // sensor is lower or equal than the manhattan distance of that sensor.
    //             val potentialPos = Pos(x, row)
    //             // If all sensors have a manhattan distance larger than the pos, the pos
    //             // can't possibly be a beacon.
    //             val beaconPossible = sensors.all { s ->
    //                 val potentialBaconDistance = s.pos.manhattanDistance(potentialPos)
    //                 // println("  s=$s md=$potentialBaconDistance")
    //                 s.manhattanDistance < potentialBaconDistance
    //             }
    //             if (beaconPossible) {
    //                 // println(" found $x")
    //                 possibleBeacon += x
    //             }
    //         }
    //
    //         // Remove beacons itself.
    //         sensors.forEach { s ->
    //             if (s.beacon.y == row) {
    //                 possibleBeacon -= s.beacon.x
    //             }
    //             if (s.pos.y == row) {
    //                 possibleBeacon -= s.pos.x
    //             }
    //         }
    //
    //         // println("Beacon can't exist at:")
    //         // covered.toList().sorted().forEach(::println)
    //         possibleBeacon.forEach { x ->
    //             if (x in 0..m && row in 0..m) {
    //                 val f = Pos(x, row)
    //                 potentials += f
    //                 val res = f.x * m + f.y
    //                 println("FOUND $f with $res")
    //             }
    //         }
    //     }
    //
    //     // val m = 20
    //     val candidates = potentials.filter { pos ->
    //         pos.x in 0..m && pos.y in 0..m
    //     }
    //     candidates.forEach(::println)
    //     val f = candidates[0]
    //     val res = f.x * 4000000 + f.y
    //     println(res)
    // }

    @Test
    fun part2() {
        val sensors = Files
            .readAllLines(Path.of("15.txt"))
            .filter(String::isNotBlank)
            .map(::parseSensor)

        val rows = mutableMapOf<Int, R>()

        // For every sensor, collect all reachable rows.
        // For each reachable row, collect ranges
        sensors.forEach { sensor ->
            // println("--- $sensor")
            val potRows = reachableRows(sensor)
            potRows.forEach { row ->
                val covered = sensorCovered(sensor, row)
                val cr = rows[row] ?: R()
                cr.add(covered)
                rows[row] = cr
                // println("$row: $covered")
            }
        }

        // println(rows.size)
        // rows.forEach { (row, ranges) ->
        //     println("\n*** $row ***")
        //     println(ranges)
        // }
        // val row = 2_000_000
        // // val row = 10
        // println("row=$row")
        // println(rows[row]!!.size)
        // println(rows[row])

        val maxRow = 4_000_000
        // val maxRow = 20
        val scores = rows
            .filter { (k, _) -> k in 0..maxRow }
            .filter { (k, v) -> v.count() > 1 }
            // .forEach { (k, v) ->
            //     println("=== $k\n$v")
            // }
            .map { (k,v) ->
                println("$k $v")
                val r = BigDecimal(v.gap) * BigDecimal(4_000_000) + BigDecimal(k)
                r
            }
        println(scores)

        // === 2650264
        // -219026..2638484
        // 2638486..4541698
    }

    // Can be part of the sensor class.
    private fun sensorCovered(sensor: Sensor, row: Int): IntRange {
        val delta = ((sensor.pos.y - row).absoluteValue - sensor.manhattanDistance).absoluteValue
        val x = sensor.pos.x
        return (x - delta)..(x + delta)
    }

    private fun reachableRows(sensor: Sensor): IntRange {
        val y = sensor.pos.y
        val md = sensor.manhattanDistance
        return (y - md)..(y + md)
    }

    private fun parseSensor(line: String): Sensor {
        // Sensor at x=20, y=1: closest beacon is at x=15, y=3
        val parts = line.split("=")
        return Sensor(
            Pos(parts[1].split(",")[0].toInt(), parts[2].split(":")[0].toInt()),
            Pos(parts[3].split(",")[0].toInt(), parts[4].toInt())
        )
    }

    @Test
    fun `single range in empty R`() {
        val sut = R()
        sut.add(1..10)
        assertEquals(1, sut.count())
    }

    @Test
    fun `continous range`() {
        val sut = R()
        sut.add(1..10)
        sut.add(11..20)
        assertEquals(1, sut.count())
        println(sut)
    }

    @Test
    fun `continous overlapping range`() {
        val sut = R()
        sut.add(1..10)
        sut.add(5..20)
        assertEquals(1, sut.count())
        println(sut)
    }

    @Test
    fun `continous including range`() {
        val sut = R()
        sut.add(1..10)
        sut.add(5..8)
        assertEquals(1, sut.count())
        assertEquals(1, sut.first)
        assertEquals(10, sut.last)
    }

    @Test
    fun `continous overlapping left range`() {
        val sut = R()
        sut.add(5..20)
        sut.add(1..10)
        assertEquals(1, sut.count())
        assertEquals(1, sut.first)
        assertEquals(20, sut.last)
        println(sut)
    }

    @Test
    fun `combine 1`() {
        val sut = R()
        println(sut.combine(1..10, 11..20))
        println(sut.combine(11..20, 1..10))

        println(sut.combine(1..10, 15..20))
        println(sut.combine(15..20, 1..10))

        println(sut.combine(1..10, 5..20))
        println(sut.combine(5..20, 1..10))

        println(sut.combine(5..20, 1..10))
        println(sut.combine(5..15, 10..13))
    }

    @Test
    fun `test simplification`() {
        val sut = R()

        sut.add(1..10)
        println(sut)
        sut.add(20..30)
        println(sut)
        sut.add(11..19)
        println(sut)
        sut.add(-10..-5)
        println(sut)

        sut.add(-10..-1)
        println(sut)
    }
}

// Memory efficient custom range operator
class R {
    private val ranges = mutableListOf<IntRange>()

    val gap: Int
        get() {
            return ranges[0].last +  1
        }

    val first: Int
        get() {
            assert(ranges.size == 1)
            return ranges[0].first
        }

    val last: Int
        get() {
            assert(ranges.size == 1)
            return ranges[0].last
        }

    val size: Int
        get() {
            assert(ranges.size == 1)
            return last - first
        }

    fun add(r: IntRange) {
        if (ranges.isEmpty()) {
            ranges += r
            return
        }

        ranges += r
        simplify()
    }

    private fun simplify() {
        // Iterate through ranges until nothing changes
        var rep = true
        loop@ while (rep) {
            rep = false
            for (i in 0 until ranges.size) {
                for (j in i + 1 until ranges.size) {
                    val r1 = ranges[i]
                    val r2 = ranges[j]
                    // println("Comparing $i=$r1 and $j=$r2")
                    val k = combine(r1, r2)
                    if (k.size == 1) {
                        rep = true
                        ranges.removeAt(j)
                        ranges.removeAt(i)
                        ranges += k
                        continue@loop
                    }
                }
            }
        }
    }

    fun combine(r1: IntRange, r2: IntRange): List<IntRange> {
        if (r1.last + 1 < r2.first) {
            return listOf(r1, r2)
        }

        if (r2.last + 1 < r1.first) {
            return listOf(r2, r1)
        }

        return listOf((min(r1.first, r2.first))..max(r1.last, r2.last))
    }

    fun count(): Int {
        return ranges.size
    }

    override fun toString(): String {
        return ranges.joinToString("\n")
    }
}
