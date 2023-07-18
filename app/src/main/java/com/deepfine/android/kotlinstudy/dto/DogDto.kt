package com.deepfine.android.kotlinstudy.dto

import com.deepfine.android.kotlinstudy.adapter.HomeAdapter
import com.google.gson.annotations.SerializedName

data class DogDto(
  val id: Int,
  @SerializedName("bred_for")
  val bredFor: String?,
  val name: String,
  var bookmarkCheck: Boolean,
  var image: DogImage? = null,
  var weight: DogWeight? = null,
  var height: DogHeight? = null,
  var temperament: String? = null,
  @SerializedName("life_span")
  var lifeSpan: String? = null,
  var bookmarkImg: Int? = null,
  var index: Int? = null,
  val viewType: HomeAdapter.ViewType
) {
  companion object {
    fun createLoadingData(): DogDto {
      return DogDto(
        0,
        null,
        "",
        false,
        viewType = HomeAdapter.ViewType.LOADING
      )
    }
  }
}