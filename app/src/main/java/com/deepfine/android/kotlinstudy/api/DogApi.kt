package com.deepfine.android.kotlinstudy.api

import com.deepfine.android.kotlinstudy.dto.DogDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DogApi {

  //id로 api data 검색 후 결과 get
  @GET("v1/breeds/{UserID}")
  fun getSearchData(
    @Header("Authorization") apiKey: String,
    @Path("UserID") userId: String //UserID에 들어가는 값 받아오기
  ): Call<DogDto>

  //limit만큼 api data get
  @GET("v1/breeds")
  fun getApiData(
    @Header("Authorization") apiKey: String,
    @Query("limit") limit: String
  ): Call<ArrayList<DogDto>>

}