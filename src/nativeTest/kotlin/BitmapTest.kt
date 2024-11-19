import kotlin.test.Test
import kotlin.test.assertEquals

class BitmapTest {

    @Test
    fun `read bmp - color`() {
        val filePath = "./images/pokeball.bmp"
        val bitmap = BitmapIO.read(filePath)
        println(bitmap)
    }

    @Test
    fun `read write bmp - color`() {
        val filePath = "./images/pokeball.bmp"
        val bitmap = BitmapIO.read(filePath)
        val testBitmapFilePath = "./output/pokeball_color_write_test.bmp"
        BitmapIO.write(testBitmapFilePath, bitmap)
        println(bitmap)

        val readColorBitmap = BitmapIO.read(testBitmapFilePath)
        println("previously written bitmap: $readColorBitmap")

        for (i in 0 until bitmap.data.size) {
            assertEquals(bitmap.data[i], readColorBitmap.data[i])
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `read write read bmp - grayscale`() {
        val colorFilePath = "./images/pokeball.bmp"
        val grayscaleFilePath = "./output/pokeball_grayscale_write_test.bmp"
        val colorBitmap = BitmapIO.read(colorFilePath)

        val grayscaleBitmap = BitmapIO.toGrayscale(colorBitmap)
        BitmapIO.write(grayscaleFilePath, grayscaleBitmap)
        println("color: $colorBitmap")
        println("grayscale to write: $grayscaleBitmap")

        val readGrayscaleBitmap = BitmapIO.read(grayscaleFilePath)
        println("grayscale read: $readGrayscaleBitmap")

        for (i in 0 until grayscaleBitmap.data.size) {
            println("${grayscaleBitmap.data[i].toHexString()} - ${readGrayscaleBitmap.data[i].toHexString()}")
            assertEquals(grayscaleBitmap.data[i], readGrayscaleBitmap.data[i])
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `read write read bmp - grayscale square`() {
        val graySquareInputFilePath = "./images/gray_square.bmp"
        val graySquareOutputFilePath = "./output/gray_square.bmp"

        val graySquareInput = BitmapIO.read(graySquareInputFilePath)
        BitmapIO.write(graySquareOutputFilePath, graySquareInput)
        val graySquareReadAgain = BitmapIO.read(graySquareOutputFilePath)

        for (i in 0 until graySquareInput.data.size) {
            println("${graySquareInput.data[i].toHexString()} - ${graySquareReadAgain.data[i].toHexString()}")
            assertEquals(graySquareInput.data[i], graySquareReadAgain.data[i])
        }
    }

}