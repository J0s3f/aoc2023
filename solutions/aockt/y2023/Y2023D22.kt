package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import util.spatial.Area
import util.spatial.overlaps
import util.spatial.spatial3d.Point3D

object Y2023D22 : Solution {
    private data class SandBrick(val start: Point3D, val end: Point3D) {

        init {
            require(start.z <= end.z) { "Start and end given out of order." }
            require(start.z >= 1) { "Sand brick collides with ground." }
            val isHorizontal = start.z == end.z && (start.x == end.x || start.y == end.y)
            val isVertical = start.x == end.x && start.y == end.y
            require(isHorizontal || isVertical) { "The sand brick must be a straight line." }
        }

        val fallingArea: Area = Area(
            xRange = minOf(start.x, end.x)..maxOf(start.x, end.x),
            yRange = minOf(start.y, end.y)..maxOf(start.y, end.y),
        )

        fun fallTo(restHeight: Long): SandBrick = SandBrick(
            start = start.copy(z = restHeight),
            end = end.copy(z = end.z - start.z + restHeight),
        )

        fun fallingAreaOverlaps(other: SandBrick): Boolean = fallingArea overlaps other.fallingArea
    }

    private class SandBrickSimulator(bricks: Iterable<SandBrick>) {

        val settledBricks: List<SandBrick> =
            bricks
                .toMutableList()
                .apply {
                    sortBy { it.start.z }
                    forEachIndexed { index, brick ->
                        this[index] = slice(0..<index)
                            .filter { brick.fallingAreaOverlaps(it) }
                            .maxOfOrNull { it.end.z + 1 }
                            .let { restHeight -> brick.fallTo(restHeight ?: 1L) }
                    }
                }
                .sortedBy { it.start.z }

        val supportedBy: Map<SandBrick, Set<SandBrick>>

        private val SandBrick.supportedBricks: Set<SandBrick> get() = supportedBy.getValue(this)

        val supporting: Map<SandBrick, Set<SandBrick>>

        private val SandBrick.standingOn: Set<SandBrick> get() = supporting.getValue(this)

        init {
            val supportedBy: Map<SandBrick, MutableSet<SandBrick>> = settledBricks.associateWith { mutableSetOf() }
            val supporting: Map<SandBrick, MutableSet<SandBrick>> = settledBricks.associateWith { mutableSetOf() }

            settledBricks.forEachIndexed { index, above ->
                settledBricks.slice(0..<index).forEach { below ->
                    if (below.fallingAreaOverlaps(above) && above.start.z == below.end.z + 1) {
                        supportedBy.getValue(below).add(above)
                        supporting.getValue(above).add(below)
                    }
                }
            }

            this.supportedBy = supportedBy
            this.supporting = supporting
        }

        val redundantBricks: Set<SandBrick> =
            settledBricks
                .filter { it.supportedBricks.all { supported -> supported.standingOn.count() >= 2 } }
                .toSet()

        fun fallingBricksIfDisintegrating(brick: SandBrick): Set<SandBrick> = buildSet {
            require(brick in supporting) { "The brick $brick is not part of the simulation." }
            val fallingBricks = this

            brick.supportedBricks
                .filter { supported -> supported.standingOn.size == 1 }
                .let(fallingBricks::addAll)

            val queue = ArrayDeque(elements = fallingBricks)

            while (queue.isNotEmpty()) {
                queue.removeFirst()
                    .supportedBricks
                    .minus(fallingBricks)
                    .filter { supportedByFalling -> fallingBricks.containsAll(supportedByFalling.standingOn) }
                    .onEach(fallingBricks::add)
                    .forEach(queue::add)
            }
        }
    }

    private fun parseInput(input: String): List<SandBrick> =
        input
            .lineSequence()
            .map { line -> line.replace('~', ',') }
            .map { line -> line.split(',', limit = 6).map(String::toInt) }
            .onEach { require(it.size == 6) }
            .map {
                SandBrick(
                    start = it.take(3).let { (x, y, z) -> Point3D(x, y, z) },
                    end = it.takeLast(3).let { (x, y, z) -> Point3D(x, y, z) },
                )
            }
            .toList()

    override fun partOne(input: String) = parseInput(input).let(::SandBrickSimulator).redundantBricks.count()
    override fun partTwo(input: String) = parseInput(input).let(::SandBrickSimulator).run {
        settledBricks
            .map(::fallingBricksIfDisintegrating)
            .sumOf { it.count() }
    }
}
