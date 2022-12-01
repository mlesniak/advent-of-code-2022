package com.mlesniak.changeme

import java.nio.file.Files
import java.nio.file.Path

fun readLineGroups(filename: String): MutableList<MutableList<String>> {
    val res = mutableListOf<MutableList<String>>()

    var group = mutableListOf<String>()
    Files
        .readAllLines(Path.of(filename))
        .forEach { line ->
            if (line.isEmpty()) {
                res += group
                group = mutableListOf()
                return@forEach
            }
            group += line
        }
    res += group

    return res
}

