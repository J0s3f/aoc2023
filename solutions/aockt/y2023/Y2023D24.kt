package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import util.ExtensionFunctions.combinations
import util.Fraction

object Y2023D24 : Solution {
    private data class Hailstone(
        val px: Long, val py: Long, val pz: Long,
        val vx: Long, val vy: Long, val vz: Long,
    ) {
        fun crossesWithin(other: Hailstone, area: LongRange): Boolean {
            val determinant = other.vx * vy - vx * other.vy
            if (determinant == 0L) return false
            val dx = other.px - px
            val dy = other.py - py
            val time = Fraction.of(other.vx * dy - other.vy * dx, determinant)
            val otherTime = Fraction.of(vx * dy - vy * dx, determinant)
            if (time.sign < 0 || otherTime.sign < 0) return false
            val x = Fraction(px) + time * Fraction(vx)
            val y = Fraction(py) + time * Fraction(vy)
            return listOf(x, y).all { it >= Fraction(area.first) && it <= Fraction(area.last) }
        }
    }

    private fun parseInput(input: String): List<Hailstone> =
        input
            .lines()
            .map { line -> line.split('@', ',').map { it.trim().toLong() } }
            .onEach { require(it.size == 6) { "Invalid hailstone: $it" } }
            .map { Hailstone(it[0], it[1], it[2], it[3], it[4], it[5]) }

    private fun solveLinearSystem(rows: List<List<Fraction>>): List<Fraction>? {
        val matrix = rows.map { it.toMutableList() }.toMutableList()
        val size = matrix.size
        for (column in 0 until size) {
            val pivot = (column until size).firstOrNull { matrix[it][column].sign != 0 } ?: return null
            matrix[column] = matrix[pivot].also { matrix[pivot] = matrix[column] }
            val pivotValue = matrix[column][column]
            matrix[column] = matrix[column].map { it / pivotValue }.toMutableList()
            for (row in 0 until size) {
                if (row == column) continue
                val factor = matrix[row][column]
                if (factor.sign == 0) continue
                matrix[row] = matrix[row].zip(matrix[column]) { a, b -> a - factor * b }.toMutableList()
            }
        }
        return matrix.map { it.last() }
    }

    private fun findRock(a: Hailstone, b: Hailstone, c: Hailstone): List<Fraction>? {
        fun equations(first: Hailstone, second: Hailstone): List<List<Long>> {
            val dvx = second.vx - first.vx
            val dvy = second.vy - first.vy
            val dvz = second.vz - first.vz
            val dpx = second.px - first.px
            val dpy = second.py - first.py
            val dpz = second.pz - first.pz
            val rhsX = (second.py * second.vz - second.pz * second.vy) - (first.py * first.vz - first.pz * first.vy)
            val rhsY = (second.pz * second.vx - second.px * second.vz) - (first.pz * first.vx - first.px * first.vz)
            val rhsZ = (second.px * second.vy - second.py * second.vx) - (first.px * first.vy - first.py * first.vx)
            return listOf(
                listOf(0, dvz, -dvy, 0, -dpz, dpy, rhsX),
                listOf(-dvz, 0, dvx, dpz, 0, -dpx, rhsY),
                listOf(dvy, -dvx, 0, -dpy, dpx, 0, rhsZ),
            )
        }
        return solveLinearSystem((equations(a, b) + equations(a, c)).map { row -> row.map { Fraction(it) } })
    }

    fun countCrossingsWithin(input: String, area: LongRange): Int =
        parseInput(input).combinations().count { (a, b) -> a.crossesWithin(b, area) }

    override fun partOne(input: String) = countCrossingsWithin(input, 200000000000000L..400000000000000L)

    override fun partTwo(input: String): Any {
        val rock = parseInput(input)
            .windowed(3)
            .firstNotNullOf { (a, b, c) -> findRock(a, b, c) }
        val sum = rock[0] + rock[1] + rock[2]
        check(sum.isInteger) { "The rock position is not integral." }
        return sum.numerator.toLong()
    }
}
