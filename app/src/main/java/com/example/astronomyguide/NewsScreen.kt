package com.example.astronomyguide

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NewsScreen(newsViewModel: NewsViewModel = viewModel()) {
    val displayedNews by remember { derivedStateOf { newsViewModel.displayedNews } }
    val isNewsUpdateEnabled by remember { newsViewModel.isNewsUpdateEnabled }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A1E6D),  // DarkPurple
                        Color(0xFF6B3FA0),  // MediumPurple
                        Color.Black
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Астрономические Новости",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { newsViewModel.toggleNewsUpdate() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF9D71D3))  // LightPurple
            ) {
                Icon(
                    imageVector = if (isNewsUpdateEnabled) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isNewsUpdateEnabled) "Пауза" else "Продолжить",
                    tint = Color.White
                )
            }
        }

        Text(
            text = if (isNewsUpdateEnabled) "Автообновление: ВКЛ" else "Автообновление: ВЫКЛ",
            color = Color(0xFFB39DDB),  // AccentPurple
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 8.dp)
            ) {
                NewsCard(
                    news = displayedNews.getOrNull(0),
                    index = 0,
                    onLikeClick = { newsViewModel.likeNews(0) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                NewsCard(
                    news = displayedNews.getOrNull(1),
                    index = 1,
                    onLikeClick = { newsViewModel.likeNews(1) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                NewsCard(
                    news = displayedNews.getOrNull(2),
                    index = 2,
                    onLikeClick = { newsViewModel.likeNews(2) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                NewsCard(
                    news = displayedNews.getOrNull(3),
                    index = 3,
                    onLikeClick = { newsViewModel.likeNews(3) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NewsCard(
    news: NewsItem?,
    index: Int,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .clickable { onLikeClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B3FA0).copy(alpha = 0.9f)  // MediumPurple
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (news == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF9D71D3))  // LightPurple
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f)
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4A1E6D))  // DarkPurple
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = news.category,
                            color = Color(0xFFB39DDB),  // AccentPurple
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = news.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = news.content,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                        .background(Color(0xFF4A1E6D).copy(alpha = 0.7f))  // DarkPurple
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Лайки",
                            tint = Color(0xFFFF6B6B)
                        )
                        Text(
                            text = "${news.likes}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}