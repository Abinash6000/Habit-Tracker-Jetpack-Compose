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
import com.project.socialhabittracker.ui.edit_habit.EditHabit
import com.project.socialhabittracker.ui.edit_habit.EditHabitDestination
import com.project.socialhabittracker.ui.habit_report.HabitReport
import com.project.socialhabittracker.ui.habit_report.HabitReportDestination
import com.project.socialhabittracker.ui.home.HomeDestination
import com.project.socialhabittracker.ui.home.HomeScreen
import com.project.socialhabittracker.ui.overall_report.OverallReport
import com.project.socialhabittracker.ui.ranking.Ranking
import com.project.socialhabittracker.ui.settings.Settings

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
                navigateToHabitReport = { navController.navigate("${HabitReportDestination.route}/${it}") },
                navigateToEditHabit = { navController.navigate("${EditHabitDestination.route}/${it}")},
                navController = navController
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
            HabitReport(
                onNavigateUp = { navController.navigateUp() },
                navigateToEditHabit = { navController.navigate("${EditHabitDestination.route}/${it}") }
            )
        }

        composable(
            route = EditHabitDestination.routeWithArgs,
            arguments = listOf(navArgument(EditHabitDestination.habitIdArg) {
                type = NavType.IntType
            })
        ) {
            EditHabit(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(route = OverallReport.route) {
            OverallReport()
        }

        composable(route = Ranking.route) {
            Ranking()
        }

        composable(route = Settings.route) {
            Settings()
        }
    }
}