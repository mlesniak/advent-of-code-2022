package com.mlesniak.changeme

import com.mlesniak.changeme.CpuCommandType.AddX
import com.mlesniak.changeme.CpuCommandType.NoOp
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

enum class CpuCommandType(val cycles: Int) {
    NoOp(1),
    AddX(2);
}

data class CpuCommand(
    val type: CpuCommandType,
    val arg: Int? = null
)

class Day10Test {
    @Test
    fun part1() {
        val commands = Files
            .readAllLines(Path.of("10.txt"))
            .filter { it.isNotBlank() }
            .map {
                val ps = it.split(" ")
                when (val c = ps[0]) {
                    "noop" -> CpuCommand(NoOp)
                    "addx" -> CpuCommand(AddX, ps[1].toInt())
                    else -> throw IllegalArgumentException("? $c")
                }
            }

        val xs = simulator(commands)
        xs.forEachIndexed { idx, v ->
            val cycle = idx+1
            println("$cycle x=$v")
        }

        val picks = listOf(20, 60, 100, 140, 180, 220)
        val res = picks.sumOf { p ->
            println("p=$p ${xs[p-1]}")
            p * xs[p - 1]
        }
        println(res)
    }

    // Return value of X at each cycle
    private fun simulator(commands: List<CpuCommand>): List<Int> {
        var x = 1;
        var cycle = 1;
        var xs = mutableListOf<Int>()

        for (com in commands) {
            println("--- COM $com")
            println("x=$x")
            repeat(com.type.cycles) {
                println("cycle $cycle")
                xs += x
                cycle++
            }
            when (com.type) {
                NoOp -> {}
                AddX -> {
                    x += com.arg!!
                }
            }
            println("after com: x=$x")
        }

        return xs
    }

    @Test
    fun part2() {
    }
}
