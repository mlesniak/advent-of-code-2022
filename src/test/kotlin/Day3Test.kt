package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Path

class Day3Test {
    @Test
    fun part1() {
        val res = Files
            .readAllLines(Path.of("3.txt"))
            .map {
                val mid = it.length / 2
                val left = it.substring(0 until mid)
                val right = it.substring(mid)
                listOf(left.toSet(), right.toSet())
            }
            .map { findDuplicate(it) }
            .sumOf { toCode(it) }
        println(res)
    }

    private fun toCode(c: Char): Int {
        println(c)
        if (c in 'a'..'z') {
            return c.code - 'a'.code + 1
        }

        return c.code - 'A'.code + 1 + 26
    }

    private fun findDuplicate(list: List<Set<Char>>): Char {
        val chars = list[0].intersect(list[1])

        if (chars.size > 1) {
            throw IllegalStateException("More than one char remaining: $chars")
        }
        return chars.find { true }!!
    }

    @Test
    fun part2() {
    }
}
