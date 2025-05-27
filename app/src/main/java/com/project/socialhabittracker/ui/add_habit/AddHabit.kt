package com.project.socialhabittracker.ui.add_habit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

object AddHabitDestination : NavigationDestination {
    override val route: String = "add_habit"
    override val titleRes: Int = R.string.add_habit_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabit(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: AddHabitViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            HabitTrackerTopAppBar(
                title = stringResource(id = R.string.add_habit_title),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        AddHabitBody(
            addHabitUiState = viewModel.addHabitUiState,
            onItemValueChange = {viewModel.updateAddHabitUiState(it)},
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
fun AddHabitBody(
    addHabitUiState: AddHabitUiState,
    onItemValueChange: (Habit) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Options for the habit type
    val typeOptions = listOf("Yes or No", "Measurable")
    var expanded by remember { mutableStateOf(false) } // For the dropdown menu
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        // Name Field
        OutlinedTextField(
            value = addHabitUiState.habit.name,
            onValueChange = {
                onItemValueChange(addHabitUiState.habit.copy(name = it))
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Unit Field
        OutlinedTextField(
            value = addHabitUiState.habit.unit,
            onValueChange = {
                onItemValueChange(addHabitUiState.habit.copy(unit = it))
                },
            label = { Text("Unit") },
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type Field with Dropdown
            OutlinedTextField(
                value = addHabitUiState.habit.type,
                onValueChange = {
                    onItemValueChange(addHabitUiState.habit.copy(type = it))
                },
                label = { Text("Type") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                readOnly = true,
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowDropDown, "Type",
                        Modifier.clickable { expanded = !expanded })
                }
            )

            // Dropdown for Type Selection
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                typeOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            onItemValueChange(addHabitUiState.habit.copy(type = option))
                            expanded = false
                        }
                    )
                }
            }
        }

        if(addHabitUiState.habit.type.equals("Measurable"))
            // Target Field
            OutlinedTextField(
                value = addHabitUiState.habit.targetCount,
                onValueChange = {
                    onItemValueChange(addHabitUiState.habit.copy(targetCount = it))
                },
                label = { Text("Target") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

        // Notes Field
        OutlinedTextField(
            value = addHabitUiState.habit.note,
            onValueChange = {
                onItemValueChange(addHabitUiState.habit.copy(note = it))
            },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        // Save Button
        Button(
            onClick = onSaveClick,
            enabled = addHabitUiState.isEntryValid,
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Preview
@Composable
fun AddHabitPreview() {
    AddHabit(navigateBack = {}, onNavigateUp = {})
}