package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MessageEntity
import com.example.data.model.VocabularyEntity
import com.example.ui.viewmodel.EnglishViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: EnglishViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(0) } // 0: Học, 1: Flashcard, 2: Trắc Nghiệm, 3: Trợ Lý AI
    val scope = rememberCoroutineScope()
    var showAddWordDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = when (currentTab) {
                                0 -> "BỘ TỪ ĐIỂN CHUẨN"
                                1 -> "LUYỆN GHÉP THẺ"
                                2 -> "ĐÁNH GIÁ CHẤT LƯỢNG"
                                3 -> "HỘI THOẠI GIAO TIẾP"
                                else -> "CHÀO BUỔI SÁNG"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = Color(0xFF6750A4),
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                        Text(
                            text = when (currentTab) {
                                0 -> "Học Tiếng Anh"
                                1 -> "Flashcard"
                                2 -> "Trắc nghiệm"
                                3 -> "Trợ lý AI"
                                else -> "Học Tiếng Anh"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1B20),
                            fontSize = 22.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    if (currentTab == 0) {
                        IconButton(
                            onClick = { showAddWordDialog = true },
                            modifier = Modifier.testTag("add_vocab_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Thêm từ mới",
                                tint = Color(0xFF6750A4)
                            )
                        }
                    }

                    // Exquisite Editorial-theme profile icon badge on header
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEADDFF))
                            .border(BorderStroke(1.dp, Color(0xFFD0BCFF)), CircleShape)
                            .clickable { /* Feedbacks on press */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Cá nhân",
                            tint = Color(0xFF21005D),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFEF7FF)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Trang Học Từ") },
                    label = { Text("Học Từ") },
                    modifier = Modifier.testTag("nav_study_tab")
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Flashcard") },
                    label = { Text("Flashcard") },
                    modifier = Modifier.testTag("nav_flashcard_tab")
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Trắc nghiệm") },
                    label = { Text("Trắc nghiệm") },
                    modifier = Modifier.testTag("nav_quiz_tab")
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(Icons.Default.Face, contentDescription = "Trợ lý AI") },
                    label = { Text("Trợ lý AI") },
                    modifier = Modifier.testTag("nav_ai_tab")
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentTab) {
                0 -> StudyTabScreen(viewModel)
                1 -> FlashcardTabScreen(viewModel)
                2 -> QuizTabScreen(viewModel)
                3 -> AiBuddyTabScreen(viewModel)
            }
        }
    }

    if (showAddWordDialog) {
        AddWordDialog(
            onDismiss = { showAddWordDialog = false },
            onSave = { english, phonetic, pos, meaning, rawExample, rawTranslation, cat ->
                viewModel.addCustomVocab(
                    VocabularyEntity(
                        word = english,
                        phonetic = "/${phonetic.trim().removePrefix("/").removeSuffix("/")}/",
                        partOfSpeech = pos,
                        definition = meaning,
                        exampleSentence = rawExample,
                        exampleTranslation = rawTranslation,
                        category = cat
                    )
                )
                showAddWordDialog = false
            },
            categories = viewModel.categories.filter { it != "Tất cả" }
        )
    }
}

// ==== STUDY TAB MODULE ====

