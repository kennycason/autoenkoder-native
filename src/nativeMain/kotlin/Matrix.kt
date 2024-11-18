import kotlin.random.Random

object Matrix {

    fun randomMatrix(rows: Int, cols: Int, range: Double = 1.0): Array<DoubleArray> =
        Array(rows) { DoubleArray(cols) { Random.nextDouble(-range, range) } }

    fun dotProduct(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val rowsA = a.size
        val colsA = a[0].size
        val colsB = b[0].size
        val result = Array(rowsA) { DoubleArray(colsB) }

        for (i in 0 until rowsA) {
            for (j in 0 until colsB) {
                for (k in 0 until colsA) {
                    result[i][j] += a[i][k] * b[k][j]
                }
            }
        }
        return result
    }

    fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        return Array(cols) { col -> DoubleArray(rows) { row -> matrix[row][col] } }
    }

    fun applyActivation(matrix: Array<DoubleArray>, activation: (Double) -> Double): Array<DoubleArray> {
        val result = Array(matrix.size) { DoubleArray(matrix[0].size) }
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                result[i][j] = activation(matrix[i][j])
            }
        }
        return result
    }

    fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val result = Array(a.size) { DoubleArray(a[0].size) }
        for (i in a.indices) {
            for (j in a[i].indices) {
                result[i][j] = a[i][j] + b[i][j]
            }
        }
        return result
    }

    fun subtract(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val result = Array(a.size) { DoubleArray(a[0].size) }
        for (i in a.indices) {
            for (j in a[i].indices) {
                result[i][j] = a[i][j] - b[i][j]
            }
        }
        return result
    }

    fun hadamardProduct(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        return Array(a.size) { i ->
            DoubleArray(a[i].size) { j ->
                a[i][j] * b[i][j]
            }
        }
    }

    fun scalarMultiply(matrix: Array<DoubleArray>, scalar: Double): Array<DoubleArray> {
        val result = Array(matrix.size) { DoubleArray(matrix[0].size) }
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                result[i][j] = matrix[i][j] * scalar
            }
        }
        return result
    }

    // experimental, much slower 1188ms (vs ^ 272ms)
//    private fun applyActivation(matrix: Array<DoubleArray>, activation: (Double) -> Double): Array<DoubleArray> =
//        matrix.map { row -> row.map { activation(it) }.toDoubleArray() }.toTypedArray()

//    private fun subtract(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> =
//        a.mapIndexed { i, row -> row.mapIndexed { j, value -> value - b[i][j] }.toDoubleArray() }.toTypedArray()

//    private fun hadamardProduct(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> =
//        a.mapIndexed { i, row -> row.mapIndexed { j, value -> value * b[i][j] }.toDoubleArray() }.toTypedArray()

//    private fun scalarMultiply(matrix: Array<DoubleArray>, scalar: Double): Array<DoubleArray> =
//        matrix.map { row -> row.map { it * scalar }.toDoubleArray() }.toTypedArray()

//    private fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> =
//        a.mapIndexed { i, row -> row.mapIndexed { j, value -> value + b[i][j] }.toDoubleArray() }.toTypedArray()

}