import Matrix.add
import Matrix.applyActivation
import Matrix.dotProduct
import Matrix.hadamardProduct
import Matrix.randomMatrix
import Matrix.scalarMultiply
import Matrix.subtract
import Matrix.transpose
import kotlin.math.exp
import kotlin.random.Random

/*
 * Autoencoder Neural Network trained via backpropagation.
 */
class Autoenkoder(
    val inputSize: Int,
    val hiddenSize: Int
) {
    private var encodeWeights = randomMatrix(inputSize, hiddenSize)
    private var decodeWeights = randomMatrix(hiddenSize, inputSize)
    private var biasHidden = DoubleArray(hiddenSize) { Random.nextDouble(-1.0, 1.0) }
    private var biasOutput = DoubleArray(inputSize) { Random.nextDouble(-1.0, 1.0) }

    // activation functions
    private fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))
    private fun sigmoidDerivative(x: Double): Double = x * (1.0 - x)

    fun train(inputs: Array<DoubleArray>, learningRate: Double = 0.1, epochs: Int = 1000) {
        repeat(epochs) { epoch ->
            var totalError = 0.0

            for (input in inputs) {
                val inputMatrix = arrayOf(input)

                // forward propagation
                val hiddenInput = add(dotProduct(inputMatrix, encodeWeights), arrayOf(biasHidden))
                val hiddenOutput = applyActivation(hiddenInput, ::sigmoid)

                val outputInput = add(dotProduct(hiddenOutput, decodeWeights), arrayOf(biasOutput))
                val output = applyActivation(outputInput, ::sigmoid)

                // calculate error
                val errors: Array<DoubleArray> = subtract(inputMatrix, output)
                totalError += errors.flatMap { it.asIterable() }.sumOf { it * it }

                // back propagation (output layer)
                val outputGradient = hadamardProduct(errors, applyActivation(output, ::sigmoidDerivative))
                val hiddenOutputTransposed = transpose(hiddenOutput)
                val deltaWeightsHiddenOutput = dotProduct(hiddenOutputTransposed, outputGradient)

                // back propagation (hidden layer)
                val weightsHiddenOutputTransposed = transpose(decodeWeights)
                val hiddenError = dotProduct(outputGradient, weightsHiddenOutputTransposed)
                val hiddenGradient = hadamardProduct(hiddenError, applyActivation(hiddenOutput, ::sigmoidDerivative))
                val inputTransposed = transpose(inputMatrix)
                val deltaWeightsInputHidden = dotProduct(inputTransposed, hiddenGradient)

                // update weights and biases
                decodeWeights = add(decodeWeights, scalarMultiply(deltaWeightsHiddenOutput, learningRate))
                encodeWeights = add(encodeWeights, scalarMultiply(deltaWeightsInputHidden, learningRate))
                biasOutput = add(arrayOf(biasOutput), scalarMultiply(outputGradient, learningRate))[0]
                biasHidden = add(arrayOf(biasHidden), scalarMultiply(hiddenGradient, learningRate))[0]
            }

            // calculate mean-squared error for epoc
            val meanError = totalError / inputs.size
            if (epoch % 100 == 0) {
                println("epoch $epoch, error: $meanError")
            }
        }
    }

    fun calculateError(inputs: Array<DoubleArray>): Double {
        var totalError = 0.0
        for (input in inputs) {
            val inputMatrix = arrayOf(input)

            // forward propagation
            val hiddenInput = add(dotProduct(inputMatrix, encodeWeights), arrayOf(biasHidden))
            val hiddenOutput = applyActivation(hiddenInput, ::sigmoid)

            val outputInput = add(dotProduct(hiddenOutput, decodeWeights), arrayOf(biasOutput))
            val output = applyActivation(outputInput, ::sigmoid)

            // calculate error
            val errors: Array<DoubleArray> = subtract(inputMatrix, output)
            totalError += errors.flatMap { it.asIterable() }.sumOf { it * it }
        }

        val meanError = totalError / inputs.size
        return meanError
    }

    fun encode(input: DoubleArray): DoubleArray {
        val inputMatrix = arrayOf(input)
        val hiddenInput = add(dotProduct(inputMatrix, encodeWeights), arrayOf(biasHidden))
        return applyActivation(hiddenInput, ::sigmoid)[0]
    }

    fun decode(encoded: DoubleArray): DoubleArray {
        val encodedMatrix = arrayOf(encoded)
        val outputInput = add(dotProduct(encodedMatrix, decodeWeights), arrayOf(biasOutput))
        return applyActivation(outputInput, ::sigmoid)[0]
    }

    fun predict(input: DoubleArray): DoubleArray {
        return decode(encode(input))
    }

}