@Composable
fun StudyTabScreen(viewModel: EnglishViewModel) {
    val vocabList by viewModel.vocabList.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val totalCount by viewModel.totalWordsCount.collectAsStateWithLifecycle()
    val learnedCount by viewModel.learnedWordsCount.collectAsStateWithLifecycle()
    val bookmarkedCount by viewModel.bookmarkedWordsCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // High-fidelity "Mục tiêu ngày" Editorial Style banner card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6750A4))
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "MỤC TIÊU NGÀY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.3.sp,
                            color = Color(0xFFEADDFF)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val progressValue = if (totalCount > 0) learnedCount.toFloat() / totalCount.toFloat() else 0f
                        Text(
                            text = "Bạn đã hoàn thành ${(progressValue * 100).toInt()}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Peach/Rose highlight badge representing "Lửa" daily status
                    Surface(
                        color = Color(0xFFFFD8E4),
                        shape = CircleShape,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = "LỬA ${learnedCount * 3 + 5}", // Generates dynamic stylized streak numbers based on progress
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF31111D),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom Editorial track colors matching CSS guidelines exactly (#4F378B as track, #D0BCFF as progress fill)
                val progressValue = if (totalCount > 0) learnedCount.toFloat() / totalCount.toFloat() else 0f
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = Color(0xFFD0BCFF),
                    trackColor = Color(0xFF4F378B)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$learnedCount / $totalCount từ đã thuộc",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEADDFF)
                    )
                    Text(
                        text = if (progressValue >= 0.75f) "Gần xong rồi! 🎉" else "Cố lên, bạn học ơi! ⚡",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEADDFF)
                    )
                }
            }
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Tìm kiếm từ vựng hoặc nghĩa...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear text")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("vocabulary_search_field"),
            shape = RoundedCornerShape(12.dp)
        )

        // Categories Row
        ScrollableTabRow(
            selectedTabIndex = viewModel.categories.indexOf(selectedCategory).coerceAtLeast(0),
            edgePadding = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            divider = {},
            containerColor = Color.Transparent
        ) {
            viewModel.categories.forEach { category ->
                val selected = category == selectedCategory
                Tab(
                    selected = selected,
                    onClick = { viewModel.selectedCategory.value = category },
                    text = {
                        Text(
                            text = category,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Vocabulary List
        if (vocabList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🔍 Chúc mừng hoặc chưa có từ nào!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hãy nhập thêm từ vựng mới hoặc chuyển bộ lọc",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vocabList, key = { it.id }) { vocab ->
                    VocabularyCard(
                        vocab = vocab,
                        onSpeak = { viewModel.speak(vocab.word) },
                        onBookmark = { viewModel.toggleBookmark(vocab.id, vocab.isBookmarked) },
                        onToggleLearned = { viewModel.toggleLearned(vocab.id, vocab.isLearned) }
                    )
                }
            }
        }
    }
}

@Composable
fun VocabularyCard(
    vocab: VocabularyEntity,
    onSpeak: () -> Unit,
    onBookmark: () -> Unit,
    onToggleLearned: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("vocab_card_${vocab.word}"),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            width = 1.2.dp,
            color = if (vocab.isLearned) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color(0xFFCAC4D0)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (vocab.isLearned) Color(0xFFFEF7FF) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Pronounce button matched with Editorial custom circle background
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Color(0xFFF3EDF7),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Phát âm chuẩn",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = vocab.word,
                                fontFamily = FontFamily.Serif,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6750A4)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(${vocab.partOfSpeech})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6750A4),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = vocab.phonetic,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF49454F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Bookmark trigger
                    IconButton(onClick = onBookmark) {
                        Icon(
                            imageVector = if (vocab.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Đánh dấu từ quan trọng",
                            tint = if (vocab.isBookmarked) Color(0xFF6750A4) else Color(0xFF49454F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            // Meaning
            Text(
                text = vocab.definition,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 2.dp)
            )

            // Dynamic expansion block
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 2.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFCAC4D0))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Ví dụ minh họa:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6750A4),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = vocab.exampleSentence,
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1B20),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "↳ ${vocab.exampleTranslation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF49454F)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(vocab.category) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = vocab.isLearned,
                                onCheckedChange = { onToggleLearned() }
                            )
                            Text(
                                text = "Đã thuộc từ này",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==== FLASHCARD TAB MODULE ====

@Composable
fun FlashcardTabScreen(viewModel: EnglishViewModel) {
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val vocabList by viewModel.vocabList.collectAsStateWithLifecycle()
    val activeIndex by viewModel.activeFlashcardIndex.collectAsStateWithLifecycle()

    var isFlipped by remember { mutableStateOf(false) }

    // Reset card flip status when the list or active index changes
    LaunchedEffect(activeIndex, selectedCategory) {
        isFlipped = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Dropdown Topic filter for Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bộ thẻ: $selectedCategory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = {
                    val currentIdx = viewModel.categories.indexOf(selectedCategory)
                    val nextIdx = (currentIdx + 1) % viewModel.categories.size
                    viewModel.selectedCategory.value = viewModel.categories[nextIdx]
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Text("Đổi bộ từ")
            }
        }

        if (vocabList.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không có từ vựng nào trong danh mục này. Hãy đổi danh mục khác hoặc tạo từ mới!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            val adjustedIndex = activeIndex.coerceIn(0, vocabList.size - 1)
            val currentVocab = vocabList[adjustedIndex]

            // TTS triggers helper on auto change
            LaunchedEffect(adjustedIndex) {
                viewModel.incrementReview(currentVocab.id)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Interactive visual card styled as an elegant notebook page
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight(0.75f)
                        .clickable { isFlipped = !isFlipped }
                        .graphicsLayer {
                            // Simple dynamic rotation flip
                            rotationY = if (isFlipped) 180f else 0f
                            cameraDistance = 12f * density
                        }
                        .testTag("flashcard_body"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) Color(0xFFF3EDF7) else Color(0xFFFFFFFF)
                    ),
                    border = BorderStroke(1.2.dp, Color(0xFFCAC4D0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .graphicsLayer {
                                // Prevent inverted text labels on flip back
                                rotationY = if (isFlipped) 180f else 0f
                            },
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title category styled with custom tracking uppercase
                        Text(
                            text = currentVocab.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.3.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6750A4)
                        )

                        // Content Center Layout
                        if (!isFlipped) {
                            // Front details
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currentVocab.word,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 38.sp,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6750A4),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "(${currentVocab.partOfSpeech})",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6750A4),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentVocab.phonetic,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF49454F)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.speak(currentVocab.word) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEADDFF),
                                        contentColor = Color(0xFF21005D)
                                    ),
                                    border = BorderStroke(1.dp, Color(0xFFD0BCFF))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Speak pronunciation"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Phát âm")
                                }
                            }
                        } else {
                            // Back details
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = currentVocab.definition,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color(0xFF1D1B20),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "VÍ DỤ MINH HỌA:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color(0xFF6750A4),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentVocab.exampleSentence,
                                    fontFamily = FontFamily.Serif,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    ),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1D1B20),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                                )
                                Text(
                                    text = "↳ ${currentVocab.exampleTranslation}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF49454F),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Bottom Flip prompt
                        Text(
                            text = "Chạm để lật thẻ 🔄",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF49454F).copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Controllers Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val prev = if (adjustedIndex > 0) adjustedIndex - 1 else vocabList.size - 1
                        viewModel.activeFlashcardIndex.value = prev
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Trở về câu trước")
                }

                Text(
                    text = "Thẻ ${adjustedIndex + 1} / ${vocabList.size}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                IconButton(
                    onClick = {
                        val next = (adjustedIndex + 1) % vocabList.size
                        viewModel.activeFlashcardIndex.value = next
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Chuyển sang thẻ kế")
                }
            }
        }
    }
}

