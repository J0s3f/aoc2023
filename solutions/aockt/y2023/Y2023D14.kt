package aockt.y2023

import io.github.jadarma.aockt.core.Solution


object Y2023D14 : Solution {
    private fun parseInput(input: String): MutableList<CharArray> {
        return input.lines().map { it.toCharArray() }.toMutableList()
    }

    private fun moveNorth(input: MutableList<CharArray>) {
        var changed: Boolean
        do {
            changed = false
            for (i in input.size - 1 downTo 1) {
                for (c in input[i].indices) {
                    if (input[i][c] == 'O' && input[i - 1][c] == '.') {
                        changed = true
                        input[i][c] = '.'
                        input[i - 1][c] = 'O'
                    }
                }
            }
        } while (changed)
    }

    private fun moveEast(input: MutableList<CharArray>) {
        var changed: Boolean
        do {
            changed = false
            for (line in input) {
                for (c in 0..<(line.size - 1)) {
                    if (line[c] == 'O' && line[c + 1] == '.') {
                        changed = true
                        line[c] = '.'
                        line[c + 1] = 'O'
                    }
                }
            }
        } while (changed)
    }

    private fun moveWest(input: MutableList<CharArray>) {
        var changed: Boolean
        do {
            changed = false
            for (i in input.indices) {
                for (c in 1..<(input[i].size)) {
                    if (input[i][c] == 'O' && input[i][c - 1] == '.') {
                        changed = true
                        input[i][c] = '.'
                        input[i][c - 1] = 'O'
                    }
                }
            }
        } while (changed)
    }

    private fun moveSouth(input: MutableList<CharArray>) {
        var changed: Boolean
        do {
            changed = false
            for (i in 0..<(input.size - 1)) {
                for (c in input[i].indices) {
                    if (input[i][c] == 'O' && input[i + 1][c] == '.') {
                        changed = true
                        input[i][c] = '.'
                        input[i + 1][c] = 'O'
                    }
                }
            }
        } while (changed)
    }

    private fun cycle(input: List<CharArray>): List<CharArray> {
        val data = input.map { it.copyOf() }.toMutableList()
        moveNorth(data)
        moveWest(data)
        moveSouth(data)
        moveEast(data)
        return data
    }

    private fun loadAfter(input: List<CharArray>, cycles: Long): Int {
        val results: MutableList<List<CharArray>> = mutableListOf()
        results.add(input)
        var data: List<CharArray> = input
        do {
            data = cycle(data)
            results.add(data)
        } while (results.slice(0..results.size - 2).none { equal(it, data) })
        val startPos =
            results.mapIndexed { idx, el -> Pair(idx, equal(el, data)) }.filter { it.second }.map { it.first }.first()
        val cycle = results.slice(startPos..<results.size)
        return calcLoad(cycle[((cycles - startPos) % cycle.size).toInt() - 2])
    }

    private fun equal(d1: List<CharArray>, d2: List<CharArray>): Boolean {
        if (d1.size != d2.size) {
            return false
        }
        for (i in d1.indices) {
            if (!(d1[i] contentEquals d2[i])) {
                return false
            }
        }
        return true
    }

    private fun calcLoad(input: List<CharArray>): Int {
        var res = 0
        for (i in input.indices) {
            res += input[i].filter { it == 'O' }.size * (input.size - i)
        }
        return res
    }

    override fun partOne(input: String): Int {
        val data = parseInput(input)
        moveNorth(data)
        return calcLoad(data)
    }


    override fun partTwo(input: String): Int {
        val data = parseInput(input)
        val load = loadAfter(data, 1000000000)
        return load
    }

}


