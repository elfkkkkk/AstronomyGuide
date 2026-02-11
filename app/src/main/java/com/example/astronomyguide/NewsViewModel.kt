package com.example.astronomyguide

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.astronomyguide.data.LikesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val allNews = NewsData.newsList
    val displayedNews = mutableStateListOf<NewsItem>()
    val isNewsUpdateEnabled = mutableStateOf(true)

    private val repository = LikesRepository(application)

    init {
        viewModelScope.launch {
            initializeDisplayedNews()
            startNewsRotation()
        }
    }

    private suspend fun initializeDisplayedNews() {
        displayedNews.clear()
        repeat(4) {
            addRandomNewsToDisplay()
        }
    }

    private suspend fun addRandomNewsToDisplay() {
        val randomNews = getUniqueRandomNews()
        // получаем лайки из базы данных
        val likes = repository.getLikes(randomNews.id)
        displayedNews.add(randomNews.copy(likes = likes))
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

    private suspend fun replaceRandomNews() {
        val randomIndex = (0 until 4).random()
        val currentNews = displayedNews[randomIndex]

        // сохраняем лайки текущей новости
        if (currentNews.likes > 0) {
            repository.saveLikes(currentNews.id, currentNews.likes)
        }

        // получаем новую новость
        val newNews = getUniqueRandomNews(displayedNews.map { it.id })

        // загружаем лайки из БД
        val savedLikes = repository.getLikes(newNews.id)

        // обновляем отображение
        displayedNews[randomIndex] = newNews.copy(likes = savedLikes)
    }

    fun likeNews(index: Int) {
        viewModelScope.launch {
            if (index in 0 until displayedNews.size) {
                val news = displayedNews[index]
                val newLikes = news.likes + 1

                // сохраняем в БД
                repository.saveLikes(news.id, newLikes)

                // обновляем на экране
                displayedNews[index] = news.copy(likes = newLikes)
            }
        }
    }

    fun toggleNewsUpdate() {
        isNewsUpdateEnabled.value = !isNewsUpdateEnabled.value
    }
}