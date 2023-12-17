package aockt.y2023

import io.github.jadarma.aockt.core.Solution


object Y2023D15 : Solution {
    private val LENS_REGEX = """(?<label>[a-z]+)(?<operation>[=-])(?<power>[0-9]+)?""".toRegex()
    private fun parseInput(input: String): List<String> {
        return input.split(",")
    }

    private fun hash(s: String): Int {
        val codes = s.map { it.code }
        var hash = 0
        for (code in codes) {
            hash += code
            hash *= 17
            hash %= 256
        }
        return hash
    }

    override fun partOne(input: String): Int {
        return parseInput(input).sumOf { hash(it) }
    }

    enum class Operation(val s: String) {
        REMOVE("-"), ADD("=");
    }

    data class Lens(val label: String, val power: Int?) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Lens

            return label == other.label
        }

        override fun hashCode(): Int {
            return label.hashCode()
        }
    }
    data class Line(val lens:Lens,val operation: Operation) {
        override fun toString(): String {
            return lens.label + operation.s + (lens.power ?: "")
        }
    }

    override fun partTwo(input: String): Int {
        val input = parseInput(input).map { parseLens(it) }
        val hashmap = List<MutableList<Lens>>(256) { mutableListOf() }
        for(line in input) {
            val hash = hash(line.lens.label)
            val box = hashmap[hash]
            when (line.operation) {
                Operation.REMOVE -> box.remove(line.lens)
                Operation.ADD -> {
                    line.lens.power!!
                    val index = box.indexOf(line.lens)
                    if(index!=-1) {
                        box[index] = line.lens
                    } else {
                        box.addLast(line.lens)
                    }
                }
            }
        }

        return hashmap.flatMapIndexed  { i, box ->
                box.mapIndexed { j, lens ->
                    (i + 1) * (j + 1) * lens.power!!
                }
        }.sum()
    }

    private fun parseLens(lens: String): Line {
        val match = LENS_REGEX.matchEntire(lens)!!
        val label = match.groups["label"]!!.value
        val power = match.groups["power"]?.value?.toInt()
        val operation = when (match.groups["operation"]!!.value) {
            "=" -> Operation.ADD
            "-" -> Operation.REMOVE
            else -> {
                throw Exception("Invalid data")
            }
        }
        if(operation == Operation.ADD) {
            power!!
        }
        return Line(Lens(label,power), operation)
    }

}


