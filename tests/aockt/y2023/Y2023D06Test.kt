package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 6, "Wait For It")
class Y2023D06Test : AdventSpec<Y2023D06>({
    val officialTestcase = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent()

    partOne {
        officialTestcase shouldOutput 288
    }

    partTwo {
        officialTestcase shouldOutput 71503
    }

})
