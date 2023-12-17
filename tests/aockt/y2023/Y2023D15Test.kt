package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 15, "Lens Library")
class Y2023D15Test : AdventSpec<Y2023D15>({

    partOne {
        "HASH" shouldOutput 52
        "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7" shouldOutput 1320
    }

    partTwo {
        "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7" shouldOutput 145
    }

})
