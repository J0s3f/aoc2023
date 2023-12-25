package util

object ParseHelper {
    fun parseCharField(input: String): Array<CharArray> =
        getLinesSameLength(input).map { it.toCharArray() }.toTypedArray()

    fun parseIntField(input: String): Array<IntArray> =
        getLinesSameLength(input).map { line -> line.map { it.digitToInt() }.toIntArray() }.toTypedArray()

    private fun getLinesSameLength(input: String): List<String> {
        val lines = input.lines()
        val columns = lines[0].length
        for (line in lines) {
            if (line.length != columns) {
                throw Exception("Invalid input data")
            }
        }
        return lines
    }
}