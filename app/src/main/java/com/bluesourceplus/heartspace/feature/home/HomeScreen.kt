package com.bluesourceplus.heartspace.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bluesourceplus.heartspace.components.MoodCard
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreenRoute(
    onAddButton: () -> Unit,
    onMoodCardPressed: (Int) -> Unit,
    onUpdateMoodPressed: (Int) -> Unit = { }, // Placeholder for update mood action
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        onAddButton = onAddButton,
        onMoodCardPressed = onMoodCardPressed,
        state = state,
        onHomeScreenState = viewModel::handleEvent,
        onUpdateMoodPressed = onUpdateMoodPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    onAddButton: () -> Unit,
    onMoodCardPressed: (Int) -> Unit,
    state: HomeScreenState,
    onHomeScreenState: (HomeScreenIntent) -> Unit,
    onUpdateMoodPressed: (Int) -> Unit = {},
) {
    val formattedDate = remember(state.selectedDate) {
        formatSelectedDate(state.selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Heartspace", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp) },
        )
        Text(
            text = "How are you feeling?",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        DayNavigator(
            formattedDate = formattedDate,
            canGoNext = state.canGoNext,
            onPreviousDay = { onHomeScreenState(HomeScreenIntent.GoToPreviousDay) },
            onNextDay = { onHomeScreenState(HomeScreenIntent.GoToNextDay) },
            onToday = { onHomeScreenState(HomeScreenIntent.GoToToday) },
        )

        Spacer(modifier = Modifier.height(10.dp))

        AddNewEntryCard {
            onAddButton()
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.moods.isEmpty()) {
                item {
                    HomeEmptyContent(
                        formattedDate = formattedDate,
                    )
                }
            } else {
                items(state.moods, key = { mood -> mood.id }) { mood ->
                    Spacer(modifier = Modifier.height(10.dp))
                    MoodCard(
                        moodModel = mood,
                        onMoodPressed = { onMoodCardPressed(mood.id) },
                        onDelete = { onHomeScreenState(HomeScreenIntent.DeleteMood(mood.id)) },
                        onUpdatePressed = { onUpdateMoodPressed(it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DayNavigator(
    formattedDate: String,
    canGoNext: Boolean,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous day",
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(onClick = onToday) {
                Text("Today")
            }
        }
        IconButton(
            onClick = onNextDay,
            enabled = canGoNext,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next day",
            )
        }
    }
}

@Composable
fun AddNewEntryCard(onAddButton: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onAddButton,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Add Mood",
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tap to add new mood",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun HomeEmptyContent(
    modifier: Modifier = Modifier,
    formattedDate: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No mood entries for $formattedDate",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatSelectedDate(date: LocalDate): String {
    val month = date.month.name.lowercase().replaceFirstChar { it.titlecase() }
    return "$month ${date.dayOfMonth}, ${date.year}"
}
