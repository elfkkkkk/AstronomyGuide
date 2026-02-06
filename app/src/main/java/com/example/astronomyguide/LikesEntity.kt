package com.example.astronomyguide.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likes")
data class LikesEntity(
    @PrimaryKey
    val newsId: Int,
    val likesCount: Int
)