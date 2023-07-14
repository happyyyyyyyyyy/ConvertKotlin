package com.deepfine.android.kotlinstudy.dto

import com.google.gson.annotations.SerializedName

data class DogDto(
  var image: DogImage,
  var id: Int,
  var name: String,
  @SerializedName("bred_for")
  var bredFor: String?,
  var weight: DogWeight,
  var height: DogHeight,
  var temperament: String?,
  @SerializedName("life_span")
  var lifeSpan: String?,
  var bookmarkCheck: Boolean,
  var bookmarkImg: Int,
  var index: Int
)