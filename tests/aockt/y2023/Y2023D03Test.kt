package aockt.y2023

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

private val officialTestcase = """
    467..114..
    ...*......
    ..35..633.
    ......#...
    617*......
    .....+.58.
    ..592.....
    ......755.
    ...${'$'}.*....
    .664.598..
    """.trimIndent()

// more tests from https://www.reddit.com/r/adventofcode/comments/189q9wv/2023_day_3_another_sample_grid_to_use/
private val redditTest1 = """
    12.......*..
    +.........34
    .......-12..
    ..78........
    ..*....60...
    78..........
    .......23...
    ....90*12...
    ............
    2.2......12.
    .*.........*
    1.1.......56
""".trimIndent()

private val redditTest2 = """
    12.......*..
    +.........34
    .......-12..
    ..78........
    ..*....60...
    78.........9
    .5.....23..${'$'}
    8...90*12...
    ............
    2.2......12.
    .*.........*
    1.1..503+.56
""".trimIndent()

private val redditTest3 = """
    ....................
    ..-52..52-..52..52..
    ..................-.
""".trimIndent()

private val redditTest4 = """
    ........
    .24..4..
    ......*.
""".trimIndent()

private val redditTest5 = """
    .......5......
    ..7*..*.......
    ...*13*.......
    .......15.....
""".trimIndent()

@AdventDay(2023, 3, "Gear Ratios")
class Y2023D03Test : AdventSpec<Y2023D03>({

    partOne {
        officialTestcase shouldOutput 4361
        redditTest1 shouldOutput 413
        redditTest2 shouldOutput 925
        redditTest3 shouldOutput 156
        redditTest4 shouldOutput 4
        redditTest5 shouldOutput 40
    }

    partTwo {
        officialTestcase shouldOutput 467835
        redditTest1 shouldOutput 6756
        redditTest2 shouldOutput 6756
        redditTest3 shouldOutput 0
        redditTest4 shouldOutput 0
        redditTest5 shouldOutput 442
        "333.3\n...*." shouldOutput 999
    }

})
