package aockt.y2023

import io.github.jadarma.aockt.core.Solution


object Y2023D16 : Solution {
    private fun parseInput(input: String): Array<CharArray> {
        val lines = input.lines()
        val columns = lines[0].length
        for (line in lines) {
            if (line.length != columns) {
                throw Exception("Invalid input data")
            }
        }
        return lines.map { it.toCharArray() }.toTypedArray()
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN;
    }

    data class Pos(val x: Int, val y: Int)
    data class Beam(val pos: Pos, val direction: Direction) {
        private fun follow(): Beam {
            return when (direction) {
                Direction.LEFT -> Beam(Pos(pos.x - 1, pos.y), direction)
                Direction.RIGHT -> Beam(Pos(pos.x + 1, pos.y), direction)
                Direction.UP -> Beam(Pos(pos.x, pos.y - 1), direction)
                Direction.DOWN -> Beam(Pos(pos.x, pos.y + 1), direction)
            }
        }

        private fun isInBounds(xSize: Int, ySize: Int): Boolean {
            return pos.x in 0..<xSize && pos.y in 0..<ySize
        }

        fun followInBounds(xSize: Int, ySize: Int): Beam? {
            val next = follow()
            if (next.isInBounds(xSize, ySize)) return next
            return null
        }

        fun with(direction: Direction): Beam {
            return Beam(pos, direction)
        }
    }

    private fun countEnergizedTiles(input: Array<CharArray>, startPosition: Beam): Int {
        val xSize = input[0].size
        val ySize = input.size
        val visited: MutableSet<Beam> = mutableSetOf()
        val todo = ArrayDeque<Beam>()
        todo.add(startPosition)
        fun addNext(beam: Beam) {
            todo.addNotNull(beam.followInBounds(xSize, ySize))
        }

        while (todo.isNotEmpty()) {
            val pos = todo.removeFirst()
            if (!visited.contains(pos)) {
                visited.add(pos)
                val field = input.get(pos.pos)
                when (field) {
                    '.' -> addNext(pos)
                    '/' -> when (pos.direction) {
                        Direction.LEFT -> addNext(pos.with(Direction.DOWN))
                        Direction.RIGHT -> addNext(pos.with(Direction.UP))
                        Direction.UP -> addNext(pos.with(Direction.RIGHT))
                        Direction.DOWN -> addNext(pos.with(Direction.LEFT))
                    }

                    '\\' -> when (pos.direction) {
                        Direction.LEFT -> addNext(pos.with(Direction.UP))
                        Direction.RIGHT -> addNext(pos.with(Direction.DOWN))
                        Direction.UP -> addNext(pos.with(Direction.LEFT))
                        Direction.DOWN -> addNext(pos.with(Direction.RIGHT))
                    }

                    '|' -> when (pos.direction) {
                        Direction.LEFT, Direction.RIGHT -> {
                            addNext(pos.with(Direction.UP))
                            addNext(pos.with(Direction.DOWN))
                        }
                        Direction.UP, Direction.DOWN -> addNext(pos)
                    }

                    '-' -> when (pos.direction) {
                        Direction.LEFT, Direction.RIGHT -> addNext(pos)
                        Direction.UP, Direction.DOWN -> {
                            addNext(pos.with(Direction.LEFT))
                            addNext(pos.with(Direction.RIGHT))
                        }
                    }
                }

            }
        }
        return visited.map { it.pos }.toSet().size
    }


    override fun partOne(input: String): Int {
        val input = parseInput(input)

        val startPosition = Beam(Pos(0, 0), Direction.RIGHT)

        return countEnergizedTiles(input, startPosition)
    }

    override fun partTwo(input: String): Int {
        val input = parseInput(input)

        val xSize = input[0].size
        val ySize = input.size
        val startPositionsLeft = input.indices.map { Beam(Pos(0, it), Direction.RIGHT) }
        val startPositionsRight = input.indices.map { Beam(Pos(xSize - 1, it), Direction.LEFT) }
        val startPositionsTop = input[0].indices.map { Beam(Pos(it, 0), Direction.DOWN) }
        val startPositionsBottom = input[0].indices.map { Beam(Pos(it, ySize - 1), Direction.UP) }

        val startPositions = startPositionsLeft + startPositionsRight + startPositionsTop + startPositionsBottom

        return startPositions.maxOfOrNull { startPosition -> countEnergizedTiles(input, startPosition) }!!
    }

    private fun <E> ArrayDeque<E>.addNotNull(element: E?) {
        if (element != null) {
            this.add(element)
        }
    }

    private fun Array<CharArray>.get(index: Pos): Char {
        return this[index.y][index.x]
    }
}


