package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D07 : Solution {
    private val inputRegex = """(?<cards>\w+)\s+(?<bet>\d+)\s*""".toRegex()


    data class GamePt1(val cards: String, val bet: Long) : Comparable<GamePt1> {
        private val cardOrder = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
            .reversed().mapIndexed { index, c -> c to index + 1 }.toMap()
        private val cardCounts = countChars(cards)
        private val handValue = getHandValue()


        private fun getHandValue(): Int {
            val max = cardCounts.values.max()
            return when {
                max == 5 -> 7
                max == 4 -> 6
                max == 3 && cardCounts.values.contains(2) -> 5
                max == 3 -> 4
                max == 2 && cardCounts.values.count { it == 2 } == 2 -> 3
                max == 2 -> 2
                max == 1 -> 1
                else -> throw IllegalStateException("Missing case in when")
            }
        }

        private fun countChars(cards: String): Map<Char, Int> {
            val occurrencesMap = mutableMapOf<Char, Int>()
            for (c in cards) {
                occurrencesMap.putIfAbsent(c, 0)
                occurrencesMap[c] = occurrencesMap[c]!! + 1
            }
            return occurrencesMap
        }

        override fun compareTo(other: GamePt1): Int =
            when {
                this.handValue != other.handValue -> this.handValue compareTo other.handValue
                this.cards != other.cards -> compareCards(other.cards)
                else -> 0
            }

        private fun compareCards(cards: String): Int {
            val difference = this.cards.zip(cards).firstOrNull { it.first != it.second } ?: return 0
            return cardOrder[difference.first]!!.compareTo(cardOrder[difference.second]!!)
        }

    }

    data class GamePt2(val cards: String, val bet: Long) : Comparable<GamePt2> {
        private val cardOrder = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
            .reversed().mapIndexed { index, c -> c to index + 1 }.toMap()
        private val handValue = getHandValue()

        private fun getHandValue(): Int {
            var cards = this.cards
            if (cards.contains('J')) {
                cards = if (cards == "JJJJJ") {
                    "AAAAA"
                } else {
                    val cardCounts = countChars(cards.replace("J", ""))
                    val replaceChar =
                        cardCounts.entries.filter { it.value == cardCounts.values.max() }
                            .sortedByDescending { cardOrder[it.key] }.first().key
                    cards.replace('J', replaceChar)
                }
            }
            return handValue(cards)
        }

        private fun handValue(cards: String): Int {
            val cardCounts = countChars(cards)
            val max = cardCounts.values.max()
            return when {
                max == 5 -> 7
                max == 4 -> 6
                max == 3 && cardCounts.values.contains(2) -> 5
                max == 3 -> 4
                max == 2 && cardCounts.values.count { it == 2 } == 2 -> 3
                max == 2 -> 2
                max == 1 -> 1
                else -> throw IllegalStateException("Missing case in when")
            }
        }

        private fun countChars(cards: String): Map<Char, Int> {
            val occurrencesMap = mutableMapOf<Char, Int>()
            for (c in cards) {
                occurrencesMap.putIfAbsent(c, 0)
                occurrencesMap[c] = occurrencesMap[c]!! + 1
            }
            return occurrencesMap
        }

        override fun compareTo(other: GamePt2): Int =
            when {
                this.handValue != other.handValue -> this.handValue compareTo other.handValue
                this.cards != other.cards -> compareCards(other.cards)
                else -> 0
            }

        private fun compareCards(cards: String): Int {
            val difference = this.cards.zip(cards).firstOrNull { it.first != it.second } ?: return 0
            return cardOrder[difference.first]!!.compareTo(cardOrder[difference.second]!!)
        }

    }

    private fun parseInputPt1(input: String): List<GamePt1> = input.lines().mapNotNull { inputRegex.matchEntire(it) }
        .map { GamePt1(it.groups["cards"]!!.value, it.groups["bet"]!!.value.toLong()) }

    private fun parseInputPt2(input: String): List<GamePt2> = input.lines().mapNotNull { inputRegex.matchEntire(it) }
        .map { GamePt2(it.groups["cards"]!!.value, it.groups["bet"]!!.value.toLong()) }

    override fun partOne(input: String): Long = parseInputPt1(input).sorted()
        .mapIndexed { index, game -> game.bet * (index + 1L) }
        .sum()

    override fun partTwo(input: String) = parseInputPt2(input).sorted()
        .mapIndexed { index, game -> game.bet * (index + 1L) }
        .sum()
}


