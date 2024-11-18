import kotlinx.cinterop.*
import platform.posix.*

object Bitmap {

    @OptIn(ExperimentalForeignApi::class)
    fun readBitmap(filePath: String): DoubleArray {
        val file = fopen(filePath, "rb") ?: throw IllegalArgumentException("Unable to open file: $filePath")
        memScoped {
            val header = ByteArray(54)
            fread(header.refTo(0), 1u, 54u, file)

            val width = header[18].toInt() or (header[19].toInt() shl 8)
            val height = header[22].toInt() or (header[23].toInt() shl 8)
            val offset = header[10].toInt() or (header[11].toInt() shl 8)

            fseek(file, offset.toLong(), SEEK_SET)

            val rowSize = ((24 * width + 31) / 32) * 4
            val data = ByteArray(rowSize * height)
            fread(data.refTo(0), 1u, data.size.toULong(), file)

            fclose(file)

            val result = mutableListOf<Double>()
            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val index = y * rowSize + x * 3
                    val b = data[index].toUByte().toDouble()
                    val g = data[index + 1].toUByte().toDouble()
                    val r = data[index + 2].toUByte().toDouble()
                    result.addAll(listOf(r, g, b))
                }
            }
            return result.toDoubleArray()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun readBitmapAsGrayScale(filePath: String): DoubleArray {
        val file = fopen(filePath, "rb") ?: throw IllegalArgumentException("Unable to open file: $filePath")
        memScoped {
            val header = ByteArray(54)
            fread(header.refTo(0), 1u, 54u, file)

            val width = header[18].toInt() or (header[19].toInt() shl 8)
            val height = header[22].toInt() or (header[23].toInt() shl 8)
            val offset = header[10].toInt() or (header[11].toInt() shl 8)

            fseek(file, offset.toLong(), SEEK_SET)

            val rowSize = ((24 * width + 31) / 32) * 4
            val data = ByteArray(rowSize * height)
            fread(data.refTo(0), 1u, data.size.toULong(), file)

            fclose(file)

            val result = mutableListOf<Double>()
            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val index = y * rowSize + x * 3
                    val b = data[index].toUByte().toDouble()
                    val g = data[index + 1].toUByte().toDouble()
                    val r = data[index + 2].toUByte().toDouble()
                    val gray = 0.3 * r + 0.59 * g + 0.11 * b
                    result.add(gray)
                }
            }
            return result.toDoubleArray()
        }
    }


    @OptIn(ExperimentalForeignApi::class)
    fun writeGrayScaleBitmap(filePath: String, width: Int, height: Int, data: DoubleArray) {
        val rowSize = ((8 * width + 31) / 32) * 4
        val padding = rowSize - width
        val fileSize = 54 + rowSize * height

        val header = ByteArray(54).apply {
            this[0] = 'B'.code.toByte()
            this[1] = 'M'.code.toByte()
            this[2] = (fileSize and 0xFF).toByte()
            this[3] = ((fileSize shr 8) and 0xFF).toByte()
            this[4] = ((fileSize shr 16) and 0xFF).toByte()
            this[5] = ((fileSize shr 24) and 0xFF).toByte()
            this[10] = 54
            this[14] = 40
            this[18] = (width and 0xFF).toByte()
            this[19] = ((width shr 8) and 0xFF).toByte()
            this[22] = (height and 0xFF).toByte()
            this[23] = ((height shr 8) and 0xFF).toByte()
            this[26] = 1
            this[28] = 8
        }

        val file = fopen(filePath, "wb") ?: throw IllegalArgumentException("Unable to open file: $filePath")
        memScoped {
            fwrite(header.refTo(0), 1u, 54u, file)

            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val gray = (data[y * width + x].coerceIn(0.0, 255.0)).toInt()
                    fwrite(byteArrayOf(gray.toByte()).refTo(0), 1u, 1u, file)
                }
                if (padding > 0) {
                    fwrite(ByteArray(padding).refTo(0), 1u, padding.toULong(), file)
                }
            }
            fclose(file)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun writeColorBitmap(filePath: String, width: Int, height: Int, data: DoubleArray) {
        val rowSize = ((24 * width + 31) / 32) * 4
        val padding = rowSize - width * 3
        val fileSize = 54 + rowSize * height

        val header = ByteArray(54).apply {
            this[0] = 'B'.code.toByte()
            this[1] = 'M'.code.toByte()
            this[2] = (fileSize and 0xFF).toByte()
            this[3] = ((fileSize shr 8) and 0xFF).toByte()
            this[4] = ((fileSize shr 16) and 0xFF).toByte()
            this[5] = ((fileSize shr 24) and 0xFF).toByte()
            this[10] = 54
            this[14] = 40
            this[18] = (width and 0xFF).toByte()
            this[19] = ((width shr 8) and 0xFF).toByte()
            this[22] = (height and 0xFF).toByte()
            this[23] = ((height shr 8) and 0xFF).toByte()
            this[26] = 1
            this[28] = 24
        }

        val file = fopen(filePath, "wb") ?: throw IllegalArgumentException("Unable to open file: $filePath")
        memScoped {
            fwrite(header.refTo(0), 1u, 54u, file)

            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val index = (y * width + x) * 3
                    val r = (data[index].coerceIn(0.0, 255.0)).toInt()
                    val g = (data[index + 1].coerceIn(0.0, 255.0)).toInt()
                    val b = (data[index + 2].coerceIn(0.0, 255.0)).toInt()
                    fwrite(byteArrayOf(b.toByte(), g.toByte(), r.toByte()).refTo(0), 1u, 3u, file)
                }
                if (padding > 0) {
                    fwrite(ByteArray(padding).refTo(0), 1u, padding.toULong(), file)
                }
            }
            fclose(file)
        }
    }
}