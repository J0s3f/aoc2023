object ParseHelper {
    fun parseCharField(input: String): Array<CharArray> {
        val lines = input.lines()
        val columns = lines[0].length
        for (line in lines) {
            if (line.length != columns) {
                throw Exception("Invalid input data")
            }
        }
        return lines.map { it.toCharArray() }.toTypedArray()
    }
}