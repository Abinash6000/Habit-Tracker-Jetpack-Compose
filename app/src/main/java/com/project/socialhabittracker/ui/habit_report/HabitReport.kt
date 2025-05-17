package com.project.socialhabittracker.ui.habit_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.socialhabittracker.R
import com.project.socialhabittracker.data.local.db.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.local.db.habit_db.Habit
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.ui.AppViewModelProvider
import com.project.socialhabittracker.ui.home.HabitInfo
import com.project.socialhabittracker.ui.home.HabitTrackerTopAppBar
import com.project.socialhabittracker.ui.home.convertToDate
import com.project.socialhabittracker.ui.home.convertToMonthYear

object HabitReportDestination : NavigationDestination {
    override val route = "report"
    override val titleRes = R.string.habit_report_title
    const val habitIdArg = "habitIdArg"
    val routeWithArgs = "$route/{$habitIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitReport(
    viewModel: HabitReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateUp: () -> Unit,
    navigateToEditHabit: (Int) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val habitReportUiState = viewModel.habitReportUiState
    val habitReportScores = viewModel.habitReportScore

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HabitTrackerTopAppBar(
                title = habitReportUiState.habit.name,
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditHabit(viewModel.habitId) },
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
            habitReportUiState,
            habitReportScores,
            innerPadding
        )
    }
}

@Composable
fun HabitReportBody(
    habitReportUiState: HabitInfo,
    habitReportScores: HabitScores,
    innerPadding: PaddingValues
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        NoteCard(
            note = habitReportUiState.habit.note,
            modifier = Modifier.padding(12.dp)
        )
        ProgressChartCard(
            habitCompletionList = habitReportUiState.habitCompletionDetails,
            isMeasurable = habitReportUiState.habit.type == "Measurable",
            modifier = Modifier.padding(12.dp)
        )
        ArrangedScoreCards(
            perfectDaysScore = habitReportScores.perfectDaysScore,
            currentStreakScore = habitReportScores.currentStreakScore,
            bestStreakScore = habitReportScores.bestStreakScore,
            percentageScore = habitReportScores.percentageScore,
            overallScore = habitReportScores.overallScore,
            missedDaysScore = habitReportScores.missedDaysScore,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun NoteCard(
    note: String,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.wrapContentSize()
        ) {
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ProgressChartCard(
    habitCompletionList: List<HabitCompletion>,
    isMeasurable: Boolean,
    modifier: Modifier = Modifier
) {
    // Find the maximum progressValue
    var maxProgressValue = 1f
    for(hc in habitCompletionList) {
        if(hc.progressValue.toFloat() > maxProgressValue)
            maxProgressValue = hc.progressValue.toFloat()
    }

    // Create a LazyListState
    val listState = rememberLazyListState()

    // Automatically scroll to the last item when the composable is recomposed
    LaunchedEffect(habitCompletionList) {
        if (habitCompletionList.isNotEmpty()) {
            listState.scrollToItem(habitCompletionList.lastIndex)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(habitCompletionList) { habitCompletion ->
                    BarItem(
                        habitCompletion = habitCompletion,
                        isMeasurable = isMeasurable,
                        maxProgressValue = maxProgressValue
                    )
                }
            }
        }
    }
}

@Composable
fun BarItem(
    habitCompletion: HabitCompletion,
    isMeasurable: Boolean,
    maxProgressValue: Float
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()  // Fill the available height
            .wrapContentHeight(Alignment.Bottom)  // Align content to the bottom of the column
    ) {
        // Determine progressValue and normalize it relative to maxProgressValue
        val progressValue = if (isMeasurable) {
            habitCompletion.progressValue.toFloat()
        } else {
            if (habitCompletion.isCompleted) 1f else 0f
        }
        val normalizedHeight = (progressValue / maxProgressValue) * 100

        val date = convertToDate(habitCompletion.date)
        val monthYear = convertToMonthYear(habitCompletion.date)
        val dateText = if(date.equals("01")) monthYear else date

        if(isMeasurable) {
            Text(
                text = if (progressValue % 1 == 0f) {
                    progressValue.toInt().toString() // Convert to Int if it's a whole number
                } else {
                    progressValue.toString() // Otherwise, keep it as Float
                },
                fontSize = 12.sp
            )
        } else {
            Icon(
                imageVector = if(habitCompletion.isCompleted) Icons.Default.Check else Icons.Default.Clear,
                contentDescription = if(habitCompletion.isCompleted) "Checked" else "Unchecked",
                modifier = Modifier.size(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(normalizedHeight.dp)
                .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = dateText,
            fontSize = if(dateText.equals(monthYear)) 8.sp else 12.sp
        )
    }
}

@Composable
fun ArrangedScoreCards(
    perfectDaysScore: String,
    currentStreakScore: String,
    bestStreakScore: String,
    percentageScore: String,
    overallScore: String,
    missedDaysScore: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.5f)
        ) {
            ScoreCard(
                title = "Perfect Days",
                score = perfectDaysScore,
                additionalScore = null
            )
            ScoreCard(
                title = "Current Streak",
                score = currentStreakScore,
                additionalScore = "Best Streak: $bestStreakScore"
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.5f)
        ) {
            ScoreCard(
                title = "Score",
                score = "$percentageScore%",
                additionalScore = "$overallScore perfect days"
            )
            ScoreCard(
                title = "Days Missed",
                score = missedDaysScore,
                additionalScore = null
            )
        }
    }
}

@Composable
fun ScoreCard(
    title: String,
    score: String,
    additionalScore: String?
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = score,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            if(additionalScore != null)
                Text(
                    text = additionalScore,
                    fontSize = 12.sp
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitReportBodyPreview() {
    HabitReportBody(
        habitReportUiState = HabitInfo(habit = Habit(name = "Swim")),
        habitReportScores = HabitScores(),
        innerPadding = PaddingValues(0.dp)
    )
}