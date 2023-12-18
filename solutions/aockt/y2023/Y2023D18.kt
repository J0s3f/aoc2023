package aockt.y2023

import aockt.y2023.Y2023D18.Direction.*
import io.github.jadarma.aockt.core.Solution
import kotlin.math.abs


object Y2023D18 : Solution {
    private val DIRECTIONS_REGEX = """(?<direction>[LRUD]) (?<length>\d+) \(#(?<color>[a-f0-9]+)\)""".toRegex()

    data class Point(val x: Long, val y: Long) {
        fun add(direction: Directions): Point {
            return when (direction.direction) {
                UP -> Point(this.x, this.y + direction.length)
                LEFT -> Point(this.x - direction.length, this.y)
                RIGHT -> Point(this.x + direction.length, this.y)
                DOWN -> Point(this.x, this.y - direction.length)
            }
        }
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN;

        companion object {
            fun fromString(value: String): Direction = when (value) {
                "L" -> LEFT
                "R" -> RIGHT
                "U" -> UP
                "D" -> DOWN
                else -> {
                    throw IllegalArgumentException("Unknown Direction")
                }
            }
        }
    }

    @JvmInline
    value class Color(val hex: String)
    data class Directions(val direction: Direction, val length: Long, val color: Color)

    private fun decodeInstructions(dir: Directions): Directions {
        val distance = dir.color.hex.substring(0, 5).toLong(radix = 16)
        val direction = when (dir.color.hex.substring(5, 6).toInt()) {
            0 -> RIGHT
            1 -> DOWN
            2 -> LEFT
            3 -> UP
            else -> {
                throw Exception("Illegal direction")
            }
        }
        return Directions(direction, distance, Color(""))
    }

    private fun parseInput(input: String): List<Directions> {
        return input.lines().map {
            val parsed = DIRECTIONS_REGEX.matchEntire(it)!!
            Directions(
                Direction.fromString(parsed.groups["direction"]!!.value),
                parsed.groups["length"]!!.value.toLong(),
                Color(parsed.groups["color"]!!.value)
            )
        }
    }

    private fun drawPolygon(directions: List<Directions>): Pair<List<Point>, Long> {
        val poly: MutableList<Point> = mutableListOf()
        val start = Point(0, 0)
        var pos = start
        poly.addLast(start)
        var border = 1L
        for (direction in directions) {
            pos = pos.add(direction)
            poly.addLast(pos)
            border += direction.length
        }
        return Pair(poly, border)
    }

    private fun calcArea(poly: Pair<List<Point>, Long>): Long {
        var area = 0L
        for (i in 1..<poly.first.size) {
            val prev = poly.first[i - 1]
            val cur = poly.first[i]
            area += (prev.y + cur.y) * (prev.x - cur.x)
        }
        return (abs(area) + poly.second + 1) / 2
    }


    override fun partOne(input: String): Long = calcArea(drawPolygon(parseInput(input)))


    override fun partTwo(input: String): Long = calcArea(drawPolygon(parseInput(input).map { decodeInstructions(it) }))


}


