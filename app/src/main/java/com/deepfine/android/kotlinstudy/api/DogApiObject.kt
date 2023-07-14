package com.deepfine.android.kotlinstudy.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DogApiObject {
  private const val BASE_URL = "https://api.thedogapi.com/"

  private val getRetrofit by lazy {
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  val getRetrofitService: DogApi by lazy { getRetrofit.create(DogApi::class.java) }
}