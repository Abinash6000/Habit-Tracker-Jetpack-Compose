package com.project.socialhabittracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.socialhabittracker.ui.add_habit.AddHabit
import com.project.socialhabittracker.ui.add_habit.AddHabitDestination
import com.project.socialhabittracker.ui.habit_report.HabitReport
import com.project.socialhabittracker.ui.habit_report.HabitReportDestination
import com.project.socialhabittracker.ui.home.HomeDestination
import com.project.socialhabittracker.ui.home.HomeScreen

/**
 * Provides Navigation Graph for the application
 */
@Composable
fun HabitTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToAddHabit = { navController.navigate(AddHabitDestination.route) },
                navigateToHabitReport = { navController.navigate("${HabitReportDestination.route}/${it}") }
            )
        }

        composable(route = AddHabitDestination.route) {
            AddHabit(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = HabitReportDestination.routeWithArgs,
            arguments = listOf(navArgument(HabitReportDestination.habitIdArg) {
                type = NavType.IntType
            })
        ) {
            HabitReport()
        }
    }
}