import kotlin.test.Test

class BitmapTest {

    @Test
    fun `read bmp - color`() {
        val filePath = "./images/pokeball.bmp"
        val bmpData = Bitmap.readBitmap(filePath)
        println(bmpData.size)
    }

    @Test
    fun `read bmp - grayscale`() {
        val filePath = "./images/pokeball.bmp"
        val bmpData = Bitmap.readBitmapAsGrayScale(filePath)
        println(bmpData.size)
    }

}