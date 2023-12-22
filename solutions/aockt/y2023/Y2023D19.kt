package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import java.util.function.IntPredicate


object Y2023D19 : Solution {
    private val WORKFLOW_REGEX = """(?<name>\w+)\{(?<rules>.+),(?<target>\w+)}""".toRegex()
    private val RULE_REGEX = """(?<var>[xmas])(?<op>[<>])(?<num>\d+):(?<target>\w+)""".toRegex()
    private val PART_REGEX = """\{x=(?<x>\d+),m=(?<m>\d+),a=(?<a>\d+),s=(?<s>\d+)}""".toRegex()

    data class Rule(val variable: String, val op: Char, val limit: Int, val target: String) {
        val check = when (op) {
            '<' -> IntPredicate { it < limit }
            '>' -> IntPredicate { it > limit }
            else -> throw IllegalArgumentException("Invalid operator: $op")
        }
    }

    data class Workflow(val name: String, val rules: List<Rule>, val fallbackTarget: String)
    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun getVar(variable: String): Int =
            when (variable) {
                "x" -> x
                "m" -> m
                "a" -> a
                "s" -> s
                else -> throw IllegalArgumentException("Invalid variable: $variable")
            }

        fun sum(): Long {
            return 0L + x + m + a + s
        }
    }

    data class SplitResult(val toTarget: RangePart?, val toNext: RangePart?)

    data class RangePart(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {
        fun getVar(variable: String): IntRange = when (variable) {
            "x" -> x
            "m" -> m
            "a" -> a
            "s" -> s
            else -> throw IllegalArgumentException("Invalid variable: $variable")
        }

        private fun withVar(variable: String, value: IntRange): RangePart = when (variable) {
            "x" -> RangePart(value, m, a, s)
            "m" -> RangePart(x, value, a, s)
            "a" -> RangePart(x, m, value, s)
            "s" -> RangePart(x, m, a, value)
            else -> throw IllegalArgumentException("Invalid variable: $variable")
        }

        fun splitAt(variable: String, op: Char, value: Int): SplitResult {
            val range = getVar(variable)
            val result: SplitResult
            if (!(range.contains(value))) {
                result = if (value < range.first) {
                    when (op) {
                        '<' -> SplitResult(this, null)
                        '>' -> SplitResult(null, this)
                        else -> throw IllegalArgumentException("Invalid operator: $op")
                    }
                } else {
                    when (op) {
                        '<' -> SplitResult(null, this)
                        '>' -> SplitResult(this, null)
                        else -> throw IllegalArgumentException("Invalid operator: $op")
                    }
                }
            } else {
                result = when (op) {
                    '<' -> SplitResult(withVar(variable, range.first..<value), withVar(variable, value..range.last))
                    '>' -> SplitResult(
                        withVar(variable, (value + 1)..range.last),
                        withVar(variable, range.first..value)
                    )

                    else -> throw IllegalArgumentException("Invalid operator: $op")
                }
            }
            return result


        }

        private fun inRange(range: IntRange): Long = range.last.toLong() - range.first + 1

        fun possibleParts(): Long {
            return inRange(x) * inRange(m) * inRange(a) * inRange(s)
        }
    }

    data class System(private val workflows: Map<String, Workflow>, val parts: List<Part>) {

        fun getWorkflow(name: String): Workflow? = workflows[name]

        constructor(workflows: List<Workflow>, parts: List<Part>) : this(workflows.associateBy { it.name }, parts)
    }

    private fun parseInput(input: String): System {
        val inSplit = input.split("""\r?\n\r?\n""".toRegex())
        val workflows = inSplit[0].lines().map { parseWorkflow(it) }
        val parts = inSplit[1].lines().map { parsePart(it) }
        return System(workflows, parts)

    }

    private fun parsePart(p: String): Part {
        val match = PART_REGEX.matchEntire(p)!!
        val x = match.groups["x"]!!.value.toInt()
        val m = match.groups["m"]!!.value.toInt()
        val a = match.groups["a"]!!.value.toInt()
        val s = match.groups["s"]!!.value.toInt()
        return Part(x, m, a, s)
    }

    private fun parseWorkflow(w: String): Workflow {
        val match = WORKFLOW_REGEX.matchEntire(w)!!
        val name = match.groups["name"]!!.value
        val rules = match.groups["rules"]!!.value.split(",").map {
            val ruleMatch = RULE_REGEX.matchEntire(it)!!
            val variable = ruleMatch.groups["var"]!!.value
            val op = ruleMatch.groups["op"]!!.value[0]
            val num = ruleMatch.groups["num"]!!.value.toInt()
            val target = ruleMatch.groups["target"]!!.value
            Rule(variable, op, num, target)
        }
        val target = match.groups["target"]!!.value
        return Workflow(name, rules, target)
    }

    private fun getTarget(workflow: Workflow, part: Part): String {
        for (rule in workflow.rules) {
            if (rule.check.test(part.getVar(rule.variable))) {
                return rule.target
            }
        }
        return workflow.fallbackTarget
    }

    private fun runWorkflows(system: System): Long {
        var sum = 0L
        val start = system.getWorkflow("in")!!
        val end = setOf("R", "A")
        for (part in system.parts) {
            var workflow = start
            while (true) {
                val target = getTarget(workflow, part)
                if (target in end) {
                    if (target == "A") {
                        sum += part.sum()
                    }
                    break
                }
                workflow = system.getWorkflow(target)!!
            }
        }
        return sum
    }

    private fun checkRanges(system: System): Long {
        val start = system.getWorkflow("in")!!
        val todo = mutableListOf(Pair(RangePart(1..4000, 1..4000, 1..4000, 1..4000), start))
        var sum = 0L

        fun split(workflow: Workflow, pt: RangePart) {
            var part = pt
            val iter = workflow.rules.iterator()
            while (iter.hasNext()) {
                val rule = iter.next()
                val result = part.splitAt(rule.variable, rule.op, rule.limit)
                if (result.toTarget != null) {
                    when (rule.target) {
                        "R" -> {
                            //do nothing
                        }

                        "A" -> {
                            sum += result.toTarget.possibleParts()
                        }

                        else -> {
                            todo.add(Pair(result.toTarget, system.getWorkflow(rule.target)!!))
                        }
                    }
                }
                if (result.toNext != null) {
                    if (iter.hasNext()) {
                        part = result.toNext
                    } else {
                        when (workflow.fallbackTarget) {
                            "R" -> {
                                //do nothing
                            }

                            "A" -> {
                                sum += result.toNext.possibleParts()
                            }

                            else -> {
                                todo.add(Pair(result.toNext, system.getWorkflow(workflow.fallbackTarget)!!))
                            }
                        }
                    }
                }
            }
        }


        while (todo.isNotEmpty()) {
            val (part, workflow) = todo.removeAt(0)
            split(workflow, part)
        }
        return sum
    }


    override fun partOne(input: String): Long {
        val system = parseInput(input)

        return runWorkflows(system)
    }


    override fun partTwo(input: String): Long = checkRanges(parseInput(input))


}


