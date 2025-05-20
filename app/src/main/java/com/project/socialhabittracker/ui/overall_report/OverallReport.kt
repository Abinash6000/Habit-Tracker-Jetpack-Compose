package com.project.socialhabittracker.ui.overall_report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.socialhabittracker.R
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.ui.AppViewModelProvider
import com.project.socialhabittracker.ui.habit_report.ArrangedScoreCards
import com.project.socialhabittracker.ui.habit_report.HabitReportBody
import com.project.socialhabittracker.ui.habit_report.HabitScores
import com.project.socialhabittracker.ui.home.BottomBar
import com.project.socialhabittracker.ui.home.HabitInfo
import com.project.socialhabittracker.ui.home.HabitTrackerTopAppBar
import com.project.socialhabittracker.ui.home.getTabBarItems

object OverallReport : NavigationDestination {
    override val route: String = "overall_report"
    override val titleRes: Int = R.string.overall_report
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallReport(
    viewModel: OverallReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToHome: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val reportScores = viewModel.reportScore

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(id = R.string.overall_report),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomBar(
                tabNum = 1,
                bottomBarItems = getTabBarItems(),
                navigateToHome = navigateToHome,
                navigateToSettings = navigateToSettings,
            )
        }
    ) { innerPadding ->
        ReportBody(
            reportScores,
            innerPadding
        )
    }
}

@Composable
fun ReportBody(reportScores: HabitScores, innerPadding: PaddingValues) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(innerPadding)
    ) {
        ArrangedScoreCards(
            perfectDaysScore = reportScores.perfectDaysScore,
            currentStreakScore = reportScores.currentStreakScore,
            bestStreakScore = reportScores.bestStreakScore,
            percentageScore = reportScores.percentageScore,
            overallScore = reportScores.overallScore,
            missedDaysScore = reportScores.missedDaysScore,
            modifier = Modifier.padding(12.dp)
        )
    }
}
