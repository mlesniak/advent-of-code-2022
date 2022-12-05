package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.util.Stack

class Day5Test {
    @Test
    fun part1() {

        val lines = readLineGroups("5.txt")
        val stackDesc = lines[0]
            .dropLast(1)
            .map {
                val sb = StringBuilder()
                for (i in 1 until it.length step 4) {
                    sb.append(it[i])
                }
                sb.toString()
            }
        val stacks = Array<Stack<Char>>(stackDesc.last().length) { Stack() }
        for (line in stackDesc) {
            for (c in line.indices) {
                if (line[c] == ' ') {
                    continue
                }
                stacks[c] += line[c]
            }
        }
        stacks.map { it.reverse() }

        parseCommands(lines[1], stacks)

        val res = stacks.map {
           it.pop()
        }.joinToString("")
        println(res)
    }

    private fun parseCommands(commands: List<String>, stacks: Array<Stack<Char>>) {
        commands.forEach { command ->
            val cs = command.split(" ").slice(listOf(1, 3, 5)).map { it.toInt() }
            val num = cs[0]
            val from = cs[1]
            val to = cs[2]
            repeat(num) {
                val elem = stacks[from-1].pop()
                stacks[to-1].push(elem)
            }
        }
    }

    @Test
    fun part2() {
    }
}
