package com.example.apitest

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.helpers.LocationHelper
import com.example.weatherwear.util.RetrofitInstance
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class APITest2Activity : AppCompatActivity() {

    // UI 요소
    private lateinit var resultTextView: TextView
    private lateinit var getClothingSetButton: Button
    private lateinit var getRegionAndWeatherButton: Button

    // LocationHelper 객체
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest2)

        // UI 초기화
        resultTextView = findViewById(R.id.textViewResult)
        getClothingSetButton = findViewById(R.id.buttonGetClothingSet)
        getRegionAndWeatherButton = findViewById(R.id.buttonGetRegionAndWeather)

        // LocationHelper 초기화
        locationHelper = LocationHelper(this, LocationServices.getFusedLocationProviderClient(this))
        locationHelper.requestLocationPermission()

        // 의류 추천받기 버튼 클릭
        getClothingSetButton.setOnClickListener {
            locationHelper.getCurrentNxNy { nx, ny ->
                sendClothingSetRequest(nx, ny)
            }
        }

        // 지역과 날씨 정보 버튼 클릭
        getRegionAndWeatherButton.setOnClickListener {
            locationHelper.getCurrentNxNy { nx, ny ->
                // TextView에 nx, ny 표시
                resultTextView.text = "nx: $nx, ny: $ny"
                sendRegionAndWeatherRequest(nx, ny)
            }
        }
    }

    //의류 추천 데이터 가져오기
    private fun sendClothingSetRequest(nx: Int, ny: Int) {
        // 갸악 하기 싫어
    }

    // 지역과 날씨 정보 가져오기
    private fun sendRegionAndWeatherRequest(nx: Int, ny: Int) {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getRegionAndWeather(nx, ny)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val regionName = data.regionName ?: "알 수 없음"
                        val weather = data.weather
                        resultTextView.text = """
                            지역: $regionName
                            날씨 정보:
                            - 1시간 기온: ${weather?.temp ?: "Unknown"} °C
                            - 최저 기온: ${weather?.minTemp ?: "Unknown"} °C
                            - 최고 기온: ${weather?.maxTemp ?: "Unknown"} °C
                            - 강수량: ${weather?.rainAmount ?: "Unknown"} mm
                            - 강수 확률: ${weather?.rainProbability ?: "Unknown"} %
                            - 강수 형태: ${weather?.rainType ?: "Unknown"}
                            - 하늘 상태: ${weather?.skyCondition ?: "Unknown"}
                            - 습도: ${weather?.humid ?: "Unknown"}%
                            - 풍속: ${weather?.windSpeed ?: "Unknown"}m/s
                        """.trimIndent()
                    } else {
                        resultTextView.text = "지역 및 날씨 정보를 가져올 수 없습니다."
                    }
                } else {
                    Toast.makeText(this@APITest2Activity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

