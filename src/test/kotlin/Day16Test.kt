package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

data class Edge(
    val cost: Int,
    val to: Valve,
)

data class Valve(
    val name: String,
    val rate: Int,
) {
    val connections: MutableSet<Edge> = mutableSetOf()

    override fun toString(): String {
        val cons = connections.map { "${it.to.name}/${it.cost}" }
        return "$name=${String.format("%2d", rate)} $cons)"
    }
}

class Day16Test {
    private val startingValve = "AA"

    @Test
    fun part1() {
        val initialValves = parseInput()
        initialValves.values.forEach(::println)

        // TODO(mlesniak) Eliminate empty valves by using Floyd-Warshall algorithm.

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
            connections[valve.name]!!.forEach { valve.connections.add(Edge(1, valves[it]!!)) }
        }

        return valves
    }

    @Test
    fun part2() {
    }
}
