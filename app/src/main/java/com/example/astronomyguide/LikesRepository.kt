package com.example.astronomyguide.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LikesRepository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val likesDao = database.likesDao()

    suspend fun getLikes(newsId: Int): Int {
        return withContext(Dispatchers.IO) {
            likesDao.getLikes(newsId) ?: 0
        }
    }

    suspend fun saveLikes(newsId: Int, likes: Int) {
        withContext(Dispatchers.IO) {
            likesDao.saveLikes(LikesEntity(newsId, likes))
        }
    }
}