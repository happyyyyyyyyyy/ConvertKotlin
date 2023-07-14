package com.deepfine.android.kotlinstudy.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DogData")
data class DogData(
  @PrimaryKey
  var id: Int,
  var name: String,
  var bredFor: String?,
  var img: String,
  var bookmarkCheck: Boolean
)