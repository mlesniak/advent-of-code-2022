package com.mlesniak.changeme

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
    private val elems: List<Any> = es.toList()

    override fun toString(): String = "[${elems.joinToString(separator = ",")}]"

    companion object {
        fun parse(s: String) = internalParse(s).first

        private fun internalParse(s: String): Pair<L, Int> {
            val elems = mutableListOf<Any>()
            var i = 1
            while (i < s.length) {
                val c = s[i]
                if (c.isDigit()) {
                    var k = i + 1
                    while (s[k].isDigit()) {
                        k++
                    }
                    val num = s.substring(i until k)
                    elems += num.toInt()
                    i = k
                    continue
                } else if (c == ']') {
                    return L(*(elems.toTypedArray())) to i
                } else if (c == '[') {
                    val (elem, k) = internalParse(s.substring(i))
                    elems += elem
                    i += k + 1
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
        val groups = readLineGroups("13.txt")
        groups.forEach { handleGroup(it) }
    }

    private fun handleGroup(elems: MutableList<String>) {
        val l1 = L.parse(elems[0])
        val l2 = L.parse(elems[1])

        println(l1)
        println(l2)
        println()
    }

    @Test
    fun part2() {
    }
}
