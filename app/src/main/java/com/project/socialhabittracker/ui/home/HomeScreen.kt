package com.project.socialhabittracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.H
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.socialhabittracker.R
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.ui.AppViewModelProvider

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddHabit: () -> Unit,
    navigateToHabitReport: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

    var showProgressDialog by remember { mutableStateOf(false) }
    var completionForDate by remember { mutableStateOf(HabitCompletion(0, 0)) }

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
                onClick = navigateToAddHabit,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_habit_title)
                )
            }
        }
    ) { innerPadding ->
        if (showProgressDialog) {
            AlertDialog(
                onDismissRequest = { showProgressDialog = false },
            ) {
                ProgressDialog(
                    progressValue = completionForDate.progressValue,
                    onValueChange = {
                        completionForDate = completionForDate.copy(progressValue = it)
                    },
                    onConfirm = {
                        showProgressDialog = false
                        viewModel.upsert(completionForDate)
                    },
                    onCancel = { showProgressDialog = false }
                )
            }
        }
        HomeBody(
            habitInfo = homeUiState.habitsList,
            contentPadding = innerPadding,
            upsurt = { viewModel.upsert(it) },
            showProgressDialog = { showProgressDialog = it },
            dataToUpsertForConfirmClick = { completionForDate = it },
            navigateToHabitReport = { navigateToHabitReport(it) },
            onItemClick = { dropDownItem, habitId ->
                viewModel.itemClick(dropDownItem, habitId)
            }
        )
    }
}

@Composable
fun HomeBody(
    habitInfo: List<HabitInfo>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    upsurt: (HabitCompletion) -> Unit,
    showProgressDialog: (Boolean) -> Unit,
    dataToUpsertForConfirmClick: (HabitCompletion) -> Unit,
    navigateToHabitReport: (Int) -> Unit,
    onItemClick: (DropDownItem, Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        DatesCard(title = "Habits")
        HabitList(
            habitInfo = habitInfo,
            upsurt = upsurt,
            showProgressDialog = showProgressDialog,
            dataToUpsertForConfirmClick = dataToUpsertForConfirmClick,
            navigateToHabitReport = { navigateToHabitReport(it) },
            onItemClick = onItemClick
        )
    }
}

@Composable
fun HabitList(
    habitInfo: List<HabitInfo>,
    upsurt: (HabitCompletion) -> Unit,
    showProgressDialog: (Boolean) -> Unit,
    dataToUpsertForConfirmClick: (HabitCompletion) -> Unit,
    navigateToHabitReport: (Int) -> Unit,
    onItemClick: (DropDownItem, Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = habitInfo, key = { it.habit.id }) {habitInfo ->
            HabitCompletionCard(
                habitInfo = habitInfo,
                dropDownItems = listOf(DropDownItem("Delete")),
                onItemClick = { onItemClick(it, habitInfo.habit.id) },
                upsert = upsurt,
                showProgressDialog = showProgressDialog,
                dataToUpsertForConfirmClick = dataToUpsertForConfirmClick,
                navigateToHabitReport = { navigateToHabitReport(it)}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackerTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
