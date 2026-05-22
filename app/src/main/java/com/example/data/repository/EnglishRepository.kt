package com.example.data.repository

import com.example.data.database.VocabDao
import com.example.data.database.MessageDao
import com.example.data.database.QuizDao
import com.example.data.model.VocabularyEntity
import com.example.data.model.MessageEntity
import com.example.data.model.QuizResultEntity
import kotlinx.coroutines.flow.Flow

class EnglishRepository(
    private val vocabDao: VocabDao,
    private val messageDao: MessageDao,
    private val quizDao: QuizDao
) {
    val allVocab: Flow<List<VocabularyEntity>> = vocabDao.getAllVocab()
    val bookmarkedVocab: Flow<List<VocabularyEntity>> = vocabDao.getBookmarkedVocab()
    val learnedVocab: Flow<List<VocabularyEntity>> = vocabDao.getLearnedVocab()
    val chatMessages: Flow<List<MessageEntity>> = messageDao.getAllMessages()
    val quizResults: Flow<List<QuizResultEntity>> = quizDao.getAllQuizResults()

    fun getVocabByCategory(category: String): Flow<List<VocabularyEntity>> {
        return vocabDao.getVocabByCategory(category)
    }

    suspend fun insertVocab(vocab: VocabularyEntity) = vocabDao.insert(vocab)

    suspend fun insertVocabList(vocabs: List<VocabularyEntity>) = vocabDao.insertAll(vocabs)

    suspend fun updateBookmark(id: Int, isBookmarked: Boolean) = vocabDao.updateBookmark(id, isBookmarked)

    suspend fun updateLearned(id: Int, isLearned: Boolean) = vocabDao.updateLearned(id, isLearned)

    suspend fun incrementReviewCount(id: Int) = vocabDao.incrementReviewCount(id)

    suspend fun getVocabCount(): Int = vocabDao.getCount()

    // Message interactions
    suspend fun insertMessage(message: MessageEntity) = messageDao.insert(message)

    suspend fun clearChat() = messageDao.clearChat()

    // Quiz interactions
    suspend fun insertQuizResult(result: QuizResultEntity) = quizDao.insertResult(result)

    suspend fun clearQuizHistory() = quizDao.clearHistory()
}
