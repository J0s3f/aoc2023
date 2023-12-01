package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D01 : Solution {

    private fun parseInput(input: String): List<String> =
        input
            .splitToSequence('\n')
            .toList()

    override fun partOne(input: String) = parseInput(input).sumOf(::getCalibrationValue)
    override fun partTwo(input: String) = parseInput(input).sumOf(::getCalibrationValueWithSpelledNumbers)


    private fun getCalibrationValue(it: String): Int {
        val numbers = it.toCharArray().filter(Char::isDigit)
        return Integer.parseInt(String(charArrayOf(numbers.first(), numbers.last())))
    }

    private fun getCalibrationValueWithSpelledNumbers(it: String): Int {
        val spelledNumbers = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )

        val numbers: MutableList<Int> = mutableListOf()
        for (i in it.indices) {
            if (it[i].isDigit()) {
                numbers.add(it[i].digitToInt())
            } else {
                for (num in spelledNumbers) {
                    if (it.substring(i).startsWith(num.key)) {
                        numbers.add(num.value)
                    }
                }
            }
        }
        return numbers.first() * 10 + numbers.last()
    }
}


