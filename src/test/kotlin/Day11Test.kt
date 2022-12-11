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
    val param: Long?,
)

data class Monkey(
    val items: MutableList<Long>,
    val opearation: Operation,

    val div: Long,
    val divTrue: Int,
    val divFalse: Int,
    var inspections: Long = 0,
)

class Day11Test {
    @Test
    fun part1() {
        val monkeys = readLineGroups("11.txt")
            .map { group -> group.map { it.trim() } }
            .map { parseGroup(it) }
        monkeys.forEach { l -> println(l) }

        repeat(20) { round ->
            println("--- ROUND $round")
            for (midx in monkeys.indices) {
                println("\nMonkey $midx")
                val m = monkeys[midx]
                while (m.items.isNotEmpty()) {
                    val itemVal = m.items.removeAt(0)
                    println("Inspecting $itemVal")
                    m.inspections++
                    val param = m.opearation.param ?: itemVal
                    var newVal = when (m.opearation.operand) {
                        Operand.ADD -> itemVal + param
                        Operand.MUL -> itemVal * param
                    }
                    println("  Level is now $newVal")
                    newVal /= 3
                    println("  Level is now $newVal")
                    if (newVal % m.div == 0L) {
                        println("$newVal is divisible by ${m.div}")
                        monkeys[m.divTrue].items += newVal
                        println("Throwing to ${m.divTrue}")
                    } else {

                        println("$newVal is not divisible by ${m.div}")
                        monkeys[m.divFalse].items += newVal
                        println("Throwing to ${m.divFalse}")
                    }
                }
            }
        }

        monkeys.forEachIndexed { index, monkey ->
            println("$index -> ${monkey.items} / ${monkey.inspections}")
        }
        val res = monkeys
            .sortedBy { it.inspections }
            .takeLast(2)
            .map { it.inspections }
            .fold(1L) { a, b -> a * b }
        println(res)
    }

    private fun parseGroup(lines: List<String>): Monkey {
        val items = lines[1].split(":")[1].split(",").map { it.trim().toLong() }.toMutableList()
        val ops = lines[2].split("old", limit = 2)[1].trim().split(" ")
        val operation = Operation(
            Operand.from(ops[0]),
            if (ops[1] == "old") null else ops[1].toLong()
        )
        val div = lines[3].split(" ")[3].toLong()
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
        val monkeys = readLineGroups("11.txt")
            .map { group -> group.map { it.trim() } }
            .map { parseGroup(it) }
        // monkeys.forEach { l -> println(l) }

        // https://en.wikipedia.org/wiki/Chinese_remainder_theorem
        val commonModulo = monkeys.map { it.div }.fold(1L) {a, b -> a*b}

        repeat(10_000) { round ->
            // println("--- ROUND $round")
            for (midx in monkeys.indices) {
                // println("\nMonkey $midx")
                val m = monkeys[midx]
                while (m.items.isNotEmpty()) {
                    val itemVal = m.items.removeAt(0)
                    // println("Inspecting $itemVal")
                    m.inspections++
                    val param = m.opearation.param ?: itemVal
                    var newVal = when (m.opearation.operand) {
                        Operand.ADD -> itemVal + param
                        Operand.MUL -> itemVal * param
                    }
                    // println("  Level is now $newVal")
                    newVal %= commonModulo
                    // println("  Level is now $newVal")
                    if (newVal % m.div == 0L) {
                        // println("$newVal is divisible by ${m.div}")
                        monkeys[m.divTrue].items += newVal
                        // println("Throwing to ${m.divTrue}")
                    } else {

                        // println("$newVal is not divisible by ${m.div}")
                        monkeys[m.divFalse].items += newVal
                        // println("Throwing to ${m.divFalse}")
                    }
                }
            }
        }

        // monkeys.forEachIndexed { index, monkey ->
        //     println("$index -> ${monkey.items} / ${monkey.inspections}")
        // }
        val res = monkeys
            .sortedBy { it.inspections }
            .takeLast(2)
            .map { it.inspections }
            .fold(1L) { a, b -> a * b }
        println(res)
    }
}
