package com.example.weatherwear.data.sample

import com.example.weatherwear.data.model.LearnedRecommendation

object SampleAIRecommendation {

    /**
     * 샘플 AI 추천 데이터를 생성하는 함수
     * @return List<LearnedRecommendation>
     */
    fun createSampleRecommendations(): List<LearnedRecommendation> {
        return listOf(
            LearnedRecommendation(
                memberEmail = "user1@example.com",
                temperature = 10.0,
                optimizedClothing = """["자켓", "청바지", "스카프"]"""
            ),
            LearnedRecommendation(
                memberEmail = "user2@example.com",
                temperature = 5.0,
                optimizedClothing = """["패딩", "기모 바지", "목도리"]"""
            ),
            LearnedRecommendation(
                memberEmail = "user3@example.com",
                temperature = -2.0,
                optimizedClothing = """["두꺼운 패딩", "기모 내복", "장갑", "털모자"]"""
            )
        )
    }
}
