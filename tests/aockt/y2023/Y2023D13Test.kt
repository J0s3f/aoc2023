package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 13, "Point of Incidence")
class Y2023D13Test : AdventSpec<Y2023D13>({
    val officialTestcase = """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
            
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
        """.trimIndent()

    partOne {
        officialTestcase shouldOutput 405
    }

    partTwo {
        officialTestcase shouldOutput 400
    }

})
