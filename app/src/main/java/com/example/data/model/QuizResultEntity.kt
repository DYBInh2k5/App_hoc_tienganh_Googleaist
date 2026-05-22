package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val correctCount: Int,
    val totalCount: Int,
    val scorePercent: Int,
    val timestamp: Long = System.currentTimeMillis()
)
