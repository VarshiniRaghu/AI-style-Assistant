package com.smartstyle.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartstyle.ui.screens.analyse.AnalyseScreen
import com.smartstyle.ui.screens.history.HistoryScreen

private sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Analyse : Screen("analyse", "Analyse", Icons.Default.Style)
    object History : Screen("history", "History", Icons.Default.History)
}

private val screens = listOf(Screen.Analyse, Screen.History)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { _ ->
        NavHost(navController = navController, startDestination = Screen.Analyse.route) {
            composable(Screen.Analyse.route) { AnalyseScreen() }
            composable(Screen.History.route) { HistoryScreen() }
        }
    }
}
