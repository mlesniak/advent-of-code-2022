package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class Day6Test {
    @Test
    fun part1() {
        val input = Files.readString(Path.of("6.txt"))
        val markerLength = 4
        var res: String = ""
        for (i in 0..(input.length - markerLength)) {
            res = input.substring(i, i + markerLength)
            if (unique(markerLength, res)) {
                println(i + markerLength)
                break
            }
        }
    }

    private fun unique(n: Int, s: String): Boolean {
        return s.toSet().size == n
    }

    @Test
    fun part2() {
        val input = Files.readString(Path.of("6.txt"))
        val markerLength = 14
        var res: String = ""
        for (i in 0..(input.length - markerLength)) {
            res = input.substring(i, i + markerLength)
            if (unique(markerLength, res)) {
                println(i + markerLength)
                break
            }
        }
    }
}
