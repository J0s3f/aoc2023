package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D03 : Solution {
    class EngineSchematic(private val lines: List<String>) {
        companion object {
            private val numberRegex = "(\\d+)".toRegex()
            private val gearRegex = "(\\*)".toRegex()
        }

        data class Position(val x: Int, val y: Int)
        data class Occurrence(val pos: Position, val length: Int)

        private val width: Int = lines.maxOfOrNull(String::length)!!
        private val height: Int = lines.size

        fun getGearRatios(): List<Int> {
            val partNumbers = findPartNumberOccurrences()
            val gears = findGears()
            return gears.map { g ->
                val numbers = partNumbers.filter { pn -> isNextTo(pn, g) }
                if (numbers.size == 2) {
                    val values = numbers.map(::getValue).map(String::toInt)
                    values[0] * values[1]
                } else {
                    0
                }
            }.filter { it != 0 }
        }

        private fun isNextTo(occ: Occurrence, pos: Position): Boolean {
            val adjacent = getAdjacentPositions(pos).toSet()
            return (0 until occ.length).any { i ->
                adjacent.contains(Position(occ.pos.x + i, occ.pos.y))
            }
        }

        private fun findPartNumberOccurrences() =
            findNumbers().filter { true in getAdjacentPositions(it).map(::getChar).map(::isSymbol) }

        fun findPartNumbers(): List<String> = findPartNumberOccurrences().map(::getValue)


        private fun findNumbers(): List<Occurrence> = lines.flatMapIndexed { y, line ->
            numberRegex.findAll(line).map {
                Occurrence(Position(it.range.first, y), it.range.last - it.range.first + 1)
            }
        }

        private fun findGears(): List<Position> = lines.flatMapIndexed { y, line ->
            gearRegex.findAll(line).map { Position(it.range.first, y) }
        }

        private fun getValue(occurrence: Occurrence): String =
            lines[occurrence.pos.y].substring(occurrence.pos.x, occurrence.pos.x + occurrence.length)

        private fun isSymbol(c: Char): Boolean {
            return c != '.' && !c.isDigit()
        }

        private fun getChar(pos: Position): Char = lines[pos.y][pos.x]

        private fun getAdjacentPositions(occurrence: Occurrence): List<Position> =
            (occurrence.pos.x until (occurrence.pos.x + occurrence.length)).map { Position(it, occurrence.pos.y) }
                .flatMap(::getAdjacentPositions).distinct()

        private fun getAdjacentPositions(pos: Position): List<Position> {
            val x = pos.x
            val y = pos.y
            return listOf(
                Position(x - 1, y),
                Position(x + 1, y),
                Position(x, y - 1),
                Position(x, y + 1),
                Position(x - 1, y - 1),
                Position(x - 1, y + 1),
                Position(x + 1, y + 1),
                Position(x + 1, y - 1)
            ).filter(::isValid)
        }

        private fun isValid(pos: Position): Boolean {
            return !(pos.x < 0 || pos.x >= width || pos.y < 0 || pos.y >= height)
        }


    }

    override fun partOne(input: String) = parseInput(input).findPartNumbers().map(String::toInt).sum()

    override fun partTwo(input: String) = parseInput(input).getGearRatios().sum()

    private fun parseInput(input: String): EngineSchematic =
        EngineSchematic(input.splitToSequence('\n').map(String::trim).filter(String::isNotBlank).toList())


}


