package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.database.AppDatabase
import com.example.data.model.MessageEntity
import com.example.data.model.PreloadedVocab
import com.example.data.model.QuizResultEntity
import com.example.data.model.VocabularyEntity
import com.example.data.repository.EnglishRepository
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.GenerationConfig
import com.example.network.Part
import com.example.network.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class EnglishViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: EnglishRepository
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    // State boundaries
    val categories = listOf("Tất cả", "Giao tiếp Hàng ngày", "Tiếng Anh Công sở", "Tiếng Anh Du lịch", "Công nghệ & IT", "Thành ngữ & Cụm Từ")
    val selectedCategory = MutableStateFlow("Tất cả")
    val searchQuery = MutableStateFlow("")

    // List of vocabulary items observed from DB
    val allVocabList: StateFlow<List<VocabularyEntity>>
    
    // Filtered list based on category and search query
    val vocabList: StateFlow<List<VocabularyEntity>>

    // Bookmarked items
    val bookmarkedList: StateFlow<List<VocabularyEntity>>

    // Learned count & stats
    val totalWordsCount = MutableStateFlow(0)
    val learnedWordsCount = MutableStateFlow(0)
    val bookmarkedWordsCount = MutableStateFlow(0)

    // Flashcard active index
    val activeFlashcardIndex = MutableStateFlow(0)

    // Quiz mode state
    val selectedQuizCategory = MutableStateFlow("Giao tiếp Hàng ngày")
    val isQuizActive = MutableStateFlow(false)
    val quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val answeredCorrectly = MutableStateFlow<Boolean?>(null)
    val quizCorrectAnswersCount = MutableStateFlow(0)
    val isQuizFinished = MutableStateFlow(false)

    // Past Quiz results
    val quizResults: StateFlow<List<QuizResultEntity>>

    // AI Conversations
    val chatMessages: StateFlow<List<MessageEntity>>
    val isAiLoading = MutableStateFlow(false)
    val currentScenario = MutableStateFlow("Casual") // Casual, Airport, Restaurant, Interview
    val currentScenarioGreeting = mapOf(
        "Casual" to "Hi there! I'm your AI English companion. How is your day today? Let's practice English!",
        "Airport" to "Hello! Welcome to Galaxy Airport. I am your gate agent. May I see your passport and baggage for check-in?",
        "Restaurant" to "Welcome to the Golden Grill! I am your host tonight. Are you ready to order, or do you have any questions about the menu?",
        "Interview" to "Hello! Welcome to tech company HR department. Let's begin the English simulation interview. Start by introducing yourself."
    )
    val scenarioPrompts = mapOf(
        "Casual" to "Bạn là một trợ lý dạy tiếng Anh thân thiện có tên 'EduBuddy'. Hãy tán gẫu bằng tiếng Anh thân mật với học viên. Nhớ phân tích câu viết của họ.",
        "Airport" to "Bạn là một nhân viên check-in tại quầy thủ tục sân bay quốc tế. Hãy thảo luận về vé máy bay, hộ chiếu, hành lý bằng tiếng Anh thực tế với học viên.",
        "Restaurant" to "Bạn là người phục vụ bàn (waiter) tại nhà hàng Âu Mỹ sang trọng sân vườn. Hãy hỏi han món ăn, thức uống, yêu cầu hóa đơn bằng tiếng Anh với học viên.",
        "Interview" to "Bạn là Trưởng phòng tuyển dụng Nhân sự (HR Intervewer) tại công ty phần mềm công nghệ cao. Bạn phỏng vấn bằng tiếng Anh để kiểm tra khả năng và kinh nghiệm."
    )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EnglishRepository(
            vocabDao = database.vocabDao(),
            messageDao = database.messageDao(),
            quizDao = database.quizDao()
        )

        allVocabList = repository.allVocab.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        quizResults = repository.quizResults.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        chatMessages = repository.chatMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedList = repository.bookmarkedVocab.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Dynamically combine Category, Search Query, and dbList to emit the active list
        vocabList = combine(allVocabList, selectedCategory, searchQuery) { list, cat, query ->
            var result = list
            if (cat != "Tất cả") {
                result = result.filter { it.category == cat }
            }
            if (query.isNotBlank()) {
                result = result.filter {
                    it.word.contains(query, ignoreCase = true) || 
                    it.definition.contains(query, ignoreCase = true)
                }
            }
            result
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Setup preloaded data & stats monitoring
        viewModelScope.launch {
            val count = repository.getVocabCount()
            if (count == 0) {
                repository.insertVocabList(PreloadedVocab.items)
            }
            updateStats()
        }

        // Initialize Text-to-Speech
        tts = TextToSpeech(application, this)

        // Observe and update count stats
        viewModelScope.launch {
            allVocabList.collect {
                updateStats()
            }
        }

        // Setup Default Welcome Message inside Chat if empty
        viewModelScope.launch {
            delay(500) // wait for database
            val messages = chatMessages.first()
            if (messages.isEmpty()) {
                initScenario(currentScenario.value)
            }
        }
    }

    private fun updateStats() {
        val list = allVocabList.value
        totalWordsCount.value = list.size
        learnedWordsCount.value = list.count { it.isLearned }
        bookmarkedWordsCount.value = list.count { it.isBookmarked }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsInitialized = true
            }
        }
    }

    fun speak(text: String) {
        if (isTtsInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.e("TTS", "TextToSpeech is not initialized or not supported on this device.")
        }
    }

    override fun onCleared() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onCleared()
    }

    // Vocabulary & Bookmark Toggles
    fun toggleBookmark(id: Int, isBookmarked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBookmark(id, !isBookmarked)
        }
    }

    fun toggleLearned(id: Int, isLearned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLearned(id, !isLearned)
        }
    }

    fun incrementReview(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.incrementReviewCount(id)
        }
    }

    fun addCustomVocab(vocab: VocabularyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertVocab(vocab)
        }
    }

    // Quiz Manager
    fun startQuiz(categoryName: String) {
        selectedQuizCategory.value = categoryName
        viewModelScope.launch(Dispatchers.Default) {
            // Get words from this specific category
            val list = allVocabList.value.filter { categoryName == "Tất cả" || it.category == categoryName }
            if (list.size < 4) {
                // If not enough words, fall back to all items
                generateQuizQuestions(allVocabList.value, categoryName)
            } else {
                generateQuizQuestions(list, categoryName)
            }
        }
    }

    private fun generateQuizQuestions(vocabPool: List<VocabularyEntity>, categoryName: String) {
        val totalQuestions = minOf(10, vocabPool.size)
        val shuffledPool = vocabPool.shuffled()
        val questionsList = ArrayList<QuizQuestion>()

        for (i in 0 until totalQuestions) {
            val correctWord = shuffledPool[i]
            // Pick 3 wrong options
            val wrongOptions = vocabPool.filter { it.id != correctWord.id }
                .shuffled()
                .take(3)
                .map { it.definition }

            val allOptions = (wrongOptions + correctWord.definition).shuffled()
            val correctIndex = allOptions.indexOf(correctWord.definition)

            questionsList.add(
                QuizQuestion(
                    word = correctWord.word,
                    partOfSpeech = correctWord.partOfSpeech,
                    phonetic = correctWord.phonetic,
                    correctDefinition = correctWord.definition,
                    exampleSentence = correctWord.exampleSentence,
                    options = allOptions,
                    correctOptionIndex = correctIndex,
                    vocabularyId = correctWord.id
                )
            )
        }

        quizQuestions.value = questionsList
        currentQuestionIndex.value = 0
        selectedAnswerIndex.value = null
        answeredCorrectly.value = null
        quizCorrectAnswersCount.value = 0
        isQuizFinished.value = false
        isQuizActive.value = true
    }

    fun submitAnswer(optionIndex: Int) {
        if (selectedAnswerIndex.value != null) return // Already answered
        
        selectedAnswerIndex.value = optionIndex
        val activeQuestion = quizQuestions.value[currentQuestionIndex.value]
        val isCorrect = optionIndex == activeQuestion.correctOptionIndex
        answeredCorrectly.value = isCorrect

        if (isCorrect) {
            quizCorrectAnswersCount.value += 1
            // Mark as learned dynamically & increment review statistic!
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateLearned(activeQuestion.vocabularyId, true)
                repository.incrementReviewCount(activeQuestion.vocabularyId)
            }
        }
    }

    fun nextQuestion() {
        val nextIdx = currentQuestionIndex.value + 1
        if (nextIdx < quizQuestions.value.size) {
            currentQuestionIndex.value = nextIdx
            selectedAnswerIndex.value = null
            answeredCorrectly.value = null
        } else {
            // Save results
            saveQuizResult()
            isQuizFinished.value = true
        }
    }

    private fun saveQuizResult() {
        val total = quizQuestions.value.size
        val correct = quizCorrectAnswersCount.value
        val percent = if (total > 0) (correct * 100) / total else 0
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertQuizResult(
                QuizResultEntity(
                    category = selectedQuizCategory.value,
                    correctCount = correct,
                    totalCount = total,
                    scorePercent = percent
                )
            )
        }
    }

    fun quitQuiz() {
        isQuizActive.value = false
        isQuizFinished.value = false
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearQuizHistory()
        }
    }

    // AI Conversations Engine
    fun changeScenario(scenario: String) {
        currentScenario.value = scenario
        viewModelScope.launch {
            repository.clearChat()
            initScenario(scenario)
        }
    }

    private suspend fun initScenario(scenario: String) {
        val greeting = currentScenarioGreeting[scenario] ?: "Hello! Let's practice English."
        repository.insertMessage(
            MessageEntity(
                sender = "ai",
                text = greeting,
                translation = when (scenario) {
                    "Casual" -> "Chào bạn! Tôi là người bạn đồng hành tiếng Anh AI của bạn. Ngày hôm nay của bạn thế nào? Cùng luyện tiếng Anh nhé!"
                    "Airport" -> "Xin chào! Chào mừng tới Sân bay Galaxy. Tôi là nhân viên làm thủ tục. Tôi có thể xem hộ chiếu và hành lý gửi của bạn không?"
                    "Restaurant" -> "Chào mừng quý khách đến với Golden Grill! Tôi là phục vụ bàn tối nay. Quý khách đã sẵn sàng gọi món chưa, hay cần xem thêm thực đơn?"
                    "Interview" -> "Xin chào! Chào mừng bạn đến với văn phòng nhân sự công ty công nghệ. Chúng ta sẽ bắt đầu phỏng vấn giả định nhé. Bạn hãy tự giới thiệu bản thân."
                    else -> "Xin chào! Hãy cùng nói tiếng Anh nhé."
                },
                grammarAnalysis = "Chào mừng bạn! Đây là câu mở đầu của cuộc đối thoại. Hãy bắt đầu nhập câu trả lời tiếng Anh ở ô phía dưới để luyện tập.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Log user message
            repository.insertMessage(
                MessageEntity(
                    sender = "user",
                    text = userText,
                    timestamp = System.currentTimeMillis()
                )
            )
            isAiLoading.value = true

            // Gather full conversation history as prompt content
            val messages = chatMessages.value
            val historyContent = messages.map { msg ->
                if (msg.sender == "user") {
                    "Learner: ${msg.text}"
                } else {
                    "AI Buddy: ${msg.text}"
                }
            }.joinToString("\n")

            val activeScenario = currentScenario.value
            val systemContext = systemPrompt(activeScenario)

            try {
                val fullAiPrompt = """
                    Conversation History so far:
                    $historyContent
                    
                    Learner's latest message: "$userText"
                    
                    Respond strictly within the role details. Offer corrections in Vietnamese for the Learner's English issues or unnatural phrasing. Provide your response as a direct JSON object containing fields exactly:
                    {
                      "reply": "your conversation reply in English",
                      "grammarAnalysis": "your helpful grammar/vocabulary correction feedback in Vietnamese, detailing any errors or praising if written beautifully",
                      "translation": "Vietnamese translation of your 'reply' field"
                    }
                    Ensure output is a valid JSON. Do not write any markdown code walls except the raw JSON itself.
                """.trimIndent()

                val apiRequest = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = fullAiPrompt)))
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemContext))),
                    generationConfig = GenerationConfig(
                        temperature = 0.7f,
                        responseMimeType = "application/json"
                    )
                )

                val apiKey = BuildConfig.GEMINI_API_KEY
                
                withContext(Dispatchers.IO) {
                    val apiResponse = RetrofitClient.service.generateContent(apiKey, apiRequest)
                    val jsonText = apiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    
                    if (jsonText != null) {
                        try {
                            // Parse JSON safely using moshi custom parser
                            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                            val adapter = moshi.adapter(GeminiJsonResponse::class.java)
                            val cleanedJsonText = cleanJsonString(jsonText)
                            val parsed = adapter.fromJson(cleanedJsonText)
                            
                            if (parsed != null) {
                                repository.insertMessage(
                                    MessageEntity(
                                        sender = "ai",
                                        text = parsed.reply,
                                        translation = parsed.translation,
                                        grammarAnalysis = parsed.grammarAnalysis,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            } else {
                                fallbackModelSave(jsonText)
                            }
                        } catch (e: Exception) {
                            Log.e("JSON_FAIL", "Failed parsing JSON: $jsonText", e)
                            fallbackModelSave(jsonText)
                        }
                    } else {
                        repository.insertMessage(
                            MessageEntity(
                                sender = "ai",
                                text = "I am sorry, I couldn't process that response. Let's try saying something else!",
                                translation = "Rất tiếc, tôi không thể xử lý câu trả lời này. Hãy thử nói câu khác xem sao!",
                                grammarAnalysis = "Lỗi kết nối hoặc xử lý nội dung từ Gemini.",
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("AI_ERROR", "Error speaking to Gemini API", e)
                repository.insertMessage(
                    MessageEntity(
                        sender = "ai",
                        text = "Oh dear, my translation core is experiencing issues right now. Could you please send that again?",
                        translation = "Ôi, lõi kết nối AI đang gặp sự cố nhỏ. Anh chị vui lòng gửi lại nhé!",
                        grammarAnalysis = "Vấn đề kỹ thuật: ${e.message}. Hãy kiểm tra API key trong AI Studio Secrets panel.",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } finally {
                isAiLoading.value = false
            }
        }
    }

    private suspend fun fallbackModelSave(text: String) {
        // Safe regex fallback if string isn't standard JSON or failed to parse
        repository.insertMessage(
            MessageEntity(
                sender = "ai",
                text = text,
                translation = "Nhấp để dịch hoặc phản hồi thủ công.",
                grammarAnalysis = "Không phân tích được định dạng JSON chuẩn: $text",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private fun cleanJsonString(input: String): String {
        return input.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    private fun systemPrompt(scenario: String): String {
        val scenarioContext = scenarioPrompts[scenario] ?: "Tán gẫu bằng tiếng Anh."
        return """
            You are an English Conversation Buddy system designed to help Vietnamese native speakers learn and practice conversational English.
            Primary scenario style to roleplay: $scenarioContext
            
            Strict response format instructions:
            You must ALWAYS reply in a valid raw JSON format matching this EXACT schema:
            {
              "reply": "Write your natural conversation response in English. Keep it under 2 or 3 brief sentences to maintain an easy back-and-forth flow.",
              "grammarAnalysis": "Thảo luận và chữa lỗi cho câu nói vừa rồi của học viên bằng tiếng Việt cực kỳ chi tiết, chỉ ra lỗi sai ngữ pháp, cấu trúc hay chưa tự nhiên và đề xuất câu viết tốt hơn. Nếu học viên viết hoàn toàn chính xác và tự nhiên, hãy viết lời khen tích cực và súc tích.",
              "translation": "Dịch trọn vẹn câu trả lời tiếng Anh ở trường 'reply' sang tiếng Việt tự nhiên nhất."
            }
            Do not enclose in markdown block quotes. Respond only with the JSON object.
        """.trimIndent()
    }
}

// Data holder class for JSON response validation
data class GeminiJsonResponse(
    val reply: String,
    val grammarAnalysis: String,
    val translation: String
)

data class QuizQuestion(
    val word: String,
    val partOfSpeech: String,
    val phonetic: String,
    val correctDefinition: String,
    val exampleSentence: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val vocabularyId: Int
)