// ==== QUIZ TAB MODULE ====

@Composable
fun QuizTabScreen(viewModel: EnglishViewModel) {
    val isQuizActive by viewModel.isQuizActive.collectAsStateWithLifecycle()
    val isQuizFinished by viewModel.isQuizFinished.collectAsStateWithLifecycle()
    val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val currentQuestionIdx by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedAnsIdx by viewModel.selectedAnswerIndex.collectAsStateWithLifecycle()
    val answeredCorrectly by viewModel.answeredCorrectly.collectAsStateWithLifecycle()
    val correctAnswersCount by viewModel.quizCorrectAnswersCount.collectAsStateWithLifecycle()
    val resultsList by viewModel.quizResults.collectAsStateWithLifecycle()

    if (!isQuizActive) {
        // Welcome Quiz Panel & History logs
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Bài kiểm tra nhanh tiếng Anh 📝",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cùng kiểm tra từ vựng để củng cố phản xạ ghi nhớ của bạn ngay.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Start Session Section
            Text(
                text = "CHỌN CHỦ ĐỀ TRẮC NGHIỆM",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Topics Grid / list selecting
            val quizTopics = listOf("Tất cả", "Giao tiếp Hàng ngày", "Tiếng Anh Công sở", "Tiếng Anh Du lịch", "Công nghệ & IT", "Thành ngữ & Cụm Từ")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quizTopics.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { topic ->
                            Button(
                                onClick = { viewModel.startQuiz(topic) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("quiz_topic_button_$topic"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(topic, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // History Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LỊCH SỬ ĐÃ PHẢN XẠ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (resultsList.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Text("Xóa lịch sử")
                    }
                }
            }

            // History Records list
            if (resultsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bạn chưa hoàn thành bài test nào. Hãy bắt đầu chiến nhé!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(resultsList) { res ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Chủ đề: ${res.category}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Số câu đúng: ${res.correctCount}/${res.totalCount}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    text = "${res.scorePercent}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.scorePercent >= 80) Color(0xFF2E7D32) else if (res.scorePercent >= 50) Color(0xFFE65100) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else if (isQuizFinished) {
        // Quiz Congratulations Game Over area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎉 Hoàn Thành!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "ĐIỂM SỐ CỦA BẠN", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$correctAnswersCount / ${quizQuestions.size}",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val percent = if (quizQuestions.isNotEmpty()) (correctAnswersCount * 100) / quizQuestions.size else 0
                    Text(
                        text = "Đạt tỉ lệ chính xác $percent%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.quitQuiz() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("quiz_finish_back_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Quay về màn hình")
            }
        }
    } else {
        // Active Quiz gameplay
        val currentQuestion = quizQuestions[currentQuestionIdx]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header stats & exit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.quitQuiz() }) {
                    Icon(Icons.Default.Close, contentDescription = "Thoát trắc nghiệm")
                }

                Text(
                    text = "Câu ${currentQuestionIdx + 1} / ${quizQuestions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Đúng: $correctAnswersCount",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            // Visual linear progress indicator
            val progress = (currentQuestionIdx.toFloat()) / quizQuestions.size.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Current question visual detail card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hãy tìm nghĩa chính xác cho từ:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentQuestion.word,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "(${currentQuestion.partOfSpeech})   ${currentQuestion.phonetic}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // 4 dynamic choices buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentQuestion.options.forEachIndexed { idx, option ->
                    val isThisSelected = selectedAnsIdx == idx
                    val isThisCorrect = idx == currentQuestion.correctOptionIndex
                    
                    val bColor = when {
                        selectedAnsIdx == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        isThisCorrect -> Color(0xFFE8F5E9) // correct glow green
                        isThisSelected -> Color(0xFFFFEBEE) // wrong selected glow red
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    }

                    val fColor = when {
                        selectedAnsIdx == null -> MaterialTheme.colorScheme.onSurface
                        isThisCorrect -> Color(0xFF2E7D32)
                        isThisSelected -> Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }

                    val border = if (isThisSelected || (selectedAnsIdx != null && isThisCorrect)) {
                        BorderStroke(1.5.dp, if (isThisCorrect) Color(0xFF2E7D32) else Color(0xFFC62828))
                    } else null

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = selectedAnsIdx == null) { viewModel.submitAnswer(idx) }
                            .testTag("quiz_choice_${idx}"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = bColor),
                        border = border
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                fontWeight = if (isThisSelected || (selectedAnsIdx != null && isThisCorrect)) FontWeight.Bold else FontWeight.Normal,
                                color = fColor,
                                modifier = Modifier.weight(1f)
                            )

                            if (selectedAnsIdx != null) {
                                if (isThisCorrect) {
                                    Icon(Icons.Default.Check, contentDescription = "Đúng", tint = Color(0xFF2E7D32))
                                } else if (isThisSelected) {
                                    Icon(Icons.Default.Close, contentDescription = "Sai", tint = Color(0xFFC62828))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action: Next Question
            AnimatedVisibility(
                visible = selectedAnsIdx != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (answeredCorrectly == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (answeredCorrectly == true) "✓ Hoàn toàn chính xác!" else "✗ Sai rồi! Đáp án đúng: ${currentQuestion.correctDefinition}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (answeredCorrectly == true) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ví dụ: ${currentQuestion.exampleSentence}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tiếp theo")
                    }
                }
            }
        }
    }
}

