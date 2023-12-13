package aockt.y2023

import io.github.jadarma.aockt.core.Solution


object Y2023D13 : Solution {
    private val SPLIT_REGEX = """(\r?\n\s*){2,}""".toRegex()

    data class Result(val pattern: List<CharArray>, val mirror: Int, val isHorizontal: Boolean)

    private fun parseInput(input: String): List<List<CharArray>> {
        return input.split(SPLIT_REGEX).map { it.lines().map { s -> s.toCharArray() } }
    }

    private fun transpose(matrix: List<CharArray>): List<CharArray> {
        return matrix.first().mapIndexed { i, _ ->
            matrix.map { it.getOrNull(i) }.toString().toCharArray()
        }
    }

    private fun getVerticalMirror(input: List<CharArray>): List<Result> {
        val transposed = transpose(input)
        return (0..<transposed.size - 1).map { i ->
            Pair(i, hasHorizontalMirror(transposed, i))
        }.filter { it.second }.map {
            Result(input, it.first + 1, false)
        }
    }


    private fun getHorizontalMirror(input: List<CharArray>): List<Result> {
        return (0..<input.size - 1).map { i ->
            Pair(i, hasHorizontalMirror(input, i))
        }.filter { it.second }.map {
            Result(input, it.first + 1, true)
        }
    }


    private fun hasHorizontalMirror(input: List<CharArray>, i: Int): Boolean {
        val pairs = generateSequence(Pair(i, i + 1)) {
            Pair(
                it.first - 1, it.second + 1
            )
        }.takeWhile { it.first >= 0 && it.second < input.size }.toList()
        for ((a, b) in pairs) {
            if (!(input[a] contentEquals input[b])) {
                return false
            }
        }
        return true
    }

    private fun findSmudgedResult(input: Result): Result {
        val pattern = input.pattern.map { it.copyOf() }
        for (line in pattern.indices) {
            for (char in pattern[line].indices) {
                val originalChar = pattern[line][char]
                if (originalChar == '#') {
                    pattern[line][char] = '.'

                } else if (originalChar == '.') {
                    pattern[line][char] = '#'
                }
                val horizontalMirror =
                    getHorizontalMirror(pattern).filter { !input.isHorizontal || input.mirror != it.mirror }
                val verticalMirror =
                    getVerticalMirror(pattern).filter { input.isHorizontal || input.mirror != it.mirror }
                if (horizontalMirror.isNotEmpty() || verticalMirror.isNotEmpty()) {
                    val res = horizontalMirror + verticalMirror
                    return res[0]
                }
                pattern[line][char] = originalChar
            }
        }
        return input
    }

    private fun getValue(pattern: List<CharArray>): Int {
        val horizontalMirror = getHorizontalMirror(pattern)
        if (horizontalMirror.isNotEmpty()) return 100 * horizontalMirror[0].mirror
        val verticalMirror = getVerticalMirror(pattern)
        if (verticalMirror.isNotEmpty()) return verticalMirror[0].mirror
        throw IllegalStateException("No mirror found")
    }

    private fun getFixedValue(pattern: List<CharArray>): Int {
        val result = getHorizontalMirror(pattern) + getVerticalMirror(pattern)
        val fixed = findSmudgedResult(result[0])
        return (if (fixed.isHorizontal) 100 else 1) * fixed.mirror
    }


    override fun partOne(input: String): Int = parseInput(input).sumOf { getValue(it) }


    override fun partTwo(input: String) = parseInput(input).sumOf { getFixedValue(it) }
}





