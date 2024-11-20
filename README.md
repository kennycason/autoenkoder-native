# Autoenkoder

Autoencoder in Kotlin Native

Supports:
- Autoencoder trained via error back-propagation
- Deep Learning via Stacked Autoencoders
- Bitmap IO (read/write/grayscale transform)
- Basic matrix library 

This project is a fun/learning project and exploration of Kotlin Native. 
There are no plans to use more performant matrix libraries or utilize GPU. 

## Learn Pokéball Images

| <img src="https://raw.githubusercontent.com/kennycason/autoenkoder-native/refs/heads/main/images/pokeball.bmp" width="64px" /> | <img src="https://raw.githubusercontent.com/kennycason/autoenkoder-native/refs/heads/main/images/pokeball_grayscale_learned.bmp" width="64px" /> | <img src="https://raw.githubusercontent.com/kennycason/autoenkoder-native/refs/heads/main/images/pokeball_color_learned.bmp" width="64px" /> | <img src="https://raw.githubusercontent.com/kennycason/autoenkoder-native/refs/heads/main/images/pokeball_color_deep_learned.bmp" width="64px" /> |
|--------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| Original Image                                                                                                                 | Learned Grayscale Image                                                                                                                          | Learned Color Image                                                                                                                          | Deep Learning of Color Image                                                                                                                      |

## Learn NXOR / Parity Check

```kotlin
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
```

Output

```
epoch 0, error: 0.8543126789285606
...
epoch 9900, error: 0.013029161424338374
trained in 265ms
input: 0.0, 0.0, 1.0, reconstructed: 7.689938580572574E-7, 0.05951891258592248, 0.9381659206219269
input: 0.0, 1.0, 0.0, reconstructed: 0.08873456995039496, 0.9062663091018858, 0.09928074353065161
input: 1.0, 0.0, 0.0, reconstructed: 0.9379659435554321, 0.07327247025541789, 2.4847349308061413E-5
input: 1.0, 1.0, 1.0, reconstructed: 0.936292022686433, 0.9999946754167286, 0.9346902098542325
```

## Deep Learning Learn Random 25 Dimensional Vectors

```kotlin
// generate 5 random 25 dimensional vectors
val trainingData = Array(5) {
    DoubleArray(25) { Random.nextDouble(0.0, 1.0) }
}
// create a two-layer stacked autoencoder
val inputSize = trainingData.first().size
val config = StackedAutoenkoderConfig(
    layers = listOf(
        LayerConfig(inputSize = inputSize, outputSize = inputSize / 2),
        LayerConfig(inputSize = inputSize / 2, outputSize =  inputSize / 4)
    )
)
val stackedAutoenkoder = StackedAutoenkoder(config)
stackedAutoenkoder.train(trainingData, learningRate = 0.075, epochs = 10_000)

// print results for visual inspection
trainingData.forEach { x ->
    val y = stackedAutoenkoder.predict(x)
    println("input:  ${x.joinToString { round(it).toString() }}\n" +
            "output: ${y.joinToString { round(it).toString() }}")
}
```

Output

