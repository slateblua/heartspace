package com.bluesourceplus.heartspace.feature.aboutmoodentry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bluesourceplus.heartspace.components.DeleteMoodAlert
import org.koin.androidx.compose.koinViewModel

@Composable
fun AboutMoodRoute(
    moodId: Int,
    back: () -> Unit,
    onUpdateMoodPressed: (Int) -> Unit
) {
    val aboutMoodViewModel: AboutMoodViewModel = koinViewModel()
    val state by aboutMoodViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(moodId) {
        aboutMoodViewModel.handleEvent(AboutMoodIntent.LoadMood(moodId))
    }

    LaunchedEffect(Unit) {
        aboutMoodViewModel.sideEffect.collect { effect ->
            when (effect) {
                AboutMoodEffect.MoodDeleted -> {
                    back()
                }
            }
        }
    }

    AboutMoodScreen(
        moodId = moodId,
        state = state,
        back = back,
        onUpdateMoodPressed = onUpdateMoodPressed,
        onAboutMoodIntent = aboutMoodViewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutMoodScreen(
    moodId: Int,
    state: AboutMoodState,
    back: () -> Unit,
    onUpdateMoodPressed: (Int) -> Unit,
    onAboutMoodIntent: (AboutMoodIntent) -> Unit
) {
    var openAlertDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
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
                IconButton(onClick = { onUpdateMoodPressed(moodId) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit mood"
                    )
                }
                IconButton(onClick = { openAlertDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete mood"
                    )
                }
            }
        )


        when (state) {
            AboutMoodState.Missing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Mood entry not found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is AboutMoodState.Content -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.note,
                            style = MaterialTheme.typography.titleLarge
                         )
                        Text(
                            text = state.mood.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (openAlertDialog) {
            DeleteMoodAlert(
                onDelete = {
                    openAlertDialog = false
                    onAboutMoodIntent(AboutMoodIntent.DeleteMood(moodId))
                },
                onDismissRequest = { openAlertDialog = false }
            )
        }
    }
}
