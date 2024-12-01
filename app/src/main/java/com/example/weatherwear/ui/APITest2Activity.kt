package com.example.apitest

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.*
import com.example.weatherwear.data.sample.SampleAIRecommendation
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.ui.MainActivity.Companion.useSample
import com.example.weatherwear.util.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var btnTestAIRecommendation: Button
    private lateinit var btnTestAIRecommendationWithSample: Button

    // Helper 객체
    private lateinit var apiService: ApiService
    private var rwcResponse: RWCResponse? = null // 날씨 정보를 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

        // UI 초기화
        resultTextView = findViewById(R.id.textViewResult)
        btnTestAIRecommendation = findViewById(R.id.btn_testAIrecommendation)
        btnTestAIRecommendationWithSample = findViewById(R.id.btn_testAIrecommendationWithSample)
        // 샘플 데이터 스위칭 버튼 추가
        val btnSwitchSampleMode: Button = findViewById(R.id.btn_switchSampleMode)

        // 샘플 데이터 사용 여부 스위칭
        btnSwitchSampleMode.setOnClickListener {
            useSample = !useSample
            val mode = if (useSample) "샘플 모드" else "실제 데이터 모드"
            Toast.makeText(this, "데이터 모드가 '$mode'로 변경되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // API 초기화
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        // AI 추천 버튼
        btnTestAIRecommendation.setOnClickListener {
            val memberEmail = "jts3531" // 테스트용 이메일
            fetchAIRecommendations(memberEmail)
        }

        // AI 추천 샘플 데이터 버튼
        btnTestAIRecommendationWithSample.setOnClickListener {
            val sampleRecommendations = SampleAIRecommendation.createSampleRecommendations()
            val resultText = buildAIRecommendationDisplayText(sampleRecommendations)
            resultTextView.text = resultText
        }
    }

    // AI 추천 요청 함수
    private fun fetchAIRecommendations(memberEmail: String) {
        // 온도 데이터 추출
        val temperature = rwcResponse?.regionWeather?.weather?.firstOrNull()?.temp

        if (temperature == null) {
            resultTextView.text = "온도 정보를 가져올 수 없습니다."
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // API 호출
                val response = apiService.getAIRecommendation(memberEmail, temperature)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val recommendations = response.body()
                        if (recommendations != null) {
                            // 결과 포맷팅
                            val formattedText = buildAIRecommendationDisplayText(recommendations)
                            resultTextView.text = formattedText
                        } else {
                            resultTextView.text = "추천 데이터를 가져올 수 없습니다."
                        }
                    } else {
                        resultTextView.text = "API 호출 실패: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultTextView.text = "에러 발생: ${e.message}"
                }
            }
        }
    }

    // AI 기반 추천 결과 출력
    private fun buildAIRecommendationDisplayText(recommendations: List<LearnedRecommendation>): String {
        val builder = StringBuilder()
        builder.append("=== AI 기반 추천 결과 ===\n")
        recommendations.forEach { recommendation ->
            builder.append("회원 이메일: ${recommendation.memberEmail}\n")
            builder.append("온도: ${recommendation.temperature} °C\n")

            // JSON 문자열을 List<String>으로 변환
            val optimizedClothingsToList = parseOptimizedClothing(recommendation.optimizedClothing.toString())

            // 추천 의상 출력
            builder.append("추천 의상:\n")
            optimizedClothingsToList.forEach { clothing ->
                builder.append("$clothing\n")
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    /**
     * JSON 형태의 String을 List<String>으로 변환
     * @param jsonString JSON 문자열
     * @return List<String> 변환된 리스트
     */
    fun parseOptimizedClothing(jsonString: String): List<String> {
        val list = mutableListOf<String>()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val type = jsonObject.getString("type")
            val item = jsonObject.getString("item")
            list.add("$type-$item")
        }
        return list
    }
}
