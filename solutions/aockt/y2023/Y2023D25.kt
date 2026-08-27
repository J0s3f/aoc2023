package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D25 : Solution {
    private const val CUT_SIZE = 3

    private class Network(val size: Int, wires: List<Pair<Int, Int>>) {
        private val target = IntArray(wires.size * 2)
        private val capacity = IntArray(wires.size * 2)
        private val outgoing = List(size) { mutableListOf<Int>() }

        init {
            wires.forEachIndexed { index, (a, b) ->
                target[2 * index] = b
                target[2 * index + 1] = a
                outgoing[a].add(2 * index)
                outgoing[b].add(2 * index + 1)
            }
        }

        private fun reach(source: Int): IntArray {
            val cameFrom = IntArray(size) { NOT_REACHED }
            cameFrom[source] = START
            val queue = ArrayDeque<Int>()
            queue.add(source)
            while (queue.isNotEmpty()) {
                val node = queue.removeFirst()
                for (edge in outgoing[node]) {
                    val next = target[edge]
                    if (capacity[edge] > 0 && cameFrom[next] == NOT_REACHED) {
                        cameFrom[next] = edge
                        queue.add(next)
                    }
                }
            }
            return cameFrom
        }

        fun sourceSideOfCut(source: Int, sink: Int): Int? {
            capacity.fill(1)
            repeat(CUT_SIZE) {
                val cameFrom = reach(source)
                if (cameFrom[sink] == NOT_REACHED) return null
                var node = sink
                while (node != source) {
                    val edge = cameFrom[node]
                    capacity[edge]--
                    capacity[edge xor 1]++
                    node = target[edge xor 1]
                }
            }
            val cameFrom = reach(source)
            if (cameFrom[sink] != NOT_REACHED) return null
            return cameFrom.count { it != NOT_REACHED }
        }

        private companion object {
            const val NOT_REACHED = -1
            const val START = -2
        }
    }

    private fun parseInput(input: String): Network {
        val ids = mutableMapOf<String, Int>()
        val wires = input
            .lines()
            .flatMap { line ->
                val (name, others) = line.split(": ")
                others.split(' ').map { name to it }
            }
            .map { (a, b) -> ids.getOrPut(a) { ids.size } to ids.getOrPut(b) { ids.size } }
        return Network(ids.size, wires)
    }

    override fun partOne(input: String): Int {
        val network = parseInput(input)
        for (sink in 1 until network.size) {
            val sourceSide = network.sourceSideOfCut(0, sink) ?: continue
            return sourceSide * (network.size - sourceSide)
        }
        error("No cut of $CUT_SIZE wires splits the network.")
    }
}
