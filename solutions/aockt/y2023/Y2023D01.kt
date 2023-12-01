package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D01 : Solution {
    private val spelledNumbers = mapOf(
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
    private val digitRegex = ("\\d" + spelledNumbers.keys.joinToString("|", "|")).toRegex()

    private fun parseInput(input: String): List<String> =
        input
            .splitToSequence('\n')
            .toList()

    override fun partOne(input: String) = parseInput(input).sumOf(::getCalibrationValue)
    override fun partTwo(input: String) = parseInput(input).sumOf(::getCalibrationValueWithSpelledNumbers)


    private fun getCalibrationValue(it: String): Int {
        return it.first(Char::isDigit).digitToInt() * 10 + it.last(Char::isDigit).digitToInt()
    }

    private fun getCalibrationValueWithSpelledNumbers(it: String): Int {

        fun toDigit(it: String): Int = if (it[0].isDigit()) it[0].digitToInt() else spelledNumbers.getValue(it)

        val digitsFound = digitRegex.findAll(it)

        return toDigit(digitsFound.first().value) * 10 + toDigit(digitsFound.last().value)
    }
}


