package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D03 : Solution {
    class EngineSchematic(private val lines: List<String>) {
        private val numberRegex = "(\\d+)".toRegex()
        private val gearRegex = "(\\*)".toRegex()

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
            for (i in 0..<occ.length) {
                if (adjacent.contains(Position(occ.pos.x + i, occ.pos.y))) {
                    return true
                }
            }
            return false
        }

        private fun findPartNumberOccurrences() =
            findNumbers().filter { true in getAdjacentPositions(it).map(::getChar).map(::isSymbol) }

        fun findPartNumbers(): List<String> = findPartNumberOccurrences().map(::getValue)

        private fun findNumbers(): List<Occurrence> {
            val numbers: MutableList<Occurrence> = mutableListOf()
            for (y in lines.indices) {
                numbers += numberRegex.findAll(lines[y]).map {
                    Occurrence(
                        Position(it.range.first, y), it.range.last - it.range.first + 1
                    )
                }
            }
            return numbers
        }

        private fun findGears(): List<Position> {
            val gears: MutableList<Position> = mutableListOf()
            for (y in lines.indices) {
                gears += gearRegex.findAll(lines[y]).map { Position(it.range.first, y) }
            }
            return gears
        }

        private fun getValue(occurrence: Occurrence): String =
            lines[occurrence.pos.y].substring(occurrence.pos.x, occurrence.pos.x + occurrence.length)

        private fun isSymbol(c: Char): Boolean {
            return c != '.' && !c.isDigit()
        }

        private fun getChar(pos: Position): Char = lines[pos.y][pos.x]

        private fun getAdjacentPositions(occurrence: Occurrence): List<Position> {
            val positions: MutableList<Position> = mutableListOf()
            for (i in 0..<occurrence.length) {
                positions.add(Position(occurrence.pos.x + i, occurrence.pos.y))
            }
            return positions.flatMap(::getAdjacentPositions).distinct()
        }

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


