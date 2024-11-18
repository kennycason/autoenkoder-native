import kotlin.math.ceil
import kotlin.test.Test
import kotlin.time.measureTime

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
        val elapsedMs = measureTime {
            autoEncoder.train(trainingData, learningRate = 0.2, epochs = 10_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        trainingData.forEach { xs ->
            println("input: ${xs.joinToString()}, reconstructed: ${autoEncoder.predict(xs).joinToString()}")
        }
        println("rounded")
        trainingData.forEach { xs ->
            println("input: ${xs.joinToString()}, reconstructed: ${autoEncoder.predict(xs).joinToString { round(it).toString() }}")
        }
    }

   private fun round(value: Double) = ceil(value * 10) / 10

}