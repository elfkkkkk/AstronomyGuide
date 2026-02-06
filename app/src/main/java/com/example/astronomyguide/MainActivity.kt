package com.example.astronomyguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.astronomyguide.ui.theme.AstronomyGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstronomyGuideTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.News.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.News.route) {
                NewsScreen()
            }
            composable(Screen.SolarSystem.route) {
                SolarSystemScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object News : Screen("news", "Новости", Icons.Default.Newspaper)
    object SolarSystem : Screen("solarsystem", "Солнечная система", Icons.Default.Public)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(Screen.News, Screen.SolarSystem)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF4A1E6D),
        contentColor = Color.White
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF9D71D3),  // LightPurple
                    selectedTextColor = Color(0xFF9D71D3),  // LightPurple
                    unselectedIconColor = Color(0xFFB39DDB),  // AccentPurple
                    unselectedTextColor = Color(0xFFB39DDB),  // AccentPurple
                    indicatorColor = Color(0xFF6B3FA0)  // MediumPurple
                )
            )
        }
    }
}