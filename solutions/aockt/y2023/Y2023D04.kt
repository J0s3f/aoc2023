package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D04 : Solution {
    private val cardRegex = Regex("Card\\s+(?<id>\\d+):\\s+(?<winning>.+?)\\s+\\|\\s+(?<own>.+)")
    private val numberRegex = "[0-9]+".toRegex()

    data class Card(val id: Int, val numbersOnCard: Set<Int>, val winningNumbers: Set<Int>, val matches: Int)

    override fun partOne(input: String) =
        parseInput(input).map(Card::matches).filter { it > 0 }.sumOf { 1 shl it - 1 }

    override fun partTwo(input: String): Int {
        val cards = parseInput(input)
        val toProcess = ArrayDeque(cards)
        var count = cards.size
        fun getCardCopy(id: Int) = cards.getOrNull(id - 1)
        while (!toProcess.isEmpty()) {
            val c = toProcess.removeFirst()
            (c.id + 1..c.id + c.matches).forEach { idx -> ++count; getCardCopy(idx)?.let { toProcess.addFirst(it) } }

        }
        return count
    }

    private fun parseInput(input: String): List<Card> {
        return input.splitToSequence('\n').map(String::trim).filter(String::isNotBlank).map(cardRegex::matchEntire)
            .map { matchResult ->
                val id = matchResult!!.groups["id"]!!.value.toInt()
                val winning = numberRegex.findAll(matchResult.groups["winning"]!!.value).map(MatchResult::value)
                    .map(String::toInt).toSet()
                val own =
                    numberRegex.findAll(matchResult.groups["own"]!!.value).map(MatchResult::value).map(String::toInt)
                        .toSet()
                Card(id, own, winning, winning.intersect(own).size)
            }.toList()
    }


}


