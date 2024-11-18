package com.project.socialhabittracker.ui.habit_report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.socialhabittracker.R
import com.project.socialhabittracker.data.db.habit_db.Habit
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.ui.AppViewModelProvider
import com.project.socialhabittracker.ui.home.HabitInfo
import com.project.socialhabittracker.ui.home.HabitTrackerTopAppBar
import com.project.socialhabittracker.ui.home.HomeDestination

object HabitReportDestination : NavigationDestination {
    override val route = "report"
    override val titleRes = R.string.habit_report_title
    const val habitIdArg = "habitIdArg"
    val routeWithArgs = "$route/{$habitIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitReport(
    viewModel: HabitReportViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState = viewModel.habitReportUiState

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(id = HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.add_habit_title)
                )
            }
        }
    ) { innerPadding ->
        HabitReportBody(
            homeUiState,
            innerPadding
        )
    }
}

@Composable
fun HabitReportBody(
    homeUiState: HabitInfo,
    innerPadding: PaddingValues
) {
    Column(
        Modifier.padding(innerPadding)
    ) {
        Text(text = homeUiState.habit.name)
    }
}

@Preview(showBackground = true)
@Composable
fun HabitReportBodyPreview() {
    HabitReportBody(
        homeUiState = HabitInfo(habit = Habit(name = "Swim")),
        innerPadding = PaddingValues(0.dp)
    )
}