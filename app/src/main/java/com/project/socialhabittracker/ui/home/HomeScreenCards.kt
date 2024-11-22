package com.project.socialhabittracker.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.socialhabittracker.data.db.habit_completion_db.HabitCompletion
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DatesCard(datesList: List<String> = lastFiveDates().map { it.split("-")[2] }, title: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val dateBorderStroke = colorScheme.secondary
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = dateBorderStroke,
                    fontWeight = FontWeight.SemiBold,
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
                        color = dateBorderStroke,
                        strokeWidth = 1.5.dp.toPx()
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
                        Text(
                            text = date,
                            color = dateBorderStroke
                        )
                        Canvas(
                            Modifier
                                .padding(2.dp)
                                .wrapContentSize()) {
                            drawCircle(
                                color = dateBorderStroke,
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

data class DropDownItem(
    val text: String
)

@Composable
fun HabitCompletionCard(
    habitInfo: HabitInfo,
    dropDownItems: List<DropDownItem>,
    onItemClick: (DropDownItem) -> Unit,
    upsert: (HabitCompletion) -> Unit,
    showProgressDialog: (Boolean) -> Unit,
    dataToUpsertForConfirmClick: (HabitCompletion) -> Unit,
    navigateToHabitReport: (Int) -> Unit
) {
    // variables for getting completions of 5 recent days
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val lastFiveDates = lastFiveDates()
    val progressList = habitInfo.habitCompletionDetails.filter { completion ->
        val completionDate = sdf.format(Date(completion.date))
        lastFiveDates.contains(completionDate)
    }.sortedByDescending { completion ->
        completion.date
    }

    // variables for showing context menu
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        // Box for showing content and dropdown menu on top of content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = {
                            navigateToHabitReport(habitInfo.habit.id)
                        },
                        onLongPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        }
                    )
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
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
                        .height(36.dp)
                ) {
                    for (i in 0..4) {

                        val date =
                            lastFiveDates[i] // Assume this list has the formatted dates in order
                        var completionForDate = progressList.find { completion ->
                            val completionDate = sdf.format(Date(completion.date))
                            completionDate == date
                        }
                        if (completionForDate == null) {
                            completionForDate = HabitCompletion(
                                habitId = habitInfo.habit.id,
                                date = convertToMillis(date),
                                isCompleted = false,
                                progressValue = "0"
                            )
                        }

                        Box(
                            modifier = Modifier.clickable(
                                onClick = {
                                    if (habitInfo.habit.type.equals("measurable", true)) {
                                        showProgressDialog(true)
                                        dataToUpsertForConfirmClick(completionForDate)
                                    } else {
                                        upsert(
                                            completionForDate.copy(
                                                isCompleted = !completionForDate.isCompleted
                                            )
                                        )
                                    }
                                }
                            )
                        ) {
                            if (habitInfo.habit.type.equals("yes or no", true)) {
                                if (completionForDate.isCompleted) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Completed"
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Not Completed"
                                    )
                                }
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = completionForDate.progressValue,
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = habitInfo.habit.unit,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            DropdownMenu(
                expanded = isContextMenuVisible,
                onDismissRequest = { isContextMenuVisible = false },
                offset = pressOffset.copy(
                    y = pressOffset.y - itemHeight
                )
            ) {
                dropDownItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.text) },
                        onClick = {
                            onItemClick(item)
                            isContextMenuVisible = false
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun ProgressDialog(
    progressValue: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val color = colorScheme.primary
    Surface(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val focusRequester = remember { FocusRequester() }
            val keyboard = LocalSoftwareKeyboardController.current

            // Progress TextField
            BasicTextField(
                value = TextFieldValue(
                    text = progressValue,
                    selection = TextRange(progressValue.length)
                ),
                onValueChange = {
                    onValueChange(it.text)
                },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 24.sp, color = color),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                keyboardActions = KeyboardActions(
                    onDone = {
                        this.defaultKeyboardAction(ImeAction.Done)
                        onConfirm()
                        onCancel()
                    }
                ),
                cursorBrush = SolidColor(color)
            )
            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                delay(100) // Make sure you have delay here
                keyboard?.show()
            }
            Canvas(modifier = Modifier
                .width(160.dp)
                .padding(horizontal = 8.dp)) {
                drawLine(
                    color = color,
                    start = Offset(0f,0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2f
                )
            }
            Row(
                modifier = Modifier
                    .width(140.dp)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cancel icon (cross)
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Default.Close, // Use a cross icon
                        contentDescription = "Cancel",
                    )
                }
                Canvas(modifier = Modifier
                    .height(30.dp)
                    .padding(horizontal = 8.dp)) {
                    drawLine(
                        color = color,
                        start = Offset(0f,0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2f
                    )
                }
                // Confirm icon (check)
                IconButton(onClick = onConfirm) {
                    Icon(
                        imageVector = Icons.Default.Check, // Use a check icon
                        contentDescription = "Confirm",
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProgressDialog() {
    var progress by remember { mutableStateOf("") }

    ProgressDialog(
        progressValue = progress,
        onValueChange = { newValue -> progress = newValue },
        onConfirm = { /* Handle confirmation */ },
        onCancel = { /* Handle cancellation */ },
    )
}
