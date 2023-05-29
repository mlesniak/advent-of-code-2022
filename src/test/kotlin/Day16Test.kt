package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

data class Valve(
    val name: String,
    val rate: Int,
) {
    private val connectionsTo: MutableList<Valve> = mutableListOf()
    private var locked = false

    fun add(v: Valve): Boolean {
        check(!locked)
        return connectionsTo.add(v)
    }

    fun lock() {
        locked = true
    }

    override fun toString(): String {
        val cons = connectionsTo.map { it.name }
        return "Valve(name='$name', rate=$rate, connectionsTo=$cons)"
    }
}

class Day16Test {
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
            valve.lock()
        }
        return valves
    }

    @Test
    fun part2() {
    }
}
