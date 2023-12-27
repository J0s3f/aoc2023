package aockt.y2023

import aockt.y2023.Y2023D20.Pulse.*
import io.github.jadarma.aockt.core.Solution
import util.lcm


object Y2023D20 : Solution {
    private val LINE_REGEX = """(?<type>broadcaster|%|&)(?<name>\w*)\s+->\s+(?<args>[\w, ]+)""".toRegex()

    enum class Pulse { LOW, HIGH }

    data class Signal(val source: String, val pulse: Pulse, val destination: String)

    interface Module {
        val name: String
        val inputs: Set<String>
        val outputs: Set<String>

        fun process(signal: Signal): Pulse?

        fun receiveAndEmit(signal: Signal): List<Signal> {
            if (signal.destination != name) {
                throw Exception("Sent Signal to wrong Module")
            }
            val output = process(signal) ?: return emptyList()
            return outputs.map { Signal(name, output, it) }
        }

    }

    data class BroadcastModule(
        override val name: String,
        override val inputs: Set<String>,
        override val outputs: Set<String>,
    ) : Module {
        override fun process(signal: Signal): Pulse = signal.pulse
    }

    data class FlipFlopModule(
        override val name: String,
        override val inputs: Set<String>,
        override val outputs: Set<String>,
    ) : Module {

        private var state: Pulse = LOW

        override fun process(signal: Signal): Pulse? {
            if (signal.pulse == HIGH) return null
            state = if (state == HIGH) LOW else HIGH
            return state
        }
    }

    data class ConjunctionModule(
        override val name: String,
        override val inputs: Set<String>,
        override val outputs: Set<String>,
    ) : Module {
        private val state: MutableMap<String, Pulse> = inputs.associateWith { LOW }.toMutableMap()

        override fun process(signal: Signal): Pulse {
            state[signal.source] = signal.pulse
            return if (state.values.all { it == HIGH }) LOW else HIGH
        }
    }

    data class DebugModule(override val name: String, override val inputs: Set<String>) : Module {
        override val outputs: Set<String> = emptySet()
        override fun process(signal: Signal) = null
    }

    class System(private val modules: Map<String, Module>) {

        private var totalPresses: Int = 0

        private fun pressButton(): Sequence<Signal> = sequence {
            totalPresses++
            val queue = ArrayDeque<Signal>()
            queue.add(Signal("button", LOW, "broadcaster"))
            while (queue.isNotEmpty()) {
                val signal = queue.removeFirst()
                yield(signal)
                modules
                    .getValue(signal.destination)
                    .receiveAndEmit(signal)
                    .let(queue::addAll)
            }
        }

        fun signalScoreAfter(iterations: Int): Long {
            if (totalPresses != 0) {
                throw Exception("Can only start simulation from blank state")
            }
            var low = 0
            var high = 0
            repeat(iterations) { pressButton().forEach { if (it.pulse == LOW) low++ else high++ } }
            return low.toLong() * high
        }

        fun estimatedPressesForRx(): Long {
            if (totalPresses != 0) {
                throw Exception("Can only start simulation from blank state")
            }

            val rx = modules["rx"]
            checkNotNull(rx) { "No module with ID 'rx' found. " }
            check(rx is DebugModule) { "Assumption failed: 'rx' should be a debug module." }
            check(rx.inputs.size == 1) { "Assumption failed: 'rx' should have a single input." }

            val rxIn = rx.inputs.first().let(modules::getValue)
            check(rxIn is ConjunctionModule) { "Assumption failed: 'rx' input is not a conjunction module." }

            val cycles: MutableMap<String, Int> = rxIn.inputs.associateWith { -1 }.toMutableMap()
            while (cycles.values.any { it == -1 }) {
                pressButton()
                    .filter { signal -> signal.destination == rxIn.name && signal.pulse == HIGH }
                    .filter { signal -> cycles[signal.source] == -1 }
                    .forEach { signal -> cycles[signal.source] = totalPresses }
            }
            return cycles.values.lcm()
        }
    }


    private fun parseInput(input: String): System {
        val names: MutableSet<String> = mutableSetOf()
        val types: MutableMap<String, String> = mutableMapOf()
        val outputs: MutableMap<String, Set<String>> = mutableMapOf()
        val inputs: MutableMap<String, MutableSet<String>> = mutableMapOf()
        input.lines().map { line ->
            val parsed = LINE_REGEX.matchEntire(line)!!
            val type = parsed.groups["type"]!!.value
            val name = if (type == "broadcaster") "broadcaster" else parsed.groups["name"]!!.value
            val outputModules =
                parsed.groups["args"]!!.value.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()
            outputModules.forEach { inputs.getOrPut(it) { mutableSetOf() }.add(name) }
            names.add(name)
            names.addAll(outputModules)
            outputs[name] = outputModules
            types[name] = type
        }
        return names
            .map { module ->
                val inModules = inputs[module] ?: emptySet()
                val outModules = outputs[module] ?: emptySet()
                when (types[module]) {
                    "broadcaster" -> BroadcastModule(module, inModules, outModules)
                    "%" -> FlipFlopModule(module, inModules, outModules)
                    "&" -> ConjunctionModule(module, inModules, outModules)
                    null -> DebugModule(module, inModules)
                    else -> error("Impossible state.")
                }
            }
            .associateBy { it.name }
            .let(::System)
    }

    override fun partOne(input: String) = parseInput(input).signalScoreAfter(iterations = 1000)
    override fun partTwo(input: String) = parseInput(input).estimatedPressesForRx()

}


