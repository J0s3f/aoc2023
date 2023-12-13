package aockt.y2023

import io.github.jadarma.aockt.core.Solution

fun <T> Sequence<T>.repeatForever() = generateSequence(this) { it }.flatten()

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}


object Y2023D08 : Solution {
    private val inputRegex = """(?<from>\w+)\s+=\s+\((?<left>\w+),\s+(?<right>\w+)\s*.*""".toRegex()


    enum class Direction {
        LEFT, RIGHT;

        companion object {
            fun sequenceFromString(s: String): Sequence<Direction> {
                val seq = s.map {
                    when (it) {
                        'L' -> LEFT
                        'R' -> RIGHT
                        else -> throw IllegalArgumentException("Invalid input")
                    }
                }
                return seq.asSequence().repeatForever()
            }
        }
    }

    data class StringPath(val from: String, val left: String, val right: String)
    data class Path(val name: String, var left: Path?, var right: Path?) {
        override fun toString(): String {
            return "Path(name='$name', left=${left?.name}, right=${right?.name})"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Path

            if (name != other.name) return false
            if (left?.name != other.left?.name) return false
            if (right?.name != other.right?.name) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + left?.name.hashCode()
            result = 31 * result + right?.name.hashCode()
            return result
        }


    }

    data class Game(val directions: String, val paths: Map<String, Path>)

    private fun parseInput(input: String): Game {
        val lines = input.lines()
        val directions = lines.removeFirst().trim()
        val paths = lines.asSequence().filter { it.trim().isNotBlank() }
        val matches = paths.mapNotNull { inputRegex.matchEntire(it) }.map { it.groups }
        val mapped = matches.map { StringPath(it["from"]!!.value, it["left"]!!.value, it["right"]!!.value) }
            .map { it.from to it }.toMap()
        val pathObjects = mapped.values.associate { it.from to Path(it.from, null, null) }
        pathObjects.forEach {
            val data = mapped[it.key]!!
            it.value.left = pathObjects[data.left]!!
            it.value.right = pathObjects[data.right]!!

        }

        return Game(directions, pathObjects)
    }

    override fun partOne(input: String): Int {
        val game = parseInput(input)
        val directions = Direction.sequenceFromString(game.directions).iterator()
        var count = 0
        var pos = game.paths["AAA"]!!
        while (pos.name != "ZZZ") {
            pos = when (directions.next()) {
                Direction.RIGHT -> pos.right!!
                Direction.LEFT -> pos.left!!
            }
            count++
        }
        return count
    }

    override fun partTwo(input: String): Long {
        val game = parseInput(input)
        val startPositions = game.paths.values.filter { it.name.endsWith("A") }.toList()
        val lcm = startPositions.map { findCycleLength(it, game.directions) }.reduce { acc, l -> findLCM(acc, l) }

        return lcm
    }

    private fun findCycleLength(p: Path, directions: String): Long {
        val dir = Direction.sequenceFromString(directions).iterator()
        var count = 0L
        var pos = p
        while (!pos.name.endsWith("Z")) {
            pos = when (dir.next()) {
                Direction.RIGHT -> pos.right!!
                Direction.LEFT -> pos.left!!
            }
            count++
        }
        return count
    }
}



