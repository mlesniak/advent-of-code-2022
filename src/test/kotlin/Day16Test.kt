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
        val compressedValves = floydWarshall(initialValves)
        compressedValves.values.forEach(::println)
    }

    // Used to compress the graph by removing all empty nodes and updating the distances.
    // Wikpedia: https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
    private fun floydWarshall(nodes: Map<String, Valve>): Map<String, Valve> {
        val dists = mutableMapOf<String, Int>()
        // Initialize to infinity.
        nodes.values.forEach { v1 ->
            nodes.values.forEach { v2 ->
                dists["${v1.name}-${v2.name}"] = Int.MAX_VALUE
            }
        }

        nodes.values.forEach { v1 ->
            v1.connections.forEach { edge ->
                dists["${v1.name}-${edge.to.name}"] = edge.cost
            }
        }

        nodes.values.forEach { v ->
            dists["${v.name}-${v.name}"] = 0
        }

        nodes.values.forEach { k ->
            nodes.values.forEach { i ->
                nodes.values.forEach { j ->
                    val ik = dists["${i.name}-${k.name}"]!!
                    val kj = dists["${k.name}-${j.name}"]!!
                    val ij = dists["${i.name}-${j.name}"]!!
                    if (ik != Int.MAX_VALUE && kj != Int.MAX_VALUE && ik + kj < ij) {
                        dists["${i.name}-${j.name}"] = ik + kj
                    }
                }
            }
        }

        // Create the new graph based on the computed distances.
        val newNodes = mutableMapOf<String, Valve>()
        nodes.values.forEach { v1 ->
            val newConnections = mutableSetOf<Edge>()
            nodes.values.forEach { v2 ->
                val dist = dists["${v1.name}-${v2.name}"]!!
                if (dist != Int.MAX_VALUE) {
                    newConnections.add(Edge(dist, v2))
                }
            }
            newNodes[v1.name] = Valve(v1.name, v1.rate).apply { connections.addAll(newConnections) }
        }

        // Collect all nodes which have an empty rate.
        val emptyNodes = newNodes.values.filter { it.rate == 0 && it.name != startingValve }.map { it.name }
        emptyNodes.forEach { newNodes.remove(it) }

        // Remove all empty nodes from the connections.
        newNodes.values.forEach { v ->
            v.connections.removeIf {
                emptyNodes.contains(it.to.name) || it.to.name == v.name || (it.to.name == startingValve)
            }
        }

        return newNodes
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
