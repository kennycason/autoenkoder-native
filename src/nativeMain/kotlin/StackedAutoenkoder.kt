import Matrix.subtract


data class StackedAutoenkoderConfig(
    val layers: List<LayerConfig>
) {
    data class LayerConfig(
        val inputSize: Int,
        val outputSize: Int
    )
}
/**
 * Deep learning via Stacked Autoencoders.
 */
class StackedAutoenkoder(
    config: StackedAutoenkoderConfig
) {
    private val autoenkoders: List<Autoenkoder> = config.layers.map { layerConfig ->
        Autoenkoder(layerConfig.inputSize, layerConfig.outputSize)
    }

    /**
     * Train each autoencoder layer-by-layer using the hidden outputs as inputs for the next layer.
     */
    fun train(data: Array<DoubleArray>, learningRate: Double = 0.1, epochs: Int = 1000) {
        var layerInput = data
        for ((index, autoencoder) in autoenkoders.withIndex()) {
            println("training layer ${index + 1}/${autoenkoders.size}")
            autoencoder.train(layerInput, learningRate, epochs)
            // pass the hidden layer output to the next layer
            layerInput = layerInput.map { autoencoder.encode(it) }.toTypedArray()
        }
    }

    fun calculateError(inputs: Array<DoubleArray>): Double {
        var totalError = 0.0
        for (input in inputs) {
            val inputMatrix = arrayOf(input)
            val output = predict(input)
            val errors: Array<DoubleArray> = subtract(inputMatrix, arrayOf(output))
            totalError += errors.flatMap { it.asIterable() }.sumOf { it * it }
        }
        val meanError = totalError / inputs.size
        return meanError
    }

    /**
     * Encode input data layer-by-layer.
     */
    fun encode(data: DoubleArray): List<DoubleArray> {
        var currentOutput = data
        val encodedOutputs = mutableListOf<DoubleArray>()
        for (autoencoder in autoenkoders) {
            currentOutput = autoencoder.encode(currentOutput)
            encodedOutputs.add(currentOutput)
        }
        return encodedOutputs
    }

    /**
     * Decode from feature vector / encoded representation back to the original input.
     */
    fun decode(encodedData: List<DoubleArray>): DoubleArray {
        var currentOutput = encodedData.last()
        for (autoencoder in autoenkoders.asReversed()) {
            currentOutput = autoencoder.decode(currentOutput)
        }
        return currentOutput
    }

    /**
     * Full prediction through stacked autoencoder.
     */
    fun predict(input: DoubleArray): DoubleArray {
        val encoded = encode(input)
        return decode(encoded)
    }

}