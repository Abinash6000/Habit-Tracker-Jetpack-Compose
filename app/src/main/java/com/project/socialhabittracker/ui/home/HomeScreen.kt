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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.socialhabittracker.R
import com.project.socialhabittracker.data.local.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.ui.AppViewModelProvider
import com.project.socialhabittracker.ui.overall_report.OverallReport
import com.project.socialhabittracker.ui.settings.Settings
import java.util.Calendar

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddHabit: () -> Unit,
    navigateToHabitReport: (Int) -> Unit,
    navigateToEditHabit: (Int) -> Unit,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

    var showProgressDialog by remember { mutableStateOf(false) }
    var completionForDate by remember { mutableStateOf(HabitCompletion(0, convertToMillis(convertToDateMonthYear(Calendar.getInstance().timeInMillis)))) }
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
        },
        bottomBar = {
            BottomBar(
                tabNum = 0,
                bottomBarItems = getTabBarItems(),
                navController = navController
            )
        }
    ) { innerPadding ->
        if (showProgressDialog) {
            AlertDialog(
                onDismissRequest = { showProgressDialog = false },
            ) {
                var valueChanged by remember { mutableStateOf(false) }
                ProgressDialog(
                    progressValue = if(valueChanged) completionForDate.progressValue else "",
                    onValueChange = {
                        completionForDate = completionForDate.copy(progressValue = it)
                        valueChanged = true
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
                when (dropDownItem.text) {
                    "Delete" -> viewModel.deleteHabit(habitId)
                    "Edit" -> navigateToEditHabit(habitId)
                }
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
                dropDownItems = listOf(DropDownItem("Edit"), DropDownItem("Delete")),
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

data class TabBarItem(
    val title: Int,
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
)

fun getTabBarItems(): List<TabBarItem> {
    val homeTab = TabBarItem(title = R.string.home, route = HomeDestination.route, selectedIcon = R.drawable.filled_home, unselectedIcon = R.drawable.outlined_home)
    val overallReportTab = TabBarItem(title = R.string.report, route = OverallReport.route, selectedIcon = R.drawable.filled_bar_chart, unselectedIcon = R.drawable.filled_bar_chart)
    val settingsTab = TabBarItem(title = R.string.settings, route = Settings.route, selectedIcon = R.drawable.settings_filled, unselectedIcon = R.drawable.outlined_settings)

    return listOf(homeTab, overallReportTab, settingsTab)
}

@Composable
fun BottomBar(tabNum: Int, bottomBarItems: List<TabBarItem>, navController: NavHostController) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(tabNum)
    }
    NavigationBar {
        bottomBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.route)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = ImageVector.vectorResource(tabBarItem.selectedIcon),
                        unselectedIcon = ImageVector.vectorResource(tabBarItem.unselectedIcon),
                        title = stringResource(id = tabBarItem.title)
                    )
                },
                label = {Text(text = stringResource(tabBarItem.title))})
        }
    }
}

@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String
) {
    Icon(
        imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
        contentDescription = title
    )
}
