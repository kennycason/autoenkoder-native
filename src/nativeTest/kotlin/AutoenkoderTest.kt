import kotlin.math.ceil
import kotlin.random.Random
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
        println("error ${autoEncoder.calculateError(trainingData)}")

        trainingData.forEach { x ->
            println("input: ${x.joinToString()}, reconstructed: ${autoEncoder.predict(x).joinToString()}")
        }
        println("rounded")
        trainingData.forEach { x ->
            println("input: ${x.joinToString()}, reconstructed: ${autoEncoder.predict(x).joinToString { round(it).toString() }}")
        }
    }

    @Test
    fun `autoencoder - random vectors`() {
        val autoEncoder = Autoenkoder(inputSize = 10, hiddenSize = 3)
        val trainingData = Array(4) {
            DoubleArray(10) { Random.nextDouble(0.0, 1.0) }
        }
        val elapsedMs = measureTime {
            autoEncoder.train(trainingData, learningRate = 0.2, epochs = 10_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        println("error ${autoEncoder.calculateError(trainingData)}")

        trainingData.forEach { x ->
            println("input: ${x.joinToString()}, reconstructed: ${autoEncoder.predict(x).joinToString()}")
        }
        println("rounded")
        trainingData.forEach { x ->
            println("input: ${x.joinToString { round(it).toString() }}, reconstructed: ${autoEncoder.predict(x).joinToString { round(it).toString() }}")
        }
    }

    @Test
    fun `autoencoder - tiny grayscale square image`() {
        val filePath = "./images/gray_square.bmp"
        val bitmap = BitmapIO.read(filePath)
        val trainingData = arrayOf(BitmapByteNormalizer.normalize(bitmap.data))
        trainingData.first().forEach {
            println(it)
        }

        val autoEncoder = Autoenkoder(inputSize = trainingData.first().size, hiddenSize = 1)
        val elapsedMs = measureTime {
            autoEncoder.train(trainingData, learningRate = 0.1, epochs = 10_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        println("error ${autoEncoder.calculateError(trainingData)}")

        val x = trainingData.first()
        val y = autoEncoder.predict(x)
        BitmapIO.write(
            filePath = "./output/gray_square_learned.bmp",
            bitmap = Bitmap(
                header = bitmap.header,
                data = y.map { (it * 255.0).toUInt() }.toUIntArray()
            )
        )
    }

    @Test
    fun `autoencoder - pokeball grayscale`() {
        val filePath = "./images/pokeball.bmp"
        val bitmap = BitmapIO.read(filePath)
        val grayscaleBitmap = BitmapIO.toGrayscale(bitmap)
        val trainingData = arrayOf(
            BitmapByteNormalizer.normalize(grayscaleBitmap.data)
        )

        val autoEncoder = Autoenkoder(inputSize = trainingData.first().size, hiddenSize = 10)
        val elapsedMs = measureTime {
            autoEncoder.train(trainingData, learningRate = 0.1, epochs = 20_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        println("error ${autoEncoder.calculateError(trainingData)}")

        val x = trainingData.first()
        val y = autoEncoder.predict(x)
        BitmapIO.write(
            filePath = "./output/pokeball_grayscale_learned.bmp",
            bitmap = Bitmap(
                header = grayscaleBitmap.header,
                data = y.map { (it * 255.0).toUInt() }.toUIntArray()
            )
        )
    }

    @Test
    fun `autoencoder - pokeball color`() {
        val filePath = "./images/pokeball.bmp"
        val bitmap = BitmapIO.read(filePath)
        val trainingData = arrayOf(BitmapPixelDataTransforms.toAutoenkoderInput(bitmap))

        val autoEncoder = Autoenkoder(inputSize = trainingData.first().size, hiddenSize = 10)
        val elapsedMs = measureTime {
            autoEncoder.train(trainingData, learningRate = 0.05, epochs = 20_000)
        }.inWholeMilliseconds
        println("trained in ${elapsedMs}ms")
        println("error ${autoEncoder.calculateError(trainingData)}")

        val x = trainingData.first()
        val y = autoEncoder.predict(x)
        BitmapIO.write(
            filePath = "./output/pokeball_color_learned.bmp",
            bitmap = Bitmap(
                header = bitmap.header,
                data = BitmapPixelDataTransforms.toBitMapData(bitmap.header.pixelFormat, y)
            )
        )
    }

    private fun round(value: Double) = ceil(value * 10) / 10

}