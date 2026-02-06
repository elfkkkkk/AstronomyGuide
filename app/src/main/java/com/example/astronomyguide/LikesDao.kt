package com.example.astronomyguide.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LikesDao {

    @Query("SELECT likesCount FROM likes WHERE newsId = :newsId")
    suspend fun getLikes(newsId: Int): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLikes(likes: LikesEntity)

    @Query("UPDATE likes SET likesCount = :count WHERE newsId = :newsId")
    suspend fun updateLikes(newsId: Int, count: Int)
}