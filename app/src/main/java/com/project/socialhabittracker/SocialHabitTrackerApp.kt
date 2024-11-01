package com.project.socialhabittracker

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.socialhabittracker.navigation.HabitTrackerNavHost
import com.project.socialhabittracker.ui.home.HomeScreen

@Composable
fun SocialHabitTrackerApp(navController: NavHostController = rememberNavController()) {
    HabitTrackerNavHost(navController = navController)
}