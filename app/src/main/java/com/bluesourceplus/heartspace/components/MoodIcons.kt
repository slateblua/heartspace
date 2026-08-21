package com.bluesourceplus.heartspace.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.ui.graphics.vector.ImageVector
import com.bluesourceplus.heartspace.data.Mood

fun Mood.icon(): ImageVector {
    return when (this) {
        Mood.SAD -> Icons.Filled.SentimentVeryDissatisfied
        Mood.TIRED -> Icons.Filled.SentimentNeutral
        Mood.STRESSED -> Icons.Filled.SentimentDissatisfied
        Mood.HAPPY -> Icons.Filled.SentimentSatisfied
        Mood.EXCITED -> Icons.Filled.SentimentVerySatisfied
        Mood.UNKNOWN -> Icons.AutoMirrored.Filled.HelpOutline
    }
}
