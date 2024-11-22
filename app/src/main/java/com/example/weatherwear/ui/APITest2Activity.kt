package com.example.apitest

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.helpers.LocationHelper
import com.example.weatherwear.util.RetrofitInstance
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
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
            fetchClothingSet()
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

    // 의류 추천 데이터 가져오기
    private fun fetchClothingSet() {
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // userType 쿼리를 사용하여 요청
                val response = apiService.getClothingSet(userType = "hot")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val clothingSet = response.body()
                        if (clothingSet != null) {
                            // SharedPreferences에 ClothingSet 저장
                            saveClothingSetToPreferences(clothingSet)
                            val resultText = buildClothingSetDisplayText(clothingSet)
                            resultTextView.text = resultText
                        } else {
                            resultTextView.text = "추천 의상 세트를 가져올 수 없습니다."
                        }
                    } else {
                        resultTextView.text = "Error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@APITest2Activity, "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 의상 세트 텍스트 포맷팅
    private fun buildClothingSetDisplayText(clothingSet: ClothingSet): String {
        val builder = StringBuilder()
        builder.append("의상 Set ID: ${clothingSet.id}\n")
        clothingSet.recommendedClothings.forEachIndexed { index, clothing ->
            builder.append("옷${index + 1}: ${clothing.name} (${clothing.type})\n")
        }
        return builder.toString()
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

    // ClothingSet 데이터를 SharedPreferences에 저장하는 함수
    private fun saveClothingSetToPreferences(clothingSet: ClothingSet) {
        val sharedPreferences = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val clothingSetJson = gson.toJson(clothingSet) // ClothingSet 객체를 JSON 문자열로 변환
        editor.putString("clothingSet", clothingSetJson)
        editor.apply()
    }
}
