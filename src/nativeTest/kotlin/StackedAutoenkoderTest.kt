import StackedAutoenkoderConfig.*
import kotlin.math.ceil
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.measureTime

class StackedAutoenkoderTest {

    @Test
    fun `stacked autoencoder - pokeball color`() {
        val filePath = "./images/pokeball.bmp"
        val bitmap = BitmapIO.read(filePath)
        val trainingData = arrayOf(BitmapPixelDataTransforms.toAutoenkoderInput(bitmap))

        val bitmapInputSize = trainingData.first().size
        val config = StackedAutoenkoderConfig(
            layers = listOf(
                LayerConfig(inputSize = bitmapInputSize, outputSize = bitmapInputSize / 4),
                LayerConfig(inputSize = bitmapInputSize / 4, outputSize = 10)
            )
        )

        val stackedAutoenkoder = StackedAutoenkoder(config)
        val elapsedMs = measureTime {
            stackedAutoenkoder.train(trainingData, learningRate = 0.075, epochs = 20_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        println("error: ${stackedAutoenkoder.calculateError(trainingData)}")

        val x = trainingData.first()
        val y = stackedAutoenkoder.predict(x)
        BitmapIO.write(
            filePath = "./output/pokeball_color_deep_learned.bmp",
            bitmap = Bitmap(
                header = bitmap.header,
                data = BitmapPixelDataTransforms.toBitMapData(bitmap.header.pixelFormat, y)
            )
        )
    }

    @Test
    fun `stacked autoencoder - random vectors`() {
        val trainingData = Array(5) {
            DoubleArray(25) { Random.nextDouble(0.0, 1.0) }
        }
        val inputSize = trainingData.first().size
        val config = StackedAutoenkoderConfig(
            layers = listOf(
                LayerConfig(inputSize = inputSize, outputSize = inputSize / 2),
                LayerConfig(inputSize = inputSize / 2, outputSize =  inputSize / 4)
            )
        )

        val stackedAutoenkoder = StackedAutoenkoder(config)
        val elapsedMs = measureTime {
            stackedAutoenkoder.train(trainingData, learningRate = 0.075, epochs = 10_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        println("error: ${stackedAutoenkoder.calculateError(trainingData)}")

        trainingData.forEach { x ->
            val y = stackedAutoenkoder.predict(x)
            println("input:  ${x.joinToString { round(it).toString() }}\n" +
                    "output: ${y.joinToString { round(it).toString() }}")
        }
    }

    private fun round(value: Double) = ceil(value * 10) / 10

}