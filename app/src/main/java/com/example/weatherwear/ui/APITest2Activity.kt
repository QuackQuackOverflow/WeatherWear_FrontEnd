package com.example.apitest

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.*
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.helpers.GetRWCHelper
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

    /**
     * 샘플 데이터를 테스트하기 위한 버튼
     */
    private lateinit var getSampleDataButton: Button

    // Helper 객체
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

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

        /**
         * 샘플 데이터를 출력하는 버튼
         */
        getSampleDataButton.setOnClickListener {
            val sampleRWCResponse = SampleRWC.createSampleRWCResponse()
            val resultText = buildRWCDisplayText(sampleRWCResponse)
            resultTextView.text = resultText
        }
    }

    // API를 통해 RegionAndWeather 데이터를 가져오는 함수
    private fun fetchRegionAndWeather(nx: Int, ny: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getRWC(nx, ny, "user")
                if (response.isSuccessful) {
                    val rwcResponse = response.body()
                    withContext(Dispatchers.Main) {
                        if (rwcResponse != null) {
                            val resultText = buildRWCDisplayText(rwcResponse)
                            resultTextView.text = resultText
                        } else {
                            resultTextView.text = "서버로부터 데이터를 가져올 수 없습니다."
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        resultTextView.text =
                            "API 호출 실패: ${response.code()} - ${response.message()}"
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
        builder.append("=== 지역 및 날씨 정보 ===\n")
        builder.append(formatWeatherInfoList(rwcResponse.regionAndWeather))
        builder.append("\n")
        builder.append("=== 추천 의상 정보 ===\n")
        builder.append(formatClothingInfo(rwcResponse.clothingSet))
        return builder.toString()
    }

    // 지역 및 날씨 정보를 출력하는 함수
    private fun formatWeatherInfoList(regionAndWeatherList: List<RegionAndWeather>): String {
        val builder = StringBuilder()
        regionAndWeatherList.forEachIndexed { index, regionAndWeather ->
            builder.append("지역 ${index + 1}:\n")
            builder.append(formatWeatherInfo(regionAndWeather))
            builder.append("\n")
        }
        return builder.toString()
    }

    // 단일 RegionAndWeather 날씨 정보를 출력하는 함수
    private fun formatWeatherInfo(regionAndWeather: RegionAndWeather): String {
        val builder = StringBuilder()
        builder.append("지역: ${regionAndWeather.regionName}\n")
        val weather = regionAndWeather.weather
        builder.append(" - 날짜: ${weather.forecastDate}\n")
        builder.append(" - 시간: ${weather.forecastTime}\n")
        builder.append(" - 1시간 기온: ${weather.temp} °C\n")
        builder.append(" - 최저 기온: ${weather.minTemp} °C\n")
        builder.append(" - 최고 기온: ${weather.maxTemp} °C\n")
        builder.append(" - 강수량: ${weather.rainAmount} mm\n")
        builder.append(" - 강수 확률: ${weather.rainProbability} %\n")
        builder.append(" - 하늘 상태: ${weather.skyCondition}\n")
        builder.append(" - 습도: ${weather.humid} %\n")
        builder.append(" - 풍속: ${weather.windSpeed} m/s\n")
        return builder.toString()
    }

    // 추천 의상 정보를 출력하는 함수
    private fun formatClothingInfo(clothingSet: ClothingSet): String {
        val builder = StringBuilder()
        builder.append("의상 Set ID: ${clothingSet.id}\n")
        clothingSet.recommendedClothings.forEachIndexed { index, clothing ->
            builder.append("옷${index + 1}: ${clothing.name} (${clothing.type})\n")
        }
        return builder.toString()
    }
}
