package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D09 : Solution {

    private fun parseInput(input: String): List<List<Int>> =
        input.lines().map { it.split(" ").map { it.toInt() } }.toList()

    private fun getNextValue(value: List<Int>): Int {
        val iterations: MutableList<MutableList<Int>> = generateDifferenceSequences(value)
        for (i in iterations.size - 1 downTo 1) {
            iterations[i - 1].addLast(iterations[i - 1].last() + iterations[i].last())
        }
        return iterations[0].last()
    }

    private fun getPreviousValue(value: List<Int>): Int {
        val iterations: MutableList<MutableList<Int>> = generateDifferenceSequences(value)
        for (i in iterations.size - 1 downTo 1) {
            iterations[i - 1].addFirst(iterations[i - 1].first() - iterations[i].first())
        }
        return iterations[0].first()
    }

    private fun generateDifferenceSequences(value: List<Int>): MutableList<MutableList<Int>> {
        val iterations: MutableList<MutableList<Int>> = mutableListOf(value.toMutableList())
        var last = value
        while (last.find { it != 0 } != null) {
            val nextSeq = last.zipWithNext().map { it.second - it.first }.toList()
            iterations.add(nextSeq.toMutableList())
            last = nextSeq
        }
        return iterations
    }

    override fun partOne(input: String) = parseInput(input).sumOf(::getNextValue)
    override fun partTwo(input: String) = parseInput(input).sumOf(::getPreviousValue)

}


