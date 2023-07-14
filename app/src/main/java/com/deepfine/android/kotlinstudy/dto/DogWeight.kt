package com.deepfine.android.kotlinstudy.dto

data class DogWeight(
  var metric: String,
  var imperial: String
) {
  fun getWeightAvg(): Double {
    var range = metric.split("-")
    return range[0].trim().toDouble() + range[1].trim().toDouble() / 2
  }
}