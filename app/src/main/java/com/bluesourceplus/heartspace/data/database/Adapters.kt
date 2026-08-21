package com.bluesourceplus.heartspace.data.database

import com.bluesourceplus.heartspace.data.Mood
import com.bluesourceplus.heartspace.data.MoodModel

fun MoodModel.toEntry(): MoodEntry {
    return MoodEntry(
        id = this.id,
        mood = this.mood.name,
        timestamp = this.timestamp,
        note = this.note,
    )
}

fun MoodEntry.toModel(): MoodModel {
    return MoodModel(
        id = this.id,
        mood = Mood.fromStoredName(this.mood),
        timestamp = this.timestamp,
        note = this.note,
    )
}