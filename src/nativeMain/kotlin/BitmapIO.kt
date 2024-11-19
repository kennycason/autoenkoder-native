import kotlinx.cinterop.*
import platform.posix.*


data class Bitmap(
    val header: Header,
    val data: UIntArray
) {
    override fun toString(): String {
        return "Bitmap(header=$header, data=${data.size})"
    }
}

data class Header(
    val width: Int,
    val height: Int,
    val pixelFormat: PixelFormat
)

enum class PixelFormat {
    RGB, RGBA, GRAYSCALE
}

object BitmapIO {
    @OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)
    fun read(filePath: String): Bitmap {
        memScoped {
            val filePtr = fopen(filePath, "rb") ?: throw IllegalArgumentException("Unable to open file: $filePath")

            // read the file header (14 bytes for BITMAPFILEHEADER in BMP format)
            val fileHeader = ByteArray(14)
            fread(fileHeader.refTo(0), 1u, 14u, filePtr)

            // verify this is a BMP file by checking the magic number (BM -> 0x4D42)
            if (fileHeader[0] != 'B'.code.toByte() || fileHeader[1] != 'M'.code.toByte()) {
                fclose(filePtr)
                throw IllegalArgumentException("Not a valid BMP file")
            }

            // read the info header (40 bytes for BITMAPINFOHEADER in BMP format)
            val infoHeader = ByteArray(40)
            fread(infoHeader.refTo(0), 1u, 40u, filePtr)

            // extract width, height, and bit-depth
            val width = infoHeader[4].toInt() or (infoHeader[5].toInt() shl 8)
            val height = infoHeader[8].toInt() or (infoHeader[9].toInt() shl 8)
            val bitDepth = infoHeader[14].toUByte().toInt()

            // determine the pixel format
            val pixelFormat = when (bitDepth) {
                8 -> PixelFormat.GRAYSCALE
                24 -> PixelFormat.RGB
                32 -> PixelFormat.RGBA
                else -> throw UnsupportedOperationException("Unsupported bit depth: $bitDepth")
            }

            // calculate row size (including padding) and data size
            val rowSize = ((width * bitDepth + 31) / 32) * 4
            val dataSize = rowSize * height

            // move the file pointer to the start of pixel data
            val pixelDataOffset = fileHeader[10].toInt() or (fileHeader[11].toInt() shl 8)
//            println("read pixel data offset: $pixelDataOffset")
            fseek(filePtr, pixelDataOffset.toLong(), SEEK_SET)

            // read pixel data
            val rawData = ByteArray(dataSize)
            fread(rawData.refTo(0), 1u, dataSize.toULong(), filePtr)
            fclose(filePtr)

            // parse pixel data into UIntArray (ARGB format)
            val data = UIntArray(width * height)

            // TODO handle color palettes
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val baseIndex = y * rowSize + x * (bitDepth / 8)
                    if (pixelFormat == PixelFormat.GRAYSCALE) {
                        val gray = rawData[baseIndex].toUByte().toUInt()
                        data[y * width + x] = (255u shl 24) or (gray shl 16) or (gray shl 8) or gray
//                        println("read ${gray.toHexString()}")
                    } else {
                        val b = rawData[baseIndex].toUByte().toUInt()
                        val g = rawData[baseIndex + 1].toUByte().toUInt()
                        val r = rawData[baseIndex + 2].toUByte().toUInt()
                        val a = if (bitDepth == 32) rawData[baseIndex + 3].toUByte().toUInt() else 255u
                        data[y * width + x] = (a shl 24) or (r shl 16) or (g shl 8) or b
                    }
                }
            }

            return Bitmap(
                header = Header(
                    width = width,
                    height = height,
                    pixelFormat = pixelFormat
                ),
                data = data
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class)
    fun write(filePath: String, bitmap: Bitmap) {
        memScoped {
            val filePtr = fopen(filePath, "wb") ?: throw IllegalArgumentException("Unable to open file: $filePath")

            val width = bitmap.header.width
            val height = bitmap.header.height
            val pixelFormat = bitmap.header.pixelFormat
            val bitDepth = when (pixelFormat) {
                PixelFormat.GRAYSCALE -> 8
                PixelFormat.RGB -> 24
                PixelFormat.RGBA -> 32
            }

            val colorPaletteSize = if (pixelFormat == PixelFormat.GRAYSCALE) 256 * 4 else 0
            val rowSize = ((width * bitDepth + 31) / 32) * 4
            val dataSize = rowSize * height
            val fileSize = 14 + 40 + colorPaletteSize + dataSize
            val pixelDataOffset = 14 + 40 + colorPaletteSize
//            println("write pixel data offset: $pixelDataOffset")

            // write BMP file header
            val fileHeader = ByteArray(14).apply {
                this[0] = 'B'.code.toByte()
                this[1] = 'M'.code.toByte()
                this[2] = (fileSize and 0xFF).toByte()
                this[3] = ((fileSize shr 8) and 0xFF).toByte()
                this[4] = ((fileSize shr 16) and 0xFF).toByte()
                this[5] = ((fileSize shr 24) and 0xFF).toByte()
                this[10] = (pixelDataOffset and 0xFF).toByte()
                this[11] = ((pixelDataOffset shr 8) and 0xFF).toByte()
                this[12] = ((pixelDataOffset shr 16) and 0xFF).toByte()
                this[13] = ((pixelDataOffset shr 24) and 0xFF).toByte()
            }
            fwrite(fileHeader.refTo(0), 1u, fileHeader.size.toULong(), filePtr)

            // write BMP info header
            val infoHeader = ByteArray(40).apply {
                this[0] = 40 // header size
                this[4] = (width and 0xFF).toByte()
                this[5] = ((width shr 8) and 0xFF).toByte()
                this[8] = (height and 0xFF).toByte()
                this[9] = ((height shr 8) and 0xFF).toByte()
                this[12] = 1 // number of planes
                this[14] = bitDepth.toByte() // bits per pixel
            }
            fwrite(infoHeader.refTo(0), 1u, infoHeader.size.toULong(), filePtr)

            // write grayscale color palette (if applicable)
            if (pixelFormat == PixelFormat.GRAYSCALE) {
                val colorPalette = ByteArray(256 * 4)
                for (i in 0..255) {
                    colorPalette[i * 4] = i.toByte()     // blue
                    colorPalette[i * 4 + 1] = i.toByte() // green
                    colorPalette[i * 4 + 2] = i.toByte() // red
                    colorPalette[i * 4 + 3] = 0          // reserved
                }
                fwrite(colorPalette.refTo(0), 1u, colorPalette.size.toULong(), filePtr)
            }

            // write pixel data
            val rowPadding = rowSize - (width * (bitDepth / 8))
            val rawData = ByteArray(dataSize)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixelIndex = y * width + x
                    val pixel = bitmap.data[pixelIndex]
                    val baseIndex = y * rowSize + x * (bitDepth / 8)
                    when (pixelFormat) {
                        PixelFormat.GRAYSCALE -> {
                            val gray = (pixel and 0xFFu).toByte()
                            rawData[baseIndex] = gray
//                            println("write ${gray.toHexString()}")
                        }
                        PixelFormat.RGB -> {
                            val r = ((pixel shr 16) and 0xFFu).toByte()
                            val g = ((pixel shr 8) and 0xFFu).toByte()
                            val b = (pixel and 0xFFu).toByte()
                            rawData[baseIndex] = b
                            rawData[baseIndex + 1] = g
                            rawData[baseIndex + 2] = r
                        }
                        PixelFormat.RGBA -> {
                            val r = ((pixel shr 16) and 0xFFu).toByte()
                            val g = ((pixel shr 8) and 0xFFu).toByte()
                            val b = (pixel and 0xFFu).toByte()
                            val a = ((pixel shr 24) and 0xFFu).toByte()
                            rawData[baseIndex] = b
                            rawData[baseIndex + 1] = g
                            rawData[baseIndex + 2] = r
                            rawData[baseIndex + 3] = a
                        }
                    }
                }
                if (rowPadding > 0) {
                    val paddingIndex = y * rowSize + width * (bitDepth / 8)
                    for (p in 0 until rowPadding) {
                        rawData[paddingIndex + p] = 0 // Padding bytes
                    }
                }
            }
            fwrite(rawData.refTo(0), 1u, rawData.size.toULong(), filePtr)
            fclose(filePtr)
        }
    }

    fun toGrayscale(bitmap: Bitmap): Bitmap {
        val grayscaleData = UIntArray(bitmap.data.size)

        for (i in bitmap.data.indices) {
            val pixel = bitmap.data[i]
            val r = (pixel shr 16 and 0xFFu).toInt()
            val g = (pixel shr 8 and 0xFFu).toInt()
            val b = (pixel and 0xFFu).toInt()
            val gray = (0.3 * r + 0.59 * g + 0.11 * b).toInt().coerceIn(0, 255)
            grayscaleData[i] = (255u shl 24) or (gray.toUInt() shl 16) or (gray.toUInt() shl 8) or gray.toUInt() // store the grayscale value as ARGB with the same alpha
//            println("$i > $gray > ${grayscaleData[i].toHexString()}")
        }
        return Bitmap(
            header = bitmap.header.copy(pixelFormat = PixelFormat.GRAYSCALE),
            data = grayscaleData
        )
    }

}