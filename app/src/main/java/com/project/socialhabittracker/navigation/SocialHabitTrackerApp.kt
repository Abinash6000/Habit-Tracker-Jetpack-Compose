package com.project.socialhabittracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun SocialHabitTrackerApp(navController: NavHostController = rememberNavController()) {
    HabitTrackerNavHost(navController = navController)
}