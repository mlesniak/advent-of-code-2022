package com.mlesniak.changeme

import com.mlesniak.changeme.Day13Test.Result.Continue
import com.mlesniak.changeme.Day13Test.Result.Correct
import com.mlesniak.changeme.Day13Test.Result.Incorrect
import org.junit.jupiter.api.Test

/*
    // More correct, but pretty annoying to use version:
    abstract class E
    class Ls(vararg es: E) : E()
    class N(num: Int) : E()
    // [[1],[2,3,4],5,6,7]
    val root: Ls = Ls(Ls(N(1)), Ls(N(2), N(3), N(4)), N(5), N(6), N(7))
*/


// Ugly and pragmatic.
fun Any.isList() = this is L
fun Any.isNumber() = this is Int

class L(vararg es: Any) {
    val elems: List<Any> = es.toList()

    override fun toString(): String = "[${elems.joinToString(separator = ",")}]"

    companion object {
        fun parse(s: String) = internalParse(s).first

        private fun internalParse(s: String): Pair<L, Int> {
            val elems = mutableListOf<Any>()
            var i = 1
            while (i < s.length) {
                val c = s[i]
                when {
                    c.isDigit() -> {
                        var k = i + 1
                        while (s[k].isDigit()) {
                            k++
                        }
                        val num = s.substring(i until k)
                        elems += num.toInt()
                        i = k
                        continue
                    }

                    c == ']' -> {
                        return L(*(elems.toTypedArray())) to i
                    }

                    c == '[' -> {
                        val (elem, k) = internalParse(s.substring(i))
                        elems += elem
                        i += k + 1
                    }
                }
                i++
            }

            return L(*(elems.toTypedArray())) to i
        }
    }
}

class Day13Test {
    @Test
    fun part1() {
        val res =
            readLineGroups("13.txt")
                .mapIndexed { index, strings -> index to handleGroup(strings) }
                .filter { it.second }
                .sumOf { it.first + 1 }
        println(res)
    }

    private fun handleGroup(elems: MutableList<String>): Boolean {
        val l1 = L.parse(elems[0])
        val l2 = L.parse(elems[1])

        // println(l1)
        // println(l2)
        val res = checkOrder(l1, l2)
        // println(res)
        // println()
        return res == Correct
    }

    enum class Result {
        Correct,
        Incorrect,
        Continue,
    }

    private fun checkOrder(l1: Any, l2: Any): Result {
        // println("l1=$l1\nl2=$l2\n")
        when {
            l1.isNumber() && l2.isNumber() -> {
                val i = l1 as Int
                val j = l2 as Int
                return when {
                    i < j -> Correct
                    i > j -> Incorrect
                    else -> Continue
                }
            }

            l1.isList() && l2.isList() -> {
                val ll1 = l1 as L
                val ll2 = l2 as L
                for (i in ll1.elems.indices) {
                    if (i >= ll2.elems.size) {
                        return Incorrect
                    }
                    val r = checkOrder(ll1.elems[i], ll2.elems[i])
                    if (r != Continue) {
                        return r
                    }
                }
                if (ll2.elems.size < ll1.elems.size) {
                    return Incorrect
                }
                if (ll1.elems.size == ll2.elems.size) {
                    return Continue
                }
                return Correct
            }

            l1.isNumber() && l2.isList() -> {
                return checkOrder(L(l1 as Int), l2)
            }

            l1.isList() && l2.isNumber() -> {
                return checkOrder(l1, L(l2))
            }
        }

        return Correct
    }

    @Test
    fun part2() {
        val res = readLines("13.txt")
            .filter { it.isNotBlank() }
            .sortedWith { o1, o2 ->
                val l1 = L.parse(o1!!)
                val l2 = L.parse(o2!!)

                when (checkOrder(l1, l2)) {
                    Correct -> -1
                    Incorrect -> 1
                    Continue -> 0
                }
            }
            .mapIndexed { index, s -> index to s }
            .filter {
                val s = it.second
                s in setOf("[[2]]", "[[6]]")
            }
            .map { it.first + 1 }
            .reduce { a, b -> a * b }
        println(res)
    }
}
