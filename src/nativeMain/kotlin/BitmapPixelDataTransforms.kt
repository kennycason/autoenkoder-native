/**
 * each value of data is an RGBA integer and needs to be split and flattened
 * to each corresponding R,G,B, and A components.
 *
 * Not doing so will result in blue-tinted images and general color issues resulting from trying to
 * learn full 32-bit numbers per NN input.
  */
object BitmapPixelDataTransforms {

    fun toAutoenkoderInput(bitmap: Bitmap): DoubleArray {
        val bytesPerPixel = when (bitmap.header.pixelFormat) {
            PixelFormat.RGB -> 3
            PixelFormat.RGBA -> 4
            PixelFormat.GRAYSCALE -> 1
        }
        val transformed = DoubleArray(bitmap.data.size * bytesPerPixel)
        when (bitmap.header.pixelFormat) {
            PixelFormat.RGB -> {
                bitmap.data.mapIndexed { i, pixel ->
                    val r = ((pixel shr 16) and 0xFFu)
                    val g = ((pixel shr 8) and 0xFFu)
                    val b = (pixel and 0xFFu)
                    transformed[bytesPerPixel * i] = r.toDouble() / 255.0
                    transformed[bytesPerPixel * i + 1] = g.toDouble() / 255.0
                    transformed[bytesPerPixel * i + 2] = b.toDouble() / 255.0
                }
            }
            PixelFormat.RGBA -> {
                bitmap.data.mapIndexed { i, pixel ->
                    val r = ((pixel shr 16) and 0xFFu)
                    val g = ((pixel shr 8) and 0xFFu)
                    val b = (pixel and 0xFFu)
                    val a = ((pixel shr 24) and 0xFFu)
                    transformed[bytesPerPixel * i] = r.toDouble() / 255.0
                    transformed[bytesPerPixel * i + 1] = g.toDouble() / 255.0
                    transformed[bytesPerPixel * i + 2] = b.toDouble() / 255.0
                    transformed[bytesPerPixel * i + 3] = a.toDouble() / 255.0
                }
            }
            PixelFormat.GRAYSCALE -> {
                bitmap.data.mapIndexed { i, pixel ->
                    val g = (pixel and 0xFFu)
                    transformed[i] = g.toDouble() / 255.0
                }
            }
        }
        return transformed
    }

    fun toBitMapData(pixelFormat: PixelFormat, data: DoubleArray): UIntArray {
        val bytesPerPixel = when (pixelFormat) {
            PixelFormat.RGB -> 3
            PixelFormat.RGBA -> 4
            PixelFormat.GRAYSCALE -> 1
        }

        require(data.size % bytesPerPixel == 0) { "data size must be a multiple of bytes per pixel" }
        val pixelCount = data.size / bytesPerPixel
        val transformed = UIntArray(pixelCount)

        for (i in 0 until pixelCount) {
            val baseIndex = i * bytesPerPixel
            val r = (data[baseIndex] * 255).toUInt().coerceIn(0u, 255u)
            val g = if (bytesPerPixel > 1) (data[baseIndex + 1] * 255).toUInt().coerceIn(0u, 255u) else 0u
            val b = if (bytesPerPixel > 2) (data[baseIndex + 2] * 255).toUInt().coerceIn(0u, 255u) else 0u
            val a = if (bytesPerPixel == 4) (data[baseIndex + 3] * 255).toUInt().coerceIn(0u, 255u) else 255u

            transformed[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        return transformed
    }

}