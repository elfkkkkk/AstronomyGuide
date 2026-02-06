package com.example.astronomyguide

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val allNews = NewsData.newsList
    val displayedNews = mutableStateListOf<NewsItem>()
    val isNewsUpdateEnabled = mutableStateOf(true)

    init {
        updateDisplayedNews()
        startNewsRotation()
    }

    private fun updateDisplayedNews() {
        displayedNews.clear()
        repeat(4) {
            val randomNews = getUniqueRandomNews()
            // Загружаем сохраненные лайки
            val savedLikes = NewsState.getLikes(randomNews.id)
            displayedNews.add(randomNews.copy(likes = savedLikes))
        }
    }

    private fun getUniqueRandomNews(excludeIds: List<Int> = displayedNews.map { it.id }): NewsItem {
        val availableNews = allNews.filter { it.id !in excludeIds }
        return if (availableNews.isEmpty()) allNews.random() else availableNews.random()
    }

    private fun startNewsRotation() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                if (isNewsUpdateEnabled.value && displayedNews.isNotEmpty()) {
                    replaceRandomNews()
                }
            }
        }
    }

    private fun replaceRandomNews() {
        val randomIndex = (0 until 4).random()
        val currentNews = displayedNews[randomIndex]

        // Сохраняем текущие лайки перед заменой
        NewsState.setLikes(currentNews.id, currentNews.likes)

        // Получаем новую новость
        val newNews = getUniqueRandomNews(displayedNews.map { it.id })

        // Загружаем сохраненные лайки для новой новости
        val savedLikes = NewsState.getLikes(newNews.id)

        // Обновляем отображение
        displayedNews[randomIndex] = newNews.copy(likes = savedLikes)
    }

    fun likeNews(index: Int) {
        if (index in 0 until displayedNews.size) {
            val news = displayedNews[index]
            val newLikes = news.likes + 1

            // Обновляем в отображении
            displayedNews[index] = news.copy(likes = newLikes)

            // Сохраняем в хранилище
            NewsState.setLikes(news.id, newLikes)
        }
    }

    fun toggleNewsUpdate() {
        isNewsUpdateEnabled.value = !isNewsUpdateEnabled.value
    }
}