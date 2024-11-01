package com.project.socialhabittracker.ui.home

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.socialhabittracker.R
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.ui.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddHabit: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

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
        HomeBody(
            habitInfo = homeUiState.habitsList,
            contentPadding = innerPadding
        )
    }
}

@Composable
fun HomeBody(
    habitInfo: List<HabitInfo>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (habitInfo.isEmpty()) {
            Text(
                text = stringResource(R.string.add_your_habits_by_clicking_on),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(contentPadding)
            ) {
                DatesCard(title = "Habits")
                HabitList(
                    habitInfo = habitInfo
                )
            }
        }
    }
}

@Composable
fun HabitList(
    habitInfo: List<HabitInfo>
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = habitInfo, key = { it.habit.id }) {
            HabitCompletionCard(habitInfo = it)
        }
    }
}

fun lastFiveDates(): List<String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    val lastFiveDates = mutableListOf<String>()

    for (i in 1..5) {
        val date = sdf.format(cal.time)
        lastFiveDates.add(date)
        cal.add(Calendar.DATE, -1)
    }

    return lastFiveDates
}

@Composable
fun DatesCard(datesList: List<String> = lastFiveDates().map { it.split("-")[2] }, title: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(140.dp)
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Canvas(
                    modifier = Modifier
                        .height(15.dp)
                        .wrapContentWidth()
                ) {
                    drawLine(
                        start = Offset(x = 0F, y = size.height),
                        end = Offset(x = 0F, y = 0F),
                        color = Color.Gray,
                        strokeWidth = 1.dp.toPx()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
            ) {
                for (date in datesList) {
                    Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
                        Text(text = date)
                        Canvas(
                            Modifier
                                .padding(2.dp)
                                .wrapContentSize()) {
                            drawCircle(
                                color = Color.LightGray,
                                radius = 35f,
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitCompletionCard(
    habitInfo: HabitInfo,
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val lastFiveDates = lastFiveDates()
    val progressList = habitInfo.habitCompletionDetails.filter { completion ->
            val completionDate = sdf.format(Date(completion.date))
            lastFiveDates.contains(completionDate)
        }.sortedByDescending { completion ->
            completion.date
    }
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 4.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = habitInfo.habit.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(140.dp)
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Canvas(
                    modifier = Modifier
                        .height(15.dp)
                        .wrapContentWidth()
                ) {
                    drawLine(
                        start = Offset(x = 0F, y = size.height),
                        end = Offset(x = 0F, y = 0F),
                        color = Color.Gray,
                        strokeWidth = 1.dp.toPx()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
            ) {
                for (i in 0..4) {
                    val date = lastFiveDates[i] // Assume this list has the formatted dates in order
                    val completionForDate = progressList.find { completion ->
                        val completionDate = sdf.format(Date(completion.date))
                        completionDate == date
                    }

                    if(habitInfo.habit.type.equals("yes or no", true)) {
                        Text(text = if(completionForDate?.isCompleted != null && completionForDate.isCompleted) "V" else "X")
                    } else{
                        Text(text = completionForDate?.progressValue?.toString() ?: "0")
                    }
                }
            }
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
