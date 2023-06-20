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

    fun nextStates(): List<State> {
        val neighbors = valves[current]!!.connectionsTo
        return neighbors.map { move(it.name) } + open()
    }

    fun print(): State {
        println(this)
        return this
    }

    fun pass(): State {
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

    fun add(v: Valve) {
        connectionsTo.add(v)
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

        return valves
    }

    @Test
    fun part2() {
    }
}
