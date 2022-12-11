package com.mlesniak.changeme

import org.junit.jupiter.api.Test

enum class Operand(val s: String) {
    ADD("+"),
    MUL("*");

    companion object {
        fun from(t: String): Operand =
            values().find { it.s == t }!!
    }
}

data class Operation(
    val operand: Operand,
    // be pragmatic:
    // null means "old"
    val param: Int?,
)

data class Monkey(
    val items: MutableList<Int>,
    val opearation: Operation,

    val div: Int,
    val divTrue: Int,
    val divFalse: Int
)

class Day11Test {
    @Test
    fun part1() {
        val groups = readLineGroups("11.txt")
            .map { group -> group.map { it.trim() } }
            .map { parseGroup(it) }

        groups.forEach { l -> println(l) }
    }

    private fun parseGroup(lines: List<String>): Monkey {
        val items = lines[1].split(":")[1].split(",").map { it.trim().toInt() }.toMutableList()
        val ops = lines[2].split("old", limit = 2)[1].trim().split(" ")
        val operation = Operation(
            Operand.from(ops[0]),
            if (ops[1] == "old") null else ops[1].toInt()
        )
        val div = lines[3].split(" ")[3].toInt()
        val divTrue = lines[4].split(" ")[5].toInt()
        val divFalse = lines[5].split(" ")[5].toInt()

        return Monkey(
            items,
            operation,
            div,
            divTrue,
            divFalse
        )
    }

    @Test
    fun part2() {
    }
}
