package aockt.y2023

import util.ParseHelper.parseIntField
import aockt.y2023.Y2023D17.Direction.*
import io.github.jadarma.aockt.core.Solution
import util.path
import util.search

object Y2023D17 : Solution {
    private fun parseInput(input: String): Graph {
        return Graph(parseIntField(input))
    }

    private operator fun Array<IntArray>.get(index: Pos): Int = this[index.y][index.x]
    private operator fun Array<IntArray>.set(index: Pos, value: Int) {
        this[index.y][index.x] = value
    }

    private operator fun Array<Array<Pos?>>.get(index: Pos): Pos? = this[index.y][index.x]
    private operator fun Array<Array<Pos?>>.set(index: Pos, value: Pos?) {
        this[index.y][index.x] = value
    }

    data class Graph(val data: Array<IntArray>) {
        val xMax = data[0].size - 1
        val yMax = data.size - 1

        fun get(pos: Pos) = data[pos.y][pos.x]
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Graph

            return data.contentDeepEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentDeepHashCode()
        }

        fun contains(pos: Pos): Boolean {
            return pos.x in 0..xMax && pos.y in 0..yMax
        }
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN;

        companion object {
            fun all(): List<Direction> {
                return Direction.entries
            }
        }

        fun opposite(): Direction = when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            UP -> DOWN
            DOWN -> UP
        }
    }

    data class Pos(val x: Int, val y: Int) {
        fun posIn(direction: Direction): Pos {
            return when (direction) {
                LEFT -> Pos(this.x - 1, this.y)
                RIGHT -> Pos(this.x + 1, this.y)
                UP -> Pos(this.x, this.y - 1)
                DOWN -> Pos(this.x, this.y + 1)
            }
        }
    }

    private data class SearchState(
        val position: Pos,
        val direction: Direction,
        val straightLineStreak: Int,
    )

    private fun getDistance(graph: Graph, start: Pos, target: Pos, isUltraCrucible: Boolean = false): Int {
        fun neighbours(state: SearchState): List<SearchState> {
            val validDirections = Direction.all()
                .minus(state.direction.opposite())
                .run { if (state.straightLineStreak == if (isUltraCrucible) 10 else 3) minus(state.direction) else this }
                .run { if (isUltraCrucible && state.straightLineStreak in 1..<4) listOf(state.direction) else this }
                .filter { direction -> graph.contains(state.position.posIn(direction)) }

            return validDirections.map { direction ->
                val nextPosition = state.position.posIn(direction)
                val nextState = SearchState(
                    position = nextPosition,
                    direction = direction,
                    straightLineStreak = if (state.direction == direction) state.straightLineStreak.inc() else 1,
                )
                nextState
            }
        }

        fun navigate(start: Pos, end: Pos): List<Pair<Pos, Int>> {
            val searchGraph = object : util.Graph<SearchState> {
                override fun neighboursOf(node: SearchState) =
                    neighbours(node).map { it to graph.get(it.position) }
            }

            val search = searchGraph.search(
                start = SearchState(start, RIGHT, 0),
                goalFunction = { it.position == end && if (isUltraCrucible) it.straightLineStreak >= 4 else true }
            )

            return search.path()!!.map { it.position to search.searchTree[it]!!.second }
        }

        val pairs = navigate(start, target)
        return pairs.last().second
    }


    override fun partOne(input: String): Int {
        val graph = parseInput(input)
        return getDistance(graph, Pos(0, 0), Pos(graph.xMax, graph.yMax))
    }


    override fun partTwo(input: String): Int {
        val graph = parseInput(input)
        return getDistance(graph, Pos(0, 0), Pos(graph.xMax, graph.yMax), isUltraCrucible = true)
    }

}


