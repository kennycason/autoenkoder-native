# Autoenkoder 

Autoencoder in Kotlin Native

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

Learning Random 10 Dimensional Vectors

```
epoch 0, error: 0.9579067855245543
...
epoch 9900, error: 7.354354799459162E-5
trained in 613ms
input: 0.1, 0.1, 1.0, 0.4, 0.5, 0.5, 1.0, 0.7, 0.8, 0.8, reconstructed: 0.1, 0.1, 1.0, 0.4, 0.5, 0.5, 1.0, 0.7, 0.8, 0.8
input: 1.0, 0.7, 1.0, 0.7, 1.0, 0.3, 0.5, 0.6, 0.4, 0.2, reconstructed: 1.0, 0.7, 1.0, 0.7, 1.0, 0.3, 0.5, 0.6, 0.4, 0.2
input: 0.4, 0.7, 0.6, 0.8, 1.0, 0.5, 0.7, 0.4, 1.0, 0.5, reconstructed: 0.4, 0.7, 0.6, 0.8, 1.0, 0.5, 0.7, 0.4, 1.0, 0.5
input: 1.0, 0.7, 0.5, 0.3, 0.2, 0.3, 0.4, 0.4, 0.2, 0.9, reconstructed: 1.0, 0.7, 0.5, 0.3, 0.2, 0.3, 0.4, 0.4, 0.2, 0.9
```