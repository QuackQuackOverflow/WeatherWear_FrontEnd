package com.example.weatherwear.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.helpers.LocationToWeatherHelper
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // 스와이프 새로고침 레이아웃
    private lateinit var locationToWeatherHelper: LocationToWeatherHelper // LocationToWeatherHelper 객체 선언
    private lateinit var checkCurrentRegionMain: TextView // 지역 이름 표시 텍스트뷰
    private lateinit var currentTempViewMain: TextView // 현재 온도 표시 텍스트뷰

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 메인 레이아웃 설정

        // 뷰 초기화
        checkCurrentRegionMain = findViewById(R.id.checkCurrentRegion_main)
        currentTempViewMain = findViewById(R.id.currentTempView_main)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationToWeatherHelper = LocationToWeatherHelper(this, fusedLocationClient)

        // 위치 기반 날씨 정보 가져오기
        fetchWeatherAndUpdateUI()

        // 시간대별 날씨 레이아웃 생성
        generateTimeWeatherLayout()

        // navigationBarBtn2 클릭 시 APITest2Activity로 이동
        findViewById<Button>(R.id.navigationBarBtn2).setOnClickListener {
            startActivity(Intent(this, APITest2Activity::class.java))
        }

        // navigationBarBtn3 클릭 시 Settings로 이동
        findViewById<Button>(R.id.navigationBarBtn3).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // 스와이프 새로고침 설정
        swipeRefreshLayout.setOnRefreshListener {
            refreshLocation()
        }
    }

    // 시간대별 날씨 레이아웃 생성 함수
    private fun generateTimeWeatherLayout() {
        val timeWeatherContainer = findViewById<LinearLayout>(R.id.hourlyWeatherScrollView_main)
        val defaultIcon = R.drawable.baseline_wb_sunny_24_30dp_with_outline // 아이콘 설정
        val defaultTemperature = "0°C" // 기본 온도 설정

        for (hour in 0..23) {
            val hourLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL // 세로 방향 레이아웃
                setPadding(10, 0, 10, 0) // 패딩 설정
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val hourText = TextView(this).apply {
                text = "${hour}시" // 시간 텍스트 설정
                textSize = 20f
                gravity = Gravity.CENTER
            }

            val weatherIcon = ImageView(this).apply {
                setImageResource(defaultIcon) // 기본 아이콘 설정
                var layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tempText = TextView(this).apply {
                text = defaultTemperature // 온도 텍스트 설정
                textSize = 20f
                gravity = Gravity.CENTER
            }

            // 각 요소를 시간별 레이아웃에 추가
            hourLayout.addView(hourText)
            hourLayout.addView(weatherIcon)
            hourLayout.addView(tempText)

            // 시간대별 레이아웃을 스크롤 뷰 컨테이너에 추가
            timeWeatherContainer.addView(hourLayout)
        }
    }

    // 리뷰 팝업을 표시하는 함수
    private fun showReviewPopup() {
        val reviewPopup = ReviewPopup(this)
        reviewPopup.show()
    }

    // 새로고침 시 위치 갱신
    private fun refreshLocation() {
        fetchWeatherAndUpdateUI()
        swipeRefreshLayout.isRefreshing = false
    }

    // LocationToWeatherHelper를 사용하여 날씨 정보 가져오고 UI 업데이트!!
    private fun fetchWeatherAndUpdateUI() {
        locationToWeatherHelper.fetchRegionAndWeatherFromGPS { response ->
            if (response != null) {
                // 지역 이름 업데이트
                val regionName = response.regionName ?: "지역 정보 없음"
                checkCurrentRegionMain.text = regionName

                // 현재 온도 업데이트
                val currentTemp = response.weather?.temp?.let { "$it°C" } ?: "온도 정보 없음"
                currentTempViewMain.text = currentTemp
            } else {
                Toast.makeText(this, "날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                checkCurrentRegionMain.text = "정보 없음"
                currentTempViewMain.text = "정보 없음"
            }
        }
    }
}
