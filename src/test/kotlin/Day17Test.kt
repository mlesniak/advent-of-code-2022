package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

enum class Movement {
    Left,
    Right;

    companion object {
        fun valueOf(c: Char): Movement {
            return when (c) {
                '<' -> Left
                '>' -> Right
                else -> throw IllegalArgumentException("Unknown movement $c")
            }
        }
    }
}

class Day17Test {
    @Test
    fun part1() {
        val movements = Files.readString(Path.of("17.txt"))
            .map { Movement.valueOf(it) }
        println(movements)
    }

    @Test
    fun part2() {
    }
}
