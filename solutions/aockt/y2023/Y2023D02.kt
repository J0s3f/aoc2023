package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import kotlin.math.max

object Y2023D02 : Solution {
    private val idRegex = "Game (?<id>\\d+):(?<samples>.*)".toRegex()
    private val redRegex = "(?<count>\\d+) red".toRegex()
    private val blueRegex = "(?<count>\\d+) blue".toRegex()
    private val greenRegex = "(?<count>\\d+) green".toRegex()

    data class Line(val id: Int, val samples: List<Sample>)
    data class Sample(val red: Int, val green: Int, val blue: Int)

    override fun partOne(input: String) =
        parseInput(input).map(::getMaxPerColor)
            .filter { it.second.red <= 12 && it.second.green <= 13 && it.second.blue <= 14 }
            .map(Pair<Int, Sample>::first)
            .sum()

    override fun partTwo(input: String) =
        parseInput(input).map(::getMaxPerColor).sumOf { it.second.red * it.second.green * it.second.blue }

    private fun parseInput(input: String): List<Line> =
        input
            .splitToSequence('\n')
            .map { parseLine(it.trim()) }
            .filter { line -> line != null }
            .map { line -> line!! }
            .toList()

    private fun parseLine(line: String): Line? {
        val matchGroups = idRegex.matchEntire(line)?.groups ?: return null
        val id = matchGroups["id"]!!.value.toInt()
        val samples = matchGroups["samples"]!!.value.splitToSequence(";").map(::parseColors).toList()
        return Line(id, samples)
    }

    private fun parseColors(sample: String): Sample {
        val red = redRegex.find(sample)?.groups?.get("count")?.value?.toInt() ?: 0
        val blue = blueRegex.find(sample)?.groups?.get("count")?.value?.toInt() ?: 0
        val green = greenRegex.find(sample)?.groups?.get("count")?.value?.toInt() ?: 0
        return Sample(red, green, blue)
    }

    private fun getMaxPerColor(line: Line): Pair<Int, Sample> {
        var red = 0
        var green = 0
        var blue = 0
        for (s in line.samples) {
            red = max(red, s.red)
            green = max(green, s.green)
            blue = max(blue, s.blue)
        }
        return Pair(line.id, Sample(red, green, blue))
    }


}


