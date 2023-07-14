package com.deepfine.android.kotlinstudy.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  version = 1,
  entities = [DogData::class]
)
abstract class DogDataDatabase : RoomDatabase() {
  abstract fun getDogDao(): DogDao
}