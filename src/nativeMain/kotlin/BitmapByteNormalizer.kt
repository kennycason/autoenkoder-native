object BitmapByteNormalizer {
    fun normalize(data: UIntArray): DoubleArray {
        return data.map {
            val normalized = ((it and 0xFFu).toDouble() / 255.0).coerceIn(0.0, 1.0)
//            println("raw $it -> $normalized")
            normalized
        }.toDoubleArray()
    }
}