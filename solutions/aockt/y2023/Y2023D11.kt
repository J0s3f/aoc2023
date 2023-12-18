package aockt.y2023

import ExtensionFunctions.combinations
import ParseHelper.parseCharField
import io.github.jadarma.aockt.core.Solution
import kotlin.math.abs


object Y2023D11 : Solution {

    data class Point(val x: Long, val y: Long)

    private fun parseInput(input: String): Array<CharArray> {
        return parseCharField(input)
    }

    override fun partOne(input: String): Long {
        val data = parseInput(input)
        val galaxies: MutableSet<Point> = findGalaxies(data)
        val expansionPoints = findExpansionPoints(data)

        val expansionFactor = 1L

        val expandedGalaxies = expandGalaxies(galaxies, expansionPoints, expansionFactor)

        val distances = expandedGalaxies.combinations().map { manhattanDistance(it.first, it.second) }

        return distances.sum()
    }

    override fun partTwo(input: String): Long {
        val data = parseInput(input)
        val galaxies: MutableSet<Point> = findGalaxies(data)
        val expansionPoints = findExpansionPoints(data)

        val expansionFactor = 1000000L - 1L

        val expandedGalaxies = expandGalaxies(galaxies, expansionPoints, expansionFactor)

        val distances = expandedGalaxies.combinations().map { manhattanDistance(it.first, it.second) }

        return distances.sum()
    }

    private fun expandGalaxies(
        galaxies: Set<Point>,
        expansionPoints: Pair<Set<Long>, Set<Long>>,
        expansionFactor: Long
    ): List<Point> {
        val expandedGalaxies = galaxies.map {
            var x = it.x
            for (ex in expansionPoints.first) {
                if (it.x > ex) {
                    x += expansionFactor
                }
            }
            var y = it.y
            for (ex in expansionPoints.second) {
                if (it.y > ex) {
                    y += expansionFactor
                }
            }
            Point(x, y)
        }.toList()
        return expandedGalaxies
    }

    private fun findExpansionPoints(data: Array<CharArray>): Pair<Set<Long>, Set<Long>> {
        val expandX: MutableSet<Long> = mutableSetOf()
        for (x in data[0].indices) {
            var expand = true
            for (y in data.indices) {
                if (data[y][x] != '.') {
                    expand = false
                    break
                }
            }
            if (expand) {
                expandX.add(x.toLong())
            }
        }
        val expandY: MutableSet<Long> = mutableSetOf()
        for (y in data.indices) {
            var expand = true
            for (x in data[0].indices) {
                if (data[y][x] != '.') {
                    expand = false
                    break
                }
            }
            if (expand) {
                expandY.add(y.toLong())
            }
        }
        return Pair(expandX, expandY)
    }

    private fun findGalaxies(data: Array<CharArray>): MutableSet<Point> {
        val galaxies: MutableSet<Point> = mutableSetOf()
        for (y in data.indices) {
            for (x in data[y].indices) {
                if (data[y][x] == '#') {
                    galaxies.add(Point(x.toLong(), y.toLong()))
                }
            }
        }
        return galaxies
    }

    private fun manhattanDistance(pos1: Point, pos2: Point): Long = abs(pos1.x - pos2.x) + abs(pos1.y - pos2.y)


}


