package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D06 : Solution {
    private val inputRegex = """(?s).*Time:\s+(?<times>(\d+\s*)+)\n+Distance:\s+(?<distances>(\d+\s*)+).*""".toRegex()
    private val numberRegex = "[0-9]+".toRegex()


    data class Race(val time: Long, val distance: Long)

    private fun parseInputPart1(input: String): List<Race> = input.let { inputRegex.matchEntire(it) }.let {
            val times = numberRegex.findAll(it!!.groups["times"]!!.value).map(MatchResult::value).map(String::toLong)
            val distances =
                numberRegex.findAll(it.groups["distances"]!!.value).map(MatchResult::value).map(String::toLong)
            return (times zip distances).map { Race(it.first, it.second) }.toList()
        }

    private fun parseInputPart2(input: String): Race = input.let { inputRegex.matchEntire(it) }.let {
            val times =
                numberRegex.findAll(it!!.groups["times"]!!.value.replace("\\s+".toRegex(), "")).map(MatchResult::value)
                    .map(String::toLong)
            val distances = numberRegex.findAll(it.groups["distances"]!!.value.replace("\\s+".toRegex(), ""))
                .map(MatchResult::value).map(String::toLong)
            return (times zip distances).map { Race(it.first, it.second) }.first()
        }

    private fun totalDistance(holdTime: Long, travelTime: Long): Long = holdTime * travelTime

    private fun waysToWin(race: Race): Long {
        var waysToWin = 0L
        for (holdTime in 1 until race.time) {
            val travelTime = race.time - holdTime
            val totalDistance = totalDistance(holdTime, travelTime)

            if (totalDistance > race.distance) {
                waysToWin++
            }
        }
        return waysToWin
    }

    override fun partOne(input: String) = parseInputPart1(input).map(::waysToWin).reduce { acc, i -> acc * i }
    override fun partTwo(input: String) = parseInputPart2(input).let(::waysToWin)

}


