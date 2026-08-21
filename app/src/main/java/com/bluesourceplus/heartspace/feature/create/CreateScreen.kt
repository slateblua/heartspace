package com.bluesourceplus.heartspace.feature.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bluesourceplus.heartspace.components.formatTimestamp
import com.bluesourceplus.heartspace.components.icon
import com.bluesourceplus.heartspace.data.Mood
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CreateScreenRoute(mode: CreateMoodMode, back: () -> Unit) {
    val createViewModel: CreateViewModel = koinViewModel { parametersOf(mode) }
    val state by createViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        createViewModel.sideEffect.collect { effect ->
            when (effect) {
                CreateMoodEffect.MoodSaved -> {
                    // empty for now
                }

                CreateMoodEffect.NavigateUp -> {
                    back()
                }
            }
        }
    }

    CreateScreen(
        state = state,
        onCreateMoodIntent = createViewModel::handleEvent,
        back = back
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    state: State,
    onCreateMoodIntent: (CreateMoodIntent) -> Unit,
    back: () -> Unit
) {
    Column {
        CenterAlignedTopAppBar(
            title = { Text(text = "Heartspace") },
            navigationIcon = {
                IconButton(onClick = back) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { onCreateMoodIntent(CreateMoodIntent.OnSaveClicked) }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (state is State.Content) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    Text(
                        text = formatTimestamp(System.currentTimeMillis()),
                        fontSize = 16.sp,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val moods = Mood.entries.filter { it != Mood.UNKNOWN }
                    moods.forEachIndexed { index, mood ->
                        MoodButton(
                            mood = mood,
                            onClick = {
                                onCreateMoodIntent(CreateMoodIntent.OnMoodChanged(moods[index]))
                            },
                            isSelected = (mood == state.mood),
                        )
                    }
                }
                OutlinedTextField(
                    value = state.note,
                    onValueChange = {
                        onCreateMoodIntent(CreateMoodIntent.OnNoteChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("How are you feeling?") }
                )
            }
        }
    }
}


@Composable
fun MoodButton(mood: Mood, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(60.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(2.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
    ) {
        Icon(
            imageVector = mood.icon(),
            contentDescription = mood.displayName,
            modifier = Modifier.size(35.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
