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
                optimizedClothing = "[{\"type\":\"아우터\",\"item\":\"코트\"},{\"type\":\"상의\",\"item\":\"맨투맨\"},{\"type\":\"하의\",\"item\":\"청바지\"}]"
            ),
            LearnedRecommendation(
                memberEmail = "user2@example.com",
                temperature = 5.0,
                optimizedClothing = "[{\"type\":\"아우터\",\"item\":\"코트\"},{\"type\":\"상의\",\"item\":\"맨투맨\"},{\"type\":\"하의\",\"item\":\"청바지\"}]"
            ),
            LearnedRecommendation(
                memberEmail = "user3@example.com",
                temperature = -2.0,
                optimizedClothing = "[{\"type\":\"아우터\",\"item\":\"코트\"},{\"type\":\"상의\",\"item\":\"맨투맨\"},{\"type\":\"하의\",\"item\":\"청바지\"}]"
            )
        )
    }
}
