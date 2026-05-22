package com.example.data.database

import androidx.room.*
import com.example.data.model.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocabulary ORDER BY word ASC")
    fun getAllVocab(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE category = :category ORDER BY word ASC")
    fun getVocabByCategory(category: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE isBookmarked = 1 ORDER BY word ASC")
    fun getBookmarkedVocab(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE isLearned = 1")
    fun getLearnedVocab(): Flow<List<VocabularyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vocab: VocabularyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vocabs: List<VocabularyEntity>)

    @Query("UPDATE vocabulary SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Int, isBookmarked: Boolean)

    @Query("UPDATE vocabulary SET isLearned = :isLearned WHERE id = :id")
    suspend fun updateLearned(id: Int, isLearned: Boolean)

    @Query("UPDATE vocabulary SET reviewCount = reviewCount + 1 WHERE id = :id")
    suspend fun incrementReviewCount(id: Int)

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getCount(): Int
}
