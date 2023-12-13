package aockt.y2023

import com.sksamuel.aedile.core.Cache
import com.sksamuel.aedile.core.cacheBuilder
import io.github.jadarma.aockt.core.Solution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking


object Y2023D12 : Solution {
    private val LINE_REGEX = """(?<springs>[.#?]+)\s+(?<groups>((\d+),?)+)""".toRegex()

    data class Line(val springs: String, val groups: List<Int>)

    data class CalcParams(val i: Int, val j: Int, val cur: Int, val seq: String, val nums: List<Int>)

    private fun parseInput(input: String): List<Line> {
        return input.lines().mapNotNull {
            LINE_REGEX.find(it)?.let { line ->
                val springs = line.groups["springs"]!!.value
                val groups = line.groups["groups"]!!.value.split(",").map { it.toInt() }
                Line(springs, groups)
            }
        }.toList()
    }


    private fun calc(param: CalcParams): Int {
        val (i, j, cur, seq, nums) = param
        if (i == seq.length) {
            return if ((j == nums.size - 1 && nums[j] == cur) || (j == nums.size && cur == 0)) 1 else 0
        }
        var res = 0
        if (seq[i] in "#?") {
            res += calc(CalcParams(i + 1, j, cur + 1, seq, nums))
        }
        if (seq[i] in ".?") {
            if (cur == 0) {
                res += calc(CalcParams(i + 1, j, 0, seq, nums))
            } else if (cur > 0 && j < nums.size && nums[j] == cur) {
                res += calc(CalcParams(i + 1, j + 1, 0, seq, nums))
            }
        }
        return res
    }

    private suspend fun cached(param: CalcParams, cache: Cache<CalcParams, Long>): Long {
        val (i, j, cur, seq, nums) = param
        if (i == seq.length) {
            return if ((j == nums.size - 1 && nums[j] == cur) || (j == nums.size && cur == 0)) 1 else 0
        }
        var res = 0L
        if (seq[i] in "#?") {
            res += cache.get(CalcParams(i + 1, j, cur + 1, seq, nums)) { cached(it, cache) }
        }
        if (seq[i] in ".?") {
            if (cur == 0) {
                res += cache.get(CalcParams(i + 1, j, 0, seq, nums)) { cached(it, cache) }
            } else if (cur > 0 && j < nums.size && nums[j] == cur) {
                res += cache.get(CalcParams(i + 1, j + 1, 0, seq, nums)) { cached(it, cache) }
            }
        }
        return res
    }


    override fun partOne(input: String): Int =
        parseInput(input).sumOf { calc(CalcParams(0, 0, 0, it.springs, it.groups)) }


    override fun partTwo(input: String): Long = runBlocking(context = Dispatchers.Default) {
        parseInput(input).map { line ->
            Line(List(5) { line.springs }.joinToString("?"),
                List(line.groups.size * 5) { line.groups[it % line.groups.size] })
        }.map {
            async {
                val cache = cacheBuilder<CalcParams, Long>().build()
                val params = CalcParams(0, 0, 0, it.springs, it.groups)
                cache.get(params) { cached(params, cache) }
            }
        }.awaitAll().sum()
    }
}





