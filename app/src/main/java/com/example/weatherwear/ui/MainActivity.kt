package com.example.weatherwear.ui

import RWCResponse
import RegionAndWeather
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.helpers.GetRWCHelper
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var checkCurrentRegionMain: TextView
    private lateinit var currentTempViewMain: TextView
    private lateinit var currentWeatherViewMain: ImageView
    private lateinit var timeWeatherContainer: LinearLayout

    private lateinit var getRWCHelper: GetRWCHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        checkCurrentRegionMain = findViewById(R.id.checkCurrentRegion_main)
        currentTempViewMain = findViewById(R.id.currentTempView_main)
        currentWeatherViewMain = findViewById(R.id.currentWeatherView_main)
        timeWeatherContainer = findViewById(R.id.hourlyWeatherScrollView_main)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getRWCHelper = GetRWCHelper(this, fusedLocationClient)

        // RWC 데이터를 가져와서 UI에 반영
        fetchAndUpdateUI()

        /**
         * 샘플 데이터를 사용하여 UI 테스트
         */
        testWithSampleData()

        // Navigation Bar 버튼 초기화 및 클릭 이벤트 추가
        val navigationBarBtn1: Button = findViewById(R.id.navigationBarBtn1)
        val navigationBarBtn2: Button = findViewById(R.id.navigationBarBtn2)
        val navigationBarBtn3: Button = findViewById(R.id.navigationBarBtn3)

        // 버튼 1: ReviewPopup 호출
        navigationBarBtn1.setOnClickListener {
            val reviewPopup = ReviewPopup(this) // ReviewPopup 생성
            reviewPopup.show() // Popup을 화면에 표시
        }

        // 버튼 2: APITest2Activity로 이동
        navigationBarBtn2.setOnClickListener {
            val intent = Intent(this, APITest2Activity::class.java)
            startActivity(intent) // APITest2Activity 시작
        }

        // 버튼 3: SettingsActivity로 이동
        navigationBarBtn3.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent) // SettingsActivity 시작
        }
    }


    private fun fetchAndUpdateUI() {
        getRWCHelper.fetchRWCFromGPS { rwcResponse ->
            if (rwcResponse != null) {
                updateCurrentWeatherUI(rwcResponse)
                generateTimeWeatherLayout(rwcResponse.regionAndWeather)
            } else {
                Toast.makeText(this, "RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 날씨 정보를 UI에 반영
    private fun updateCurrentWeatherUI(rwcResponse: RWCResponse) {
        val firstRegionAndWeather = rwcResponse.regionAndWeather.firstOrNull()

        if (firstRegionAndWeather != null) {
            // 지역 이름 반영
            checkCurrentRegionMain.text = firstRegionAndWeather.regionName

            // 현재 온도 반영
            currentTempViewMain.text = "${firstRegionAndWeather.weather.temp.toInt()}°C"

            // 현재 날씨 아이콘 반영
            val skyCondition = firstRegionAndWeather.weather.skyCondition
            val iconRes = when (skyCondition) {
                "흐림" -> R.drawable.baseline_cloud_100dp_outline
                "맑음" -> R.drawable.baseline_wb_sunny_100dp_outline
                else -> R.drawable.baseline_question_mark_50dp_outline // 기본 아이콘
            }
            currentWeatherViewMain.setImageResource(iconRes)
        } else {
            // 데이터가 없을 경우 기본 메시지 표시
            checkCurrentRegionMain.text = "지역 정보 없음"
            currentTempViewMain.text = "온도 정보 없음"
            currentWeatherViewMain.setImageResource(R.drawable.baseline_question_mark_50dp_outline)
        }
    }

    private fun generateTimeWeatherLayout(regionAndWeatherList: List<RegionAndWeather>) {
        timeWeatherContainer.removeAllViews() // 기존 데이터 제거

        // 1번째부터 24번째 RegionAndWeather 객체만 사용
        val timeWeatherData = regionAndWeatherList.take(24)

        timeWeatherData.forEach { regionAndWeather ->
            // RelativeLayout 설정
            val hourLayout = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (50 * resources.displayMetrics.density).toInt(), // 50dp
                    (100 * resources.displayMetrics.density).toInt() // 150dp
                ).apply {
                    setMargins(20, 0, 20, 0) // 마진 설정
                }
                // 배경색 설정
                setBackgroundColor(resources.getColor(R.color.hourlyTempViewSkyblue, null))
            }

            // 시간 텍스트 설정 (상단)
            val hourText = TextView(this).apply {
                id = View.generateViewId() // ID 생성
                text = "${regionAndWeather.weather.forecastTime.substring(0, 2)}시"
                textSize = 15f
                gravity = android.view.Gravity.CENTER
            }
            val hourTextParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP) // 상단 정렬
                addRule(RelativeLayout.CENTER_HORIZONTAL) // 가로 중앙 정렬
            }
            hourLayout.addView(hourText, hourTextParams)

            // 날씨 아이콘 설정 (중앙)
            val weatherIcon = ImageView(this).apply {
                id = View.generateViewId() // ID 생성
                val skyCondition = regionAndWeather.weather.skyCondition
                val iconRes = when (skyCondition) {
                    "흐림" -> R.drawable.baseline_cloud_40dp_outline
                    "맑음" -> R.drawable.baseline_wb_sunny_40dp_outline
                    else -> R.drawable.baseline_question_mark_40dp_outline // 기본 아이콘
                }
                setImageResource(iconRes)
            }
            val weatherIconParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT) // 중앙 정렬
            }
            hourLayout.addView(weatherIcon, weatherIconParams)

            // 온도 텍스트 설정 (하단)
            val tempText = TextView(this).apply {
                text = "${regionAndWeather.weather.temp.toInt()}°"
                textSize = 20f
                gravity = android.view.Gravity.CENTER
            }
            val tempTextParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) // 하단 정렬
                addRule(RelativeLayout.CENTER_HORIZONTAL) // 가로 중앙 정렬
            }
            hourLayout.addView(tempText, tempTextParams)

            // 메인 컨테이너에 추가
            timeWeatherContainer.addView(hourLayout)
        }
    }



    /**
     * 샘플 데이터를 사용하여 UI 테스트
     */
    private fun testWithSampleData() {
        val sampleRWCResponse = SampleRWC.createSampleRWCResponse() // 싱글톤 객체 활용
        updateCurrentWeatherUI(sampleRWCResponse)
        generateTimeWeatherLayout(sampleRWCResponse.regionAndWeather)
    }
}
