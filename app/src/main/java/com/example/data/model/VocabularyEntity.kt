package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val definition: String,
    val exampleSentence: String,
    val exampleTranslation: String,
    val category: String,
    val isBookmarked: Boolean = false,
    val isLearned: Boolean = false,
    val reviewCount: Int = 0
)
