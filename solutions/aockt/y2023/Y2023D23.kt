package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import util.spatial.Direction
import util.spatial.Point
import util.spatial.move

object Y2023D23 : Solution {
    private class Forest(val tiles: Map<Point, Char>, val start: Point, val end: Point) {

        private fun slopeDirection(tile: Char): Direction? = when (tile) {
            '^' -> Direction.Up
            'v' -> Direction.Down
            '<' -> Direction.Left
            '>' -> Direction.Right
            else -> null
        }

        private fun stepsFrom(point: Point, slippery: Boolean): List<Point> {
            val directions = when (val slope = if (slippery) slopeDirection(tiles.getValue(point)) else null) {
                null -> Direction.all
                else -> listOf(slope)
            }
            return directions.map(point::move).filter { it in tiles }
        }

        private fun walk(origin: Point, first: Point, junctions: Set<Point>, slippery: Boolean): Pair<Point, Int>? {
            var previous = origin
            var current = first
            var length = 1
            while (current !in junctions) {
                val next = stepsFrom(current, slippery).singleOrNull { it != previous } ?: return null
                previous = current
                current = next
                length++
            }
            return current to length
        }

        fun longestHike(slippery: Boolean): Int {
            val junctions = tiles.keys
                .filter { it == start || it == end || stepsFrom(it, false).size > 2 }
                .toSet()
            require(junctions.size <= Long.SIZE_BITS) { "Too many junctions." }

            val nodes = junctions.toList()
            val index = nodes.withIndex().associate { (i, point) -> point to i }
            val edges = nodes.map { node ->
                stepsFrom(node, slippery)
                    .mapNotNull { first -> walk(node, first, junctions, slippery) }
                    .map { (target, length) -> index.getValue(target) to length }
            }
            val goal = index.getValue(end)
            val gates = edges.indices.filter { node -> edges[node].any { it.first == goal } }
            val gate = gates.singleOrNull()
            val gateLength = gate?.let { edges[it].filter { edge -> edge.first == goal }.maxOf { edge -> edge.second } }

            fun search(node: Int, visited: Long, distance: Int): Int {
                if (node == goal) return distance
                if (node == gate) return distance + gateLength!!
                var best = -1
                for ((next, length) in edges[node]) {
                    val bit = 1L shl next
                    if (visited and bit == 0L) best = maxOf(best, search(next, visited or bit, distance + length))
                }
                return best
            }

            val startIndex = index.getValue(start)
            return search(startIndex, 1L shl startIndex, 0)
        }
    }

    private fun parseInput(input: String): Forest {
        val rows = input.lines().asReversed()
        val tiles = rows
            .flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, tile -> if (tile == '#') null else Point(x, y) to tile }
            }
            .toMap()
        val start = tiles.keys.single { it.y == rows.lastIndex.toLong() }
        val end = tiles.keys.single { it.y == 0L }
        return Forest(tiles, start, end)
    }

    override fun partOne(input: String) = parseInput(input).longestHike(slippery = true)
    override fun partTwo(input: String) = parseInput(input).longestHike(slippery = false)
}