```
epoch 0, error: 0.354354799459162E
...
epoch 9900, error: 1.8956266828221909E-4
trained in 4037ms
error: 4.002042550137359E-4
input:  0.6, 0.8, 0.6, 0.8, 0.5, 0.3, 0.4, 1.0, 0.6, 0.1, 0.9, 0.4, 1.0, 0.1, 1.0, 0.5, 0.4, 0.1, 0.6, 0.2, 0.8, 0.3, 0.4, 0.3, 0.6
output: 0.6, 0.8, 0.6, 0.8, 0.5, 0.3, 0.4, 1.0, 0.6, 0.1, 0.9, 0.4, 1.0, 0.1, 1.0, 0.5, 0.4, 0.1, 0.6, 0.2, 0.8, 0.3, 0.4, 0.3, 0.6
input:  0.1, 0.5, 0.4, 0.8, 1.0, 0.3, 0.4, 0.6, 0.7, 0.4, 0.8, 0.5, 0.2, 0.4, 1.0, 1.0, 0.4, 0.8, 0.5, 1.0, 0.4, 0.8, 0.5, 0.7, 0.8
output: 0.1, 0.5, 0.4, 0.8, 1.0, 0.3, 0.4, 0.6, 0.7, 0.4, 0.8, 0.5, 0.2, 0.4, 1.0, 1.0, 0.4, 0.8, 0.5, 1.0, 0.4, 0.8, 0.5, 0.7, 0.8
input:  0.3, 0.3, 0.1, 0.5, 0.4, 0.7, 0.1, 0.2, 0.9, 0.1, 0.8, 0.8, 0.9, 0.7, 0.8, 0.2, 1.0, 0.9, 0.9, 0.3, 0.3, 0.6, 1.0, 0.8, 0.7
output: 0.3, 0.3, 0.1, 0.5, 0.4, 0.7, 0.1, 0.2, 0.9, 0.1, 0.8, 0.8, 0.9, 0.7, 0.8, 0.2, 1.0, 0.9, 0.9, 0.3, 0.3, 0.6, 1.0, 0.8, 0.7
input:  1.0, 0.2, 0.4, 0.5, 0.8, 0.2, 0.5, 0.3, 1.0, 0.5, 0.8, 0.3, 0.5, 0.9, 0.3, 0.3, 1.0, 0.1, 0.3, 0.5, 0.8, 0.1, 1.0, 0.4, 0.1
output: 1.0, 0.3, 0.4, 0.5, 0.8, 0.2, 0.5, 0.3, 1.0, 0.5, 0.8, 0.3, 0.5, 0.9, 0.3, 0.3, 1.0, 0.1, 0.3, 0.5, 0.8, 0.1, 1.0, 0.4, 0.2
input:  0.5, 0.5, 0.6, 0.7, 0.2, 0.3, 0.1, 0.8, 0.9, 0.6, 0.2, 0.5, 0.5, 0.5, 0.4, 0.1, 0.2, 0.2, 0.6, 0.3, 0.9, 1.0, 0.5, 0.8, 0.7
output: 0.5, 0.5, 0.6, 0.7, 0.2, 0.3, 0.1, 0.8, 0.9, 0.5, 0.2, 0.5, 0.5, 0.5, 0.4, 0.1, 0.2, 0.2, 0.6, 0.3, 0.9, 1.0, 0.5, 0.8, 0.7
```

## Learn Color Bitmap Image

```kotlin
val filePath = "./images/pokeball.bmp"
val bitmap = BitmapIO.read(filePath)
val trainingData = arrayOf(BitmapPixelDataTransforms.toAutoenkoderInput(bitmap))
val autoEncoder = Autoenkoder(inputSize = trainingData.first().size, hiddenSize = 10)
autoEncoder.train(trainingData, learningRate = 0.1, epochs = 10_000)
// generate and save bmp file of autoencoder's rendering 
val x = trainingData.first()
val y = autoEncoder.predict(x)
BitmapIO.write(
    filePath = "./output/pokeball_color_learned.bmp",
    bitmap = Bitmap(
        header = bitmap.header,
        data = BitmapPixelDataTransforms.toBitMapData(bitmap.header.pixelFormat, y)
    )
)
```

## Learn Grayscale Bitmap Image

```kotlin
val filePath = "./images/pokeball.bmp"
val bitmap = BitmapIO.read(filePath)
val grayscaleBitmap = BitmapIO.toGrayscale(bitmap)
val trainingData = arrayOf(
    BitmapByteNormalizer.normalize(grayscaleBitmap.data)
)
val autoEncoder = Autoenkoder(inputSize = trainingData.first().size, hiddenSize = 10)
autoEncoder.train(trainingData, learningRate = 0.1, epochs = 10_000)
        val x = trainingData.first()
        val y = autoEncoder.predict(x)
        BitmapIO.write(
            filePath = "./output/pokeball_color_learned.bmp",
            bitmap = Bitmap(
                header = bitmap.header,
                data = BitmapPixelDataTransforms.toBitMapData(bitmap.header.pixelFormat, y)
            )
        )
val x = trainingData.first()
val y = autoEncoder.predict(x)
BitmapIO.write(
    filePath = "./output/pokeball_grayscale_learned.bmp",
    bitmap = Bitmap(
        header = grayscaleBitmap.header,
        data = y.map { (it * 255.0).toUInt() }.toUIntArray()
    )
)
```