// ==== AI BUDDY TAB MODULE ====

@Composable
fun AiBuddyTabScreen(viewModel: EnglishViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val currentScenario by viewModel.currentScenario.collectAsStateWithLifecycle()
    var inputDraft by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Scenarios Horizontal Choice Strip
        Text(
            text = "Lựa chọn bối cảnh giao tiếp:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val scenarios = listOf(
                "Casual" to "💬 Tán Gẫu",
                "Airport" to "✈️ Sân Bay",
                "Restaurant" to "🍽️ Nhà Hàng",
                "Interview" to "💼 Phỏng Vấn"
            )

            scenarios.forEach { (scKey, scLabel) ->
                val selected = scKey == currentScenario
                Button(
                    onClick = { viewModel.changeScenario(scKey) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("scenario_trigger_$scKey"),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(scLabel, fontSize = 11.sp, maxLines = 1, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Messages area scrollable list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    message = msg,
                    onSpeak = { viewModel.speak(msg.text) }
                )
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gửi câu hỏi tới EduBuddy...", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Deck bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputDraft,
                onValueChange = { inputDraft = it },
                placeholder = { Text("Hãy nói bằng tiếng Anh, tôi nãy chữa lỗi giúp bạn...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_input_text_field"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Button(
                onClick = {
                    if (inputDraft.isNotBlank()) {
                        viewModel.sendMessage(inputDraft.trim())
                        inputDraft = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(54.dp)
                    .testTag("ai_send_button"),
                enabled = !isAiLoading && inputDraft.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Gửi")
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: MessageEntity,
    onSpeak: () -> Unit
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            if (!isUser) {
                // Sender label
                Text(
                    text = "🤖 EduBuddy Teacher",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
                )
            }

            // Main bubble body
            Box(
                modifier = Modifier
                    .background(
                        color = if (isUser) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 12.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )

                        if (!isUser) {
                            IconButton(
                                onClick = onSpeak,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Speak response",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Collapsible AI helper details (translation and corrections)
                    if (!isUser) {
                        if (!message.translation.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dịch: ${message.translation}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        if (!message.grammarAnalysis.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "🔍 Phân tích ngữ pháp & Gợi ý:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = message.grammarAnalysis,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==== UTILITIES AND DIALOGS ====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String) -> Unit,
    categories: List<String>
) {
    var english by remember { mutableStateOf("") }
    var phonetic by remember { mutableStateOf("") }
    var partOfSpeech by remember { mutableStateOf("noun") }
    var meaning by remember { mutableStateOf("") }
    var exampleSentence by remember { mutableStateOf("") }
    var exampleTranslation by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Giao tiếp Hàng ngày") }

    var expandedSelect by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Thêm từ vựng mới 📓",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Input fields
                OutlinedTextField(
                    value = english,
                    onValueChange = { english = it },
                    label = { Text("Từ tiếng Anh (Ví dụ: Innovation)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_english_field")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phonetic,
                        onValueChange = { phonetic = it },
                        label = { Text("Cách đọc/Phát âm (IPA)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )

                    // Part of speech trigger
                    Box(modifier = Modifier.weight(0.8f)) {
                        val posList = listOf("noun", "verb", "adjective", "adverb", "phrase")
                        var pExpanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { pExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(partOfSpeech, fontSize = 12.sp)
                        }
                        DropdownMenu(expanded = pExpanded, onDismissRequest = { pExpanded = false }) {
                            posList.forEach { pos ->
                                DropdownMenuItem(
                                    text = { Text(pos) },
                                    onClick = {
                                        partOfSpeech = pos
                                        pExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Nghĩa tiếng Việt") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_dialog_meaning_field")
                )

                OutlinedTextField(
                    value = exampleSentence,
                    onValueChange = { exampleSentence = it },
                    label = { Text("Ví dụ tiếng Anh mẫu") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = exampleTranslation,
                    onValueChange = { exampleTranslation = it },
                    label = { Text("Dịch nghĩa ví dụ") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedSelect = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chủ đề: $category")
                    }
                    DropdownMenu(
                        expanded = expandedSelect,
                        onDismissRequest = { expandedSelect = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedSelect = false
                                }
                            )
                        }
                    }
                }

                // Action controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (english.isNotBlank() && meaning.isNotBlank()) {
                                onSave(english.trim(), phonetic, partOfSpeech, meaning.trim(), exampleSentence.trim(), exampleTranslation.trim(), category)
                            }
                        },
                        enabled = english.isNotBlank() && meaning.isNotBlank(),
                        modifier = Modifier.testTag("add_dialog_save_button")
                    ) {
                        Text("Lưu từ")
                    }
                }
            }
        }
    }
}
