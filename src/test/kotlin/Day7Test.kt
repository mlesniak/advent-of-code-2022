package com.mlesniak.changeme

import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

data class DirectoryNode(
    var name: String,
    var size: Long = 0,
    var parent: DirectoryNode? = null,
    var contents: MutableMap<String, DirectoryNode> = mutableMapOf()
) {
    override fun toString(): String {
        return "DirectoryNode(name='$name', size=$size, contents=$contents)"
    }
}

class Day7Test {
    @Test
    fun part1() {
        val lines = Files.readAllLines(Path.of("7.txt"))
        var root = DirectoryNode("/")

        var i = 0
        var cur = root
        while (i < lines.size) {
            val com = lines[i]
            when {
                com == "$ cd /" -> cur = root
                com == "$ cd .." -> cur = cur.parent!!
                com.startsWith("$ cd ") -> {
                    val targetDir = com.split(" ")[2]
                    cur = cur.contents[targetDir]!!
                }

                com == "$ ls" -> {
                    var j = i + 1
                    while (j < lines.size && !lines[j].startsWith("$")) {
                        val entry = lines[j]
                        when {
                            entry.startsWith("dir ") -> {
                                val dirName = entry.split(" ")[1]
                                cur.contents[dirName] = DirectoryNode(
                                    name = dirName,
                                    parent = cur
                                )
                            }

                            else -> {
                                val parts = entry.split(" ")
                                val size = parts[0].toLong()
                                val name = parts[1]
                                cur.contents[name] = DirectoryNode(name = name, size = size)
                            }
                        }
                        j++
                    }
                    i += (j - i - 1)
                }
            }
            i++
        }

        // A lot of optimization potential to write more beautiful code.
        // No time this evening...
        computeSums(root)
        val directories = mutableListOf<DirectoryNode>()
        filterDirs(directories, root)
        val res = directories.sumOf { it.size }
        println(res)
    }

    private fun filterDirs(directories: MutableList<DirectoryNode>, root: DirectoryNode) {
        // Ignore files.
        if (root.contents.isEmpty()) {
            return
        }

        if (root.size < 100_000) {
            directories += root
        }

        for (entry in root.contents.values) {
            filterDirs(directories, entry)
        }
    }

    private fun computeSums(root: DirectoryNode) {
        var sum = 0L

        for (entry in root.contents.values) {
            if (entry.contents.isNotEmpty()) {
                // A directory.
                computeSums(entry)
                sum += entry.size
            } else {
                sum += entry.size
            }
        }

        root.size = sum
    }

    @Test
    fun part2() {
    }
}
