package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2023, 1, "Trebuchet?!")
class Y2023D01Test : AdventSpec<Y2023D01>({

    partOne {
        "1abc2" shouldOutput 12
        "a1b2c3d4e5f" shouldOutput 15
        """1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet""".trimIndent() shouldOutput 142
    }

    partTwo {
        "two1nine" shouldOutput 29
        "eightwothree" shouldOutput 83
        "abcone2threexyz" shouldOutput 13
        "xtwone3four" shouldOutput 24
        "4nineeightseven2" shouldOutput 42
        "zoneight234" shouldOutput 14
        "7pqrstsixteen" shouldOutput 76
        "two1nine\neightwothree\nabcone2threexyz\nxtwone3four\n4nineeightseven2\nzoneight234\n7pqrstsixteen" shouldOutput 281
    }

})
