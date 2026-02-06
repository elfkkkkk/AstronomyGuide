package com.example.astronomyguide

object NewsState {
    private val likesMap = mutableMapOf<Int, Int>()

    fun getLikes(newsId: Int): Int {
        return likesMap[newsId] ?: 0
    }

    fun addLike(newsId: Int) {
        val currentLikes = likesMap[newsId] ?: 0
        likesMap[newsId] = currentLikes + 1
    }

    fun setLikes(newsId: Int, likes: Int) {
        likesMap[newsId] = likes
    }
}