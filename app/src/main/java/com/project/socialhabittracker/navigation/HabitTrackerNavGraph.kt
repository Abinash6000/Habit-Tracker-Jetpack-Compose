package com.project.socialhabittracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.project.socialhabittracker.data.remote.auth.AuthViewModel
import com.project.socialhabittracker.ui.add_habit.AddHabit
import com.project.socialhabittracker.ui.add_habit.AddHabitDestination
import com.project.socialhabittracker.ui.auth.LoginDestination
import com.project.socialhabittracker.ui.auth.LoginPage
import com.project.socialhabittracker.ui.auth.SignupDestination
import com.project.socialhabittracker.ui.auth.SignupPage
import com.project.socialhabittracker.ui.edit_habit.EditHabit
import com.project.socialhabittracker.ui.edit_habit.EditHabitDestination
import com.project.socialhabittracker.ui.habit_report.HabitReport
import com.project.socialhabittracker.ui.habit_report.HabitReportDestination
import com.project.socialhabittracker.ui.home.HomeDestination
import com.project.socialhabittracker.ui.home.HomeScreen
import com.project.socialhabittracker.ui.overall_report.OverallReport
import com.project.socialhabittracker.ui.settings.AboutDialog
import com.project.socialhabittracker.ui.settings.Settings
import com.project.socialhabittracker.utils.Extensions

/**
 * Provides Navigation Graph for the application
 */
@Composable
fun HabitTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val isLoggedIn = Firebase.auth.currentUser != null

    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) HomeDestination.route else LoginDestination.route,
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
            OverallReport(
                navController = navController
            )
        }

        composable(route = Settings.route) {
            val authViewModel = AuthViewModel()

            Settings(
                onEditProfileClick = {},
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(LoginDestination.route) {
                        popUpTo(HomeDestination.route) {
                            inclusive = true
                        }
                    }
                },
                onRateClick = {},
            )
        }

        composable(route = LoginDestination.route) {
            val authViewModel = AuthViewModel()

            val context = LocalContext.current

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            var isLoading by remember { mutableStateOf(false) }

            LoginPage(
                onLoginClick = {
                    isLoading = true

                    authViewModel.login(
                        email = email,
                        password = password
                    ) { success, errorMessage ->
                        if(success) {
                            isLoading = false
                            navController.navigate(HomeDestination.route) {
                                popUpTo(LoginDestination.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            isLoading = false
                            Extensions.showToast(context, errorMessage)
                        }
                    }
                },
                onSignUpClick = { navController.navigate(SignupDestination.route) },
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isLoading = isLoading,
            )
        }

        composable(route = SignupDestination.route) {
            val authViewModel = AuthViewModel()

            val context = LocalContext.current

            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var name by remember { mutableStateOf("") }

            var isLoading by remember { mutableStateOf(false) }

            SignupPage(
                onLoginClick = { navController.navigate(LoginDestination.route) },
                onSignUpClick = {
                    isLoading = true

                    authViewModel.signup(
                        email = email,
                        password = password,
                        name = name
                    ) { success, errorMessage ->
                        if(success) {
                            isLoading = false
                            navController.navigate(HomeDestination.route) {
                                popUpTo(LoginDestination.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            isLoading = false
                            Extensions.showToast(context, errorMessage)
                        }
                    }
                },
                email = email,
                onEmailChange = { email = it },
                name = name,
                onNameChange = { name = it },
                password = password,
                onPasswordChange = { password = it },
                isLoading = isLoading,
            )
        }
    }
}