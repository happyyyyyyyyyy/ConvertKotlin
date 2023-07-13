package com.deepfine.android.kotlinstudy.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DogDao {
    @Insert
    fun insert(dogData: DogData)

    @Update
    fun update(dogData: DogData)

    @Delete
    fun delete(dogData: DogData)

    @Query("SELECT * FROM DogData")
    fun getAll(): MutableList<DogData>

    @Query("SELECT * FROM DogData LIMIT :pageSize OFFSET :offset")
    fun getItemsByPage(pageSize: Int, offset: Int): MutableList<DogData>

//    @Query("SELECT * FROM DogData WHERE bookmarkCheck = 1")
//    fun getBookmarkAll(): List<BookmarkDto?>?

    @Query("DELETE FROM DogData")
    fun deleteAll()

    @Query(
        "SELECT * FROM DogData " +
                "WHERE name Like :searchQuery " +
                "ORDER BY CASE WHEN name = :exactMatch THEN 1 ELSE 2 END, name"
    )
    fun search(searchQuery: String, exactMatch: String): MutableList<DogData>

    @Query("SELECT bookmarkCheck FROM DogData WHERE id = :id")
    fun checkData(id: Int): Boolean

    @Query("SELECT id FROM DogData WHERE id = :id")
    fun checkData2(id: Int): Int

    @Query("UPDATE DogData SET bookmarkCheck = :bookmarkCheck WHERE id = :id")
    fun updateBookmarkCheck(bookmarkCheck: Boolean, id: Int)

}