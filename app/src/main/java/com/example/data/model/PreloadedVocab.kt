package com.example.data.model

object PreloadedVocab {
    val items = listOf(
        // Daily Communication
        VocabularyEntity(
            word = "Appreciate",
            phonetic = "/əˈpriː.ʃi.eɪt/",
            partOfSpeech = "verb",
            definition = "Trân trọng, đánh giá cao, cảm kích",
            exampleSentence = "I really appreciate your help with my English studies.",
            exampleTranslation = "Tôi rất cảm kích sự giúp đỡ của bạn cho việc học tiếng Anh của tôi.",
            category = "Giao tiếp Hàng ngày"
        ),
        VocabularyEntity(
            word = "Apologize",
            phonetic = "/əˈpɒl.ə.dʒaɪz/",
            partOfSpeech = "verb",
            definition = "Xin lỗi (một cách lịch sự, chân thành)",
            exampleSentence = "We apologize for any inconvenience caused today.",
            exampleTranslation = "Chúng tôi xin lỗi vì bất kỳ sự bất tiện nào gây ra hôm nay.",
            category = "Giao tiếp Hàng ngày"
        ),
        VocabularyEntity(
            word = "Sincere",
            phonetic = "/sɪnˈsɪər/",
            partOfSpeech = "adjective",
            definition = "Chân thành, thật lòng",
            exampleSentence = "Please accept my sincere congratulations on your success.",
            exampleTranslation = "Xin vui lòng nhận lời chúc mừng chân thành của tôi về thành công của bạn.",
            category = "Giao tiếp Hàng ngày"
        ),
        VocabularyEntity(
            word = "Empathy",
            phonetic = "/ˈem.pə.θi/",
            partOfSpeech = "noun",
            definition = "Sự thấu cảm, đồng cảm sâu sắc",
            exampleSentence = "Showing empathy is important in building strong relationships.",
            exampleTranslation = "Thể hiện sự thấu cảm là điều quan trọng trong việc xây dựng mối quan hệ bền vững.",
            category = "Giao tiếp Hàng ngày"
        ),
        VocabularyEntity(
            word = "Enthusiastic",
            phonetic = "/ɪnˌθjuː.ziˈæs.tɪk/",
            partOfSpeech = "adjective",
            definition = "Hăng hái, nhiệt tình, say mê",
            exampleSentence = "She is very enthusiastic about learning new foreign languages.",
            exampleTranslation = "Cô ấy vô cùng nhiệt tình với việc học các ngôn ngữ mới ôn.",
            category = "Giao tiếp Hàng ngày"
        ),

        // Workspace
        VocabularyEntity(
            word = "Collaborate",
            phonetic = "/kəˈlæb.ə.reɪt/",
            partOfSpeech = "verb",
            definition = "Hợp tác, cộng tác",
            exampleSentence = "Researchers are collaborating to find a solution to the problem.",
            exampleTranslation = "Các nhà nghiên cứu đang cộng tác để tìm ra giải pháp cho vấn đề.",
            category = "Tiếng Anh Công sở"
        ),
        VocabularyEntity(
            word = "Deadline",
            phonetic = "/ˈded.laɪn/",
            partOfSpeech = "noun",
            definition = "Hạn chót, thời hạn hoàn thành",
            exampleSentence = "The deadline for submitting the project proposal is tomorrow.",
            exampleTranslation = "Hạn chót để nộp đề xuất dự án là vào ngày mai.",
            category = "Tiếng Anh Công sở"
        ),
        VocabularyEntity(
            word = "Prioritize",
            phonetic = "/praɪˈɒr.ɪ.taɪz/",
            partOfSpeech = "verb",
            definition = "Ưu tiên, đặt lên hàng đầu",
            exampleSentence = "You need to prioritize your tasks to save time.",
            exampleTranslation = "Bạn cần phải ưu tiên các nhiệm vụ của mình để tiết kiệm thời gian.",
            category = "Tiếng Anh Công sở"
        ),
        VocabularyEntity(
            word = "Implement",
            phonetic = "/ˈɪm.plɪ.ment/",
            partOfSpeech = "verb",
            definition = "Triển khai, thực thi, áp dụng",
            exampleSentence = "The company decided to implement new security policies.",
            exampleTranslation = "Công ty đã quyết định triển khai các chính sách bảo mật mới.",
            category = "Tiếng Anh Công sở"
        ),
        VocabularyEntity(
            word = "Negotiate",
            phonetic = "/nəˈɡəʊ.ʃi.eɪt/",
            partOfSpeech = "verb",
            definition = "Đàm phán, thương lượng",
            exampleSentence = "We managed to negotiate a lower price for the office supplies.",
            exampleTranslation = "Chúng tôi đã thương lượng được mức giá thấp hơn cho văn phòng phẩm.",
            category = "Tiếng Anh Công sở"
        ),

        // Travel
        VocabularyEntity(
            word = "Reservation",
            phonetic = "/ˌrez.əˈveɪ.ʃən/",
            partOfSpeech = "noun",
            definition = "Sự đặt trước (phòng, bàn, vé)",
            exampleSentence = "I would like to make a reservation for a double room tonight.",
            exampleTranslation = "Tôi muốn đặt trước một phòng đôi cho tối nay.",
            category = "Tiếng Anh Du lịch"
        ),
        VocabularyEntity(
            word = "Itinerary",
            phonetic = "/aɪˈtɪn.ər.ər.i/",
            partOfSpeech = "noun",
            definition = "Lịch trình chuyến đi",
            exampleSentence = "The travel agent provided us with a detailed itinerary.",
            exampleTranslation = "Đại lý du lịch đã cung cấp cho chúng tôi một lịch trình chi tiết.",
            category = "Tiếng Anh Du lịch"
        ),
        VocabularyEntity(
            word = "Destination",
            phonetic = "/ˌdes.tɪˈneɪ.ʃən/",
            partOfSpeech = "noun",
            definition = "Điểm đến, nơi đến",
            exampleSentence = "Halong Bay is a popular tourist destination in Vietnam.",
            exampleTranslation = "Vịnh Hạ Long là một điểm đến du lịch nổi tiếng ở Việt Nam.",
            category = "Tiếng Anh Du lịch"
        ),
        VocabularyEntity(
            word = "Departure",
            phonetic = "/dɪˈpɑː.tʃər/",
            partOfSpeech = "noun",
            definition = "Sự khởi hành, sự cất cánh",
            exampleSentence = "Please check the flight departure board for schedule changes.",
            exampleTranslation = "Vui lòng kiểm tra bảng cất cánh chuyến bay để xem thay đổi lịch trình.",
            category = "Tiếng Anh Du lịch"
        ),
        VocabularyEntity(
            word = "Baggage",
            phonetic = "/ˈbæɡ.ɪdʒ/",
            partOfSpeech = "noun",
            definition = "Hành lý (tương tự luggage)",
            exampleSentence = "Passengers are allowed to carry one piece of hand baggage.",
            exampleTranslation = "Hành khách được phép mang theo một kiện hành lý xách tay.",
            category = "Tiếng Anh Du lịch"
        ),

        // Tech & IT
        VocabularyEntity(
            word = "Algorithm",
            phonetic = "/ˈæl.ɡə.rɪ.ðəm/",
            partOfSpeech = "noun",
            definition = "Thuật toán",
            exampleSentence = "Google uses a complex search algorithm to display results.",
            exampleTranslation = "Google sử dụng một thuật toán tìm kiếm phức tạp để hiển thị kết quả.",
            category = "Công nghệ & IT"
        ),
        VocabularyEntity(
            word = "Database",
            phonetic = "/ˈdeɪ.tə.beɪs/",
            partOfSpeech = "noun",
            definition = "Cơ sở dữ liệu",
            exampleSentence = "Our client data is stored securely in an encrypted database.",
            exampleTranslation = "Dữ liệu khách hàng của chúng tôi được lưu trữ an toàn trong một cơ sở dữ liệu được mã hóa.",
            category = "Công nghệ & IT"
        ),
        VocabularyEntity(
            word = "Vulnerability",
            phonetic = "/ˌvʌl.nər.əˈbɪl.ə.ti/",
            partOfSpeech = "noun",
            definition = "Lỗ hổng bảo mật, điểm yếu",
            exampleSentence = "The security team patched a critical software vulnerability.",
            exampleTranslation = "Đội ngũ bảo mật đã vá một lỗ hổng phần mềm nghiêm trọng.",
            category = "Công nghệ & IT"
        ),
        VocabularyEntity(
            word = "Framework",
            phonetic = "/ˈfreɪm.wɜːk/",
            partOfSpeech = "noun",
            definition = "Bộ khung lập trình, khuôn khổ",
            exampleSentence = "Jetpack Compose is a modern framework for building Android UI.",
            exampleTranslation = "Jetpack Compose là một bộ khung hiện đại để xây dựng giao diện Android.",
            category = "Công nghệ & IT"
        ),
        VocabularyEntity(
            word = "Encryption",
            phonetic = "/ɪnˈkrɪp.ʃən/",
            partOfSpeech = "noun",
            definition = "Sự mã hóa dữ liệu",
            exampleSentence = "Encryption ensures that private messages cannot be intercepted.",
            exampleTranslation = "Mã hóa đảm bảo rằng các tin nhắn riêng tư không thể bị đánh cắp.",
            category = "Công nghệ & IT"
        ),

        // Idioms & Phrases
        VocabularyEntity(
            word = "Piece of cake",
            phonetic = "/piːs əv keɪk/",
            partOfSpeech = "phrase",
            definition = "Dễ như ăn bánh, vô cùng đơn giản",
            exampleSentence = "Don't worry about the English test; it's a piece of cake!",
            exampleTranslation = "Đừng lo lắng về bài kiểm tra tiếng Anh; nó dễ như ăn bánh ấy mà!",
            category = "Thành ngữ & Cụm Từ"
        ),
        VocabularyEntity(
            word = "Break a leg",
            phonetic = "/breɪk ə leɡ/",
            partOfSpeech = "phrase",
            definition = "Chúc may mắn (thường dùng trước khi lên biểu diễn)",
            exampleSentence = "You have your music audition tonight, break a leg!",
            exampleTranslation = "Bạn có buổi thử giọng tối nay, chúc may mắn nhé!",
            category = "Thành ngữ & Cụm Từ"
        ),
        VocabularyEntity(
            word = "Bite the bullet",
            phonetic = "/baɪt ðə ˈbʊl.ɪt/",
            partOfSpeech = "phrase",
            definition = "Cắn răng chịu đựng, đối mặt khó khăn",
            exampleSentence = "I hate dentist visits, but I just have to bite the bullet.",
            exampleTranslation = "Tôi ghét đi khám răng lắm, nhưng tôi đành phải cắn răng đối mặt thôi.",
            category = "Thành ngữ & Cụm Từ"
        ),
        VocabularyEntity(
            word = "Under the weather",
            phonetic = "/ˈʌn.dər ðə ˈweð.ər/",
            partOfSpeech = "phrase",
            definition = "Cảm thấy không khỏe, hơi ốm",
            exampleSentence = "I'm feeling a bit under the weather, so I'll stay home today.",
            exampleTranslation = "Tôi hơi không khỏe một chút, nên hôm nay tôi sẽ ở nhà.",
            category = "Thành ngữ & Cụm Từ"
        ),
        VocabularyEntity(
            word = "Burn the midnight oil",
            phonetic = "/bɜːn ðə ˈmɪd.naɪt ɔɪl/",
            partOfSpeech = "phrase",
            definition = "Thức khuya làm việc, học bài, học nhồi nhét",
            exampleSentence = "I have to burn the midnight oil tonight to prepare the presentation.",
            exampleTranslation = "Đêm nay tôi phải thức khuya cày cuốc học bài để chuẩn bị cho bài thuyết trình.",
            category = "Thành ngữ & Cụm Từ"
        )
    )
}
