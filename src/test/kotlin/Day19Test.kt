package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

enum class Material {
    Ore,
    Clay,
    Obsidian,
    Geode,
}

data class Robot(
    val oreCost: Int?,
    val clayCost: Int? = null,
    val obsidianCost: Int? = null,
)

data class Blueprint(
    val id: Int,
    val costs: Map<Material, Robot>,
)

class Day19Test {
    @Test
    fun part1() {
        val blueprints = Files.readAllLines(Path.of("19.txt"))
            .filter(String::isNotEmpty)
            .map { it.split(" ") }
            .map { line ->
                // line.forEachIndexed { idx, s ->
                //     println("($idx $s)")
                // }

                val ore = Robot(line[6].toInt())
                val clay = Robot(line[12].toInt())
                val obsidian = Robot(line[18].toInt(), line[21].toInt())
                val geode = Robot(line[27].toInt(), null, line[30].toInt())
                Blueprint(line[1].split(":")[0].toInt(), mapOf(
                    Material.Ore to ore,
                    Material.Clay to clay,
                    Material.Obsidian to obsidian,
                    Material.Geode to geode,
                ))
            }
        blueprints.forEach(::println)
    }
}
