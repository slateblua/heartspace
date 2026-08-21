package com.bluesourceplus.heartspace.data

import androidx.compose.runtime.Immutable

@Immutable
data class MoodModel(
    val id: Int = 0,
    val mood: Mood,
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class Mood(val displayName: String) {
    SAD("Sad"),
    TIRED("Tired"),
    STRESSED("Stressed"),
    HAPPY("Happy"),
    EXCITED("Excited"),
    UNKNOWN("Unknown");

    companion object {
        fun fromStoredName(value: String): Mood {
            return entries.firstOrNull { it.name == value } ?: UNKNOWN
        }
    }
}
