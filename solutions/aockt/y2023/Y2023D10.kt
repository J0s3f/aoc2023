package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import kotlin.math.max


object Y2023D10 : Solution {
    private const val EMPTY_SPACE = '.'
    private const val START_SPACE = 'S'

    data class Node(val x: Int, val y: Int, var c: Char, var dist: Int? = null) {
        var connections: MutableSet<Node> = mutableSetOf()
    }

    data class Maze(val nodes: MutableMap<Pair<Int, Int>, Node>) {
        private var start: Node? = null

        fun getStart() = start

        fun getNode(x: Int, y: Int): Node? = nodes[Pair(x, y)]
        fun addNode(x: Int, y: Int, c: Char) {
            nodes.putIfAbsent(Pair(x, y), Node(x, y, c))
        }

        private fun getNeighbours(node: Node): Set<Node> {
            when (node.c) {
                START_SPACE -> return setOfNotNull(
                    getNode(node.x, node.y + 1),
                    getNode(node.x, node.y - 1),
                    getNode(node.x + 1, node.y),
                    getNode(node.x - 1, node.y)
                )

                '|' -> return setOfNotNull(getNode(node.x, node.y + 1), getNode(node.x, node.y - 1))
                '-' -> return setOfNotNull(getNode(node.x + 1, node.y), getNode(node.x - 1, node.y))
                'L' -> return setOfNotNull(
                    getNode(node.x, node.y - 1), getNode(node.x + 1, node.y)
                )

                'J' -> return setOfNotNull(
                    getNode(node.x, node.y - 1), getNode(node.x - 1, node.y)
                )

                '7' -> return setOfNotNull(
                    getNode(node.x, node.y + 1), getNode(node.x - 1, node.y)
                )

                'F' -> return setOfNotNull(
                    getNode(node.x, node.y + 1), getNode(node.x + 1, node.y)
                )

                EMPTY_SPACE -> return setOfNotNull()
                else -> throw Exception("Unknown char ${node.c}")
            }
        }

        fun addConnections() {
            for (node in nodes) {
                when (node.value.c) {
                    START_SPACE -> start = node.value
                    EMPTY_SPACE -> {}
                    else -> getNeighbours(node.value).forEach {
                        if (getNeighbours(it).contains(node.value)) {
                            it.connections.add(node.value)
                            node.value.connections.add(it)
                        }
                    }
                }
            }

        }
    }

    private fun parseInput(input: String): Triple<Maze, Node, Pair<Int, Int>> {
        val maze = Maze(mutableMapOf())
        var maxX = 0
        var maxY = 0
        input.lines().forEachIndexed { y, line ->
            maxY = max(y, maxY)
            line.trim().forEachIndexed { x, c ->
                maxX = max(x, maxX)
                if (c != EMPTY_SPACE) {
                    maze.addNode(x, y, c)
                }
            }
        }
        maze.addConnections()
        val start = maze.getStart()!!
        parseStartShape(start)
        return Triple(maze, start, Pair(maxX, maxY))
    }

    private fun parseStartShape(start: Node) {
        val connections = start.connections.map { Pair(it.x, it.y) }
        val x = start.x
        val y = start.y

        when {
            connections.contains(Pair(x, y - 1)) -> {
                start.c = if (connections.contains(Pair(x, y + 1))) '|' else if (connections.contains(
                        Pair(
                            x - 1, y
                        )
                    )
                ) 'J' else 'L'
            }

            connections.contains(Pair(x, y + 1)) -> {
                start.c = if (connections.contains(Pair(x - 1, y))) '7' else if (connections.contains(
                        Pair(
                            x + 1, y
                        )
                    )
                ) 'F' else throw Exception("Invalid maze")
            }

            else -> start.c = '-'
        }
    }

    private fun calculateDistances(maze: Maze, start: Node): Pair<Maze, Node> {
        val queue = mutableListOf(start)
        start.dist = 0
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val newDist = current.dist!! + 1
            current.connections.forEach {
                if (it.dist == null || it.dist!! > newDist) {
                    it.dist = newDist
                    queue.add(it)
                }
            }
        }
        return Pair(maze, start)
    }

    override fun partOne(input: String): Int = parseInput(input).let { (maze, start) ->
        calculateDistances(maze, start).let { (maze, _) ->
            maze.nodes.values.mapNotNull { it.dist }.maxOrNull()!!
        }
    }

    override fun partTwo(input: String) = parseInput(input).let { (maze, start, size) ->
        calculateDistances(maze, start).let calc@{ (maze, _) ->
            val (maxX, maxY) = size
            var inside = 0
            for (y in 0..maxY) {
                var isInside = false
                var mainLoopLast = EMPTY_SPACE
                for (x in 0..maxX) {
                    val node = maze.getNode(x, y)
                    if (node?.dist != null) {
                        if (mainLoopLast == EMPTY_SPACE) {
                            mainLoopLast = node.c
                        }
                        when (node.c) {
                            '|' -> {
                                isInside = !isInside
                                mainLoopLast = EMPTY_SPACE
                            }

                            'J' -> {
                                if (mainLoopLast == 'F') {
                                    isInside = !isInside
                                }
                                mainLoopLast = EMPTY_SPACE
                            }

                            '7' -> {
                                if (mainLoopLast == 'L') {
                                    isInside = !isInside
                                }
                                mainLoopLast = EMPTY_SPACE
                            }
                        }
                    } else if (isInside) {
                        inside++
                        mainLoopLast = EMPTY_SPACE
                    }
                }
            }
            return@calc inside
        }
    }

}


