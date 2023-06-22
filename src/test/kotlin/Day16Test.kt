package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern


data class Valve(
    val name: String,
    val rate: Int,
) {
    val connections: MutableSet<Valve> = mutableSetOf()

    override fun toString(): String {
        val cons = connections.map { it.name }
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
            connections[valve.name]!!.forEach { valve.connections.add(valves[it]!!) }
        }

        return valves
    }

    @Test
    fun part2() {
    }
}
