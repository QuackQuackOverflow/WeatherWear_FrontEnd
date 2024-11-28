package com.example.apitest

import android.net.http.HttpException
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.api.getRWC
import com.example.weatherwear.data.model.*
import com.example.weatherwear.data.sample.SampleAIRecommendation
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.ui.MainActivity
import com.example.weatherwear.util.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var getRegionAndWeatherCustomButton: Button
    private lateinit var editTextNx: EditText
    private lateinit var editTextNy: EditText
    private lateinit var buttonSubmitReview: Button
    private lateinit var btnTestAIRecommendation : Button
    private lateinit var btnTestAIRecommendationWithSample : Button

    /**
     * 샘플 데이터를 테스트하기 위한 버튼
     */
    private lateinit var getSampleDataButton: Button

    // Helper 객체
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

        buttonSubmitReview = findViewById(R.id.buttonSubmitReview)

        // UI 초기화
        resultTextView = findViewById(R.id.textViewResult)
        getRegionAndWeatherCustomButton = findViewById(R.id.buttonGetRegionAndWeather_Custom)
        editTextNx = findViewById(R.id.editTextNx)
        editTextNy = findViewById(R.id.editTextNy)
        btnTestAIRecommendation = findViewById(R.id.btn_testAIrecommendation)
        // AI 추천 샘플 데이터 버튼 초기화
        btnTestAIRecommendationWithSample = findViewById(R.id.btn_testAIrecommendationWithSample)

        /**
         * useSample을 스위칭하기 위한 버튼
         */
        val useSampleButton = findViewById<Button>(R.id.btn_switchSampleMode)
        useSampleButton.setOnClickListener {
            // MainActivity의 useSample 값을 토글
            MainActivity.useSample = !MainActivity.useSample

            // 상태에 따라 버튼 텍스트 변경
            if (MainActivity.useSample) {
                Toast.makeText(this, "샘플 데이터를 활성화했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "샘플 데이터를 비활성화했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * 샘플 데이터를 위한 버튼 초기화
         */
        getSampleDataButton = findViewById(R.id.buttonGetSampleData)
        // API 초기화
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        // 사용자 입력 GPS 기반 지역 및 날씨 정보 요청
        getRegionAndWeatherCustomButton.setOnClickListener {
            val nxText = editTextNx.text.toString()
            val nyText = editTextNy.text.toString()

            if (nxText.isEmpty() || nyText.isEmpty()) {
                resultTextView.text = "nx와 ny 값을 모두 입력하세요."
                return@setOnClickListener
            }

            val nx = nxText.toIntOrNull()
            val ny = nyText.toIntOrNull()

            if (nx == null || ny == null) {
                resultTextView.text = "nx와 ny 값은 숫자로 입력해야 합니다."
                return@setOnClickListener
            }

            // API 호출
            fetchRegionAndWeather(nx, ny)
        }
        // 샘플 리뷰 데이터 생성하여 전송
        buttonSubmitReview.setOnClickListener {
            // Review 객체 생성
            val review = Review(
                memberEmail = "jts3531",   // 회원 이메일
                evaluationScore = -1,      // 평가 점수
                temp = 5.0                 // 어떤 기온에 그 옷을 입었는지
            )
            // Review 전송
            submitReviewAndShowToast(review)
        }
        // AI추천 버튼 ( 파라미터 : jts3531 )
        btnTestAIRecommendation.setOnClickListener {
            val memberEmail = "jts3531" // 테스트용 이메일
            fetchAIRecommendations(memberEmail)
        }


        /**
         * 샘플 데이터를 출력하는 버튼
         */
        getSampleDataButton.setOnClickListener {
            val sampleRWCResponse = SampleRWC.createSampleRWCResponse()
            val resultText = buildRWCDisplayText(sampleRWCResponse)
            resultTextView.text = resultText
        }
        // AI 추천 샘플 데이터 버튼 클릭 리스너
        btnTestAIRecommendationWithSample.setOnClickListener {
            val sampleRecommendations = SampleAIRecommendation.createSampleRecommendations()
            val resultText = buildAIRecommendationDisplayText(sampleRecommendations)
            resultTextView.text = resultText
        }
    }

    // API를 통해 RWCResponse 데이터를 가져오는 함수
    private fun fetchRegionAndWeather(nx: Int, ny: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ApiService 확장 함수 호출
                val rwcResponse = apiService.getRWC(nx, ny, "coldSensitive")
                withContext(Dispatchers.Main) {
                    if (rwcResponse != null) {
                        val resultText = buildRWCDisplayText(rwcResponse)
                        resultTextView.text = resultText
                    } else {
                        resultTextView.text = "서버로부터 데이터를 가져올 수 없습니다."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultTextView.text = "예외 발생: ${e.message}"
                }
            }
        }
    }

    // RWCResponse 텍스트 포맷팅
    private fun buildRWCDisplayText(rwcResponse: RWCResponse): String {
        val builder = StringBuilder()
        rwcResponse.regionWeather?.let {
            builder.append("=== 지역 및 날씨 정보 ===\n")
            builder.append(formatRegionAndWeather(it))
            builder.append("\n")
        }
        rwcResponse.clothingRecommendations?.let {
            builder.append("=== 추천 의상 정보 ===\n")
            builder.append(formatClothingInfo(it))
        }
        return builder.toString()
    }

    // 지역 및 날씨 정보를 출력하는 함수
    private fun formatRegionAndWeather(regionAndWeather: RegionAndWeather): String {
        val builder = StringBuilder()
        builder.append("지역: ${regionAndWeather.regionName}\n")

        regionAndWeather.weather.forEach { weather ->
            builder.append(" - 날짜: ${weather.forecastDate ?: "정보 없음"}\n")
            builder.append(" - 시간: ${weather.forecastTime ?: "정보 없음"}\n")
            builder.append(" - 1시간 기온: ${weather.temp ?: "정보 없음"} °C\n")
            builder.append(" - 최저 기온: ${weather.minTemp ?: "정보 없음"} °C\n")
            builder.append(" - 최고 기온: ${weather.maxTemp ?: "정보 없음"} °C\n")
            builder.append(" - 강수량: ${weather.rainAmount ?: "정보 없음"} mm\n")
            builder.append(" - 강수 확률: ${weather.rainProbability ?: "정보 없음"} %\n")
            builder.append(" - 강수 형태: ${weather.rainType}\n")
            builder.append(" - 하늘 상태: ${weather.skyCondition}\n")
            builder.append(" - 습도: ${weather.humid ?: "정보 없음"} %\n")
            builder.append(" - 풍속: ${weather.windSpeed ?: "정보 없음"} m/s\n")
            builder.append(" - 마지막 업데이트: ${weather.lastUpdateTime ?: "정보 없음"}\n")
            builder.append("\n")
        }
        return builder.toString()
    }

    private fun submitReviewAndShowToast(review: Review) {
        // CoroutineScope를 사용하여 네트워크 요청 실행
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.submitReview(review)

            // UI 업데이트를 위해 MainThread로 이동
            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@APITest2Activity, "리뷰가 전송되었습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@APITest2Activity, "리뷰 전송에 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // 추천 의상 정보를 출력하는 함수
    private fun formatClothingInfo(clothingRecommendations: List<ClothingRecommendation>): String {
        val builder = StringBuilder()
        clothingRecommendations.forEach { recommendation ->
            builder.append("온도: ${recommendation.temperature}\n")
            recommendation.recommendations.forEach { clothing ->
                builder.append(" - ${clothing}\n")
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    // AI옷추천 기능 테스트 버튼 (API요청)
    private fun fetchAIRecommendations(memberEmail: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // API 호출
                val response = apiService.getAIRecommendation(memberEmail)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val recommendations = response.body()
                        if (recommendations != null) {
                            // buildAIRecommendationDisplayText를 호출하여 결과를 포맷팅
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
            // 회원 이메일 및 온도 출력
            builder.append("회원 이메일: ${recommendation.memberEmail}\n")
            builder.append("온도: ${recommendation.temperature} °C\n")

            // JSON 문자열을 List<String>으로 변환
            val optimizedClothingsToList = parseOptimizedClothing(recommendation.optimizedClothing.toString())

            // 추천 의상 출력
            builder.append("추천 의상:\n")
            optimizedClothingsToList.forEach { clothing ->
                builder.append("$clothing\n") // 각 항목 출력
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
        val jsonArray = JSONArray(jsonString) // JSON 배열로 변환
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val type = jsonObject.getString("type") // "type" 값 가져오기
            val item = jsonObject.getString("item") // "item" 값 가져오기
            list.add("$type-$item") // "type-item" 형태로 리스트에 추가
        }
        return list
    }

}


