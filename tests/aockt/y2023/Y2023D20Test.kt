package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 20, "Pulse Propagation")
class Y2023D20Test : AdventSpec<Y2023D20>({

    val officialTestcase1 = """
            broadcaster -> a, b, c
            %a -> b
            %b -> c
            %c -> inv
            &inv -> a
        """.trimIndent()
    val officialTestcase2 = """
            broadcaster -> a
            %a -> inv, con
            &inv -> b
            %b -> con
            &con -> output
        """.trimIndent()
    partOne {
        officialTestcase1 shouldOutput 32000000
        officialTestcase2 shouldOutput 11687500
    }

    partTwo {

    }

})
