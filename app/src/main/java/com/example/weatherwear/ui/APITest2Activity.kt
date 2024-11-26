package com.example.apitest

import android.net.http.HttpException
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.api.getRWC
import com.example.weatherwear.data.model.*
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.util.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var getRegionAndWeatherCustomButton: Button
    private lateinit var editTextNx: EditText
    private lateinit var editTextNy: EditText
    private lateinit var buttonSubmitReview: Button

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



        /**
         * 샘플 데이터를 출력하는 버튼
         */
        getSampleDataButton.setOnClickListener {
            val sampleRWCResponse = SampleRWC.createSampleRWCResponse()
            val resultText = buildRWCDisplayText(sampleRWCResponse)
            resultTextView.text = resultText
        }
    }

    // API를 통해 RWCResponse 데이터를 가져오는 함수
    private fun fetchRegionAndWeather(nx: Int, ny: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ApiService 확장 함수 호출
                val rwcResponse = apiService.getRWC(nx, ny, "추위를 잘 타요")
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
                    Toast.makeText(this@APITest2Activity, "리뷰 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
}

//    // 강수 형태를 텍스트로 변환하는 함수
//    private fun mapRainType(rainType: String?): String {
//        return when (rainType) {
//            "0" -> "없음"
//            "1" -> "비"
//            "2" -> "비/눈"
//            "3" -> "눈"
//            "4" -> "소나기"
//            else -> "정보 없음"
//        }
//    }
//
//    // 하늘 상태를 텍스트로 변환하는 함수
//    private fun mapSkyCondition(skyCondition: String?): String {
//        return when (skyCondition) {
//            "1" -> "맑음"
//            "3" -> "구름 많음"
//            "4" -> "흐림"
//            else -> "정보 없음"
//        }
//    }
