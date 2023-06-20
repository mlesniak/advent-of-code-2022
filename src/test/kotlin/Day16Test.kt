package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

data class State(
    val valves: Map<String, Valve>,
    val current: String,
    val openValves: Set<Valve> = emptySet(),
    val minute: Int = 1,
    val releasedPressure: Int = 0,
    val sumReleased: Int = 0,
) {

    override fun toString(): String {
        val releasingPressure = openValves.sumOf(Valve::rate)

        return """
            
            == Minute $minute ==
            Current $current
            Open    ${openValves.map(Valve::name)}
            Press.  $releasingPressure
            Avail.  ${valves[current]!!.connectionsTo.map(Valve::name)}
            Sum     $sumReleased
        """.trimIndent()
    }

    fun print(): State {
        println(this)
        return this
    }

    fun nextStates(): List<State> {
        if (valves[current] == null) {
            println("XXX No valve for $current")
        }
        val neighbors = valves[current]!!.connectionsTo
        return neighbors.map { move(it.name) } + open()
    }

    fun pass(): State {
        print()
        println("Pass")
        return this.copy(
            minute = minute + 1,
            sumReleased = sumReleased + releasedPressure,
        )
    }

    fun open(): State {
        return this.copy(
            valves = valves,
            current = current,
            openValves = openValves + valves[current]!!,
            minute = minute + 1,
            releasedPressure = releasedPressure + valves[current]!!.rate,
            sumReleased = sumReleased + releasedPressure + valves[current]!!.rate
        )
    }

    fun move(name: String): State {
        return this.copy(
            valves = valves,
            current = name,
            openValves = openValves,
            minute = minute + 1,
            releasedPressure = releasedPressure,
            sumReleased = sumReleased + releasedPressure,
        )
    }
}

data class Valve(
    val name: String,
    val rate: Int,
) {
    val connectionsTo: MutableSet<Valve> = mutableSetOf()
    private var locked = false

    fun add(v: Valve) {
        check(!locked)
        connectionsTo.add(v)
    }

    fun lock() {
        locked = true
    }

    override fun toString(): String {
        val cons = connectionsTo.map { it.name }
        return "$name=${String.format("%2d", rate)} $cons)"
    }
}

class Day16Test {
    private val startingValve = "AA"

    @Test
    fun part1() {
        val valves = parseInput()
        println("Initial valves")
        valves.values.forEach(::println)
    }

    private fun parseInput(): Map<String, Valve> {
        val lines = Files.readAllLines(Path.of("16.txt"))
        val parts = lines.map { it.split(Pattern.compile(" "), 10) }

        val connections = mutableMapOf<String, List<String>>()
        parts.forEach { part ->
            val name = part[1]
            val tunnels = part[9].split(",").map(String::trim)
            connections += name to tunnels
        }

        val valves = parts.associate { part ->
            val name = part[1]
            val rate = part[4].split("=")[1].replace(";", "").toInt()
            name to Valve(name, rate)
        }

        valves.values.forEach { valve ->
            connections[valve.name]!!.forEach { valve.add(valves[it]!!) }
        }

        // TODO(mlesniak) Removal does not work correctly.
        // // Hardcore recursive approach.
        // var replaced = true
        // while (replaced) {
        //     replaced = false
        //     valves.values.forEach { valve ->
        //         val tmp = valve
        //             .connectionsTo
        //             .filter { n -> n.rate == 0 }
        //         if (tmp.isNotEmpty()) {
        //             replaced = true
        //         }
        //
        //         tmp.forEach { n ->
        //             valve.connectionsTo.remove(n)
        //             n.connectionsTo
        //                 .filter { c -> c.name != valve.name }
        //                 .forEach { c -> valve.add(c) }
        //         }
        //     }
        // }
        //
        // return valves.filter { it.value.rate > 0 || it.key == startingValve }
        return valves
    }

    @Test
    fun part2() {
    }
}

//
// initialState
//     .move("DD")
//     .open()
//     .move("CC")
//     .move("BB")
//     .open()
//     .move("AA")
//     .move("II")
//     .move("JJ")
//     .open()
//     .move("II")
//     .move("AA")
//     .move("DD")
//     .move("EE")
//     .move("FF")
//     .move("GG")
//     .move("HH")
//     .open()
//     .move("GG")
//     .move("FF")
//     .move("EE")
//     .open()
//     .move("DD")
//     .move("CC")
//     .open()
//     .pass()
//     .pass()
//     .pass()
//     .pass()
//     .pass()
//     .print()