import kotlin.math.round
import kotlin.test.Test

class AutoenkoderTest {

    @Test
    fun `autoencoder - XNOR Parity Check`() {
        val autoEncoder = Autoenkoder(inputSize = 3, hiddenSize = 2)
        val trainingData = arrayOf(
            doubleArrayOf(0.0, 0.0, 1.0),
            doubleArrayOf(0.0, 1.0, 0.0),
            doubleArrayOf(1.0, 0.0, 0.0),
            doubleArrayOf(1.0, 1.0, 1.0)
        )
        autoEncoder.train(trainingData, learningRate = 0.2, epochs = 10_000)
        trainingData.forEach { xs ->
            println("input: ${xs.joinToString()}, reconstructed: ${autoEncoder.predict(xs).joinToString()}")
        }
    }

}