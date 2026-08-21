package com.bluesourceplus.heartspace.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bluesourceplus.heartspace.data.Mood
import com.bluesourceplus.heartspace.data.MoodModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MoodCard(
    moodModel: MoodModel,
    onMoodPressed: (Int) -> Unit,
    onUpdatePressed: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    // Pick a color accent based on mood
    val accentColor = when (moodModel.mood) {
        Mood.HAPPY, Mood.EXCITED -> MaterialTheme.colorScheme.tertiary
        Mood.SAD -> MaterialTheme.colorScheme.error
        Mood.STRESSED, Mood.TIRED, Mood.UNKNOWN -> MaterialTheme.colorScheme.secondary
    }
    val formattedTimestamp = remember(moodModel.timestamp) {
        formatTimestamp(moodModel.timestamp)
    }

    // Animate overflow icon when menu opens
    val rotation by animateFloatAsState(if (showMenu) 90f else 0f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showMenu = true
                    },
                    onTap = { onMoodPressed(moodModel.id) }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular icon with accent background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = moodModel.mood.icon(),
                    contentDescription = "Mood: ${moodModel.mood.displayName}",
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = moodModel.mood.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (moodModel.note.isNotBlank()) {
                    Text(
                        text = moodModel.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = formattedTimestamp,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Overflow button (visible affordance) + Dropdown menu anchored to it
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier
                        .size(40.dp)
                        .semantics {
                            // improves accessibility label for the overflow button
                            contentDescription = "More actions"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(rotation) // subtle rotation when menu open
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .widthIn(min = 160.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Update") },
                        onClick = {
                            // you might show picker in parent; for now call the handler
                            onUpdatePressed(moodModel.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ChangeCircle,
                                contentDescription = "Change mood"
                            )
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            // Close menu and show confirmation dialog
                            showMenu = false
                            showConfirmDelete = true
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showConfirmDelete) {
        DeleteMoodAlert(
            onDelete = {
                onDelete(moodModel.id)
                showConfirmDelete = false
            },
            onDismissRequest = { showConfirmDelete = false }
        )
    }
}

@Composable
fun DeleteMoodAlert(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Delete mood entry") },
        text = { Text("Are you sure you want to delete this mood entry? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                onDelete()
            }) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}


// Helper function to format timestamp
fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
