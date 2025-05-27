package com.project.socialhabittracker.ui.edit_habit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.socialhabittracker.R
import com.project.socialhabittracker.data.local.habit_db.Habit
import com.project.socialhabittracker.navigation.NavigationDestination
import com.project.socialhabittracker.AppViewModelProvider
import com.project.socialhabittracker.ui.home.HabitTrackerTopAppBar
import kotlinx.coroutines.launch

object EditHabitDestination : NavigationDestination {
    override val route: String = "edit_habit"
    override val titleRes: Int = R.string.edit_habit_title
    const val habitIdArg = "habitIdArg"
    val routeWithArgs = "$route/{$habitIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHabit(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: EditHabitViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(id = R.string.edit_habit_title),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        EditHabitBody(
            editHabitUiState = viewModel.editHabitUiState,
            isEntryValid = viewModel.validateInput(viewModel.editHabitUiState),
            onItemValueChange = {viewModel.updateEditHabitUiState(it)},
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveHabit()
                    navigateBack()
                }
            },
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun EditHabitBody(
    editHabitUiState: Habit,
    isEntryValid: Boolean,
    onItemValueChange: (Habit) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        // Name Field
        OutlinedTextField(
            value = editHabitUiState.name,
            onValueChange = {
                onItemValueChange(editHabitUiState.copy(name = it))
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Unit Field
        OutlinedTextField(
            value = editHabitUiState.unit,
            onValueChange = {
                onItemValueChange(editHabitUiState.copy(unit = it))
            },
            label = { Text("Unit") },
            modifier = Modifier.fillMaxWidth()
        )

        if(editHabitUiState.type.equals("Measurable"))
        // Target Field
            OutlinedTextField(
                value = editHabitUiState.targetCount,
                onValueChange = {
                    onItemValueChange(editHabitUiState.copy(targetCount = it))
                },
                label = { Text("Target") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

        // Notes Field
        OutlinedTextField(
            value = editHabitUiState.note,
            onValueChange = {
                onItemValueChange(editHabitUiState.copy(note = it))
            },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        // Save Button
        Button(
            onClick = onSaveClick,
            enabled = isEntryValid,
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Preview
@Composable
fun EditHabitPreview() {
    EditHabit(navigateBack = {}, onNavigateUp = {})
}