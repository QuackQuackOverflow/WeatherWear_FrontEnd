package com.example.weatherwear.helpers

import android.content.Context
import android.widget.LinearLayout
import android.widget.Toast
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.ClothingRecommendation
import com.example.weatherwear.data.model.LearnedRecommendation
import com.example.weatherwear.data.model.RWCResponse
import com.example.weatherwear.data.sample.SampleAIRecommendation
import com.example.weatherwear.helpers.MainUIHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * AI 추천 요청 헬퍼 함수
 */
fun requestAIRecommendation(
    context: Context,
    useSample: Boolean,
    mainUIHelper: MainUIHelper,
    apiService: ApiService,
    clothesLinearLayout: LinearLayout,
    populateClothingRecommendations: (List<ClothingRecommendation>) -> Unit
) {
    if (useSample) {
        // 샘플 데이터 사용
        val sampleRecommendations = SampleAIRecommendation.createSampleRecommendations()
        val finalFromOfAIRecommendation =
            transformToClothingRecommendations(sampleRecommendations, mainUIHelper)
        populateClothingRecommendations(finalFromOfAIRecommendation)
    } else {
        // 실제 AI 추천 데이터 가져오기
        val loginPrefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val memberEmail = loginPrefs.getString("memberEmail", null)
        val userType = loginPrefs.getString("userType", null)

        if (memberEmail == null || userType == null) {
            Toast.makeText(context, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getAIRecommendation(memberEmail)
                if (response.isSuccessful) {
                    val aiRecommendations = response.body()
                    aiRecommendations?.let {
                        val finalFromOfAIRecommendation =
                            transformToClothingRecommendations(it, mainUIHelper)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "AI 추천 데이터를 성공적으로 가져왔습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            populateClothingRecommendations(finalFromOfAIRecommendation)
                        }
                    } ?: run {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "AI 추천 데이터를 가져올 수 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "AI 추천 요청 실패: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "오류 발생: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}

/**
 * LearnedRecommendation 리스트를 ClothingRecommendation 리스트로 변환
 */
fun transformToClothingRecommendations(
    aiRecommendations: List<LearnedRecommendation>,
    mainUIHelper: MainUIHelper
): List<ClothingRecommendation> {
    return aiRecommendations.map { learnedRecommendation ->
        val recommendationsList =
            mainUIHelper.parseOptimizedClothing(learnedRecommendation.optimizedClothing)
        ClothingRecommendation(
            temperature = "${learnedRecommendation.temperature.toInt()}°C",
            recommendations = recommendationsList
        )
    }
}
