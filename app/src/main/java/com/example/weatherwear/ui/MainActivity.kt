package com.example.weatherwear.ui

import com.example.weatherwear.ui.*
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.helpers.LocationHelper
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // 스와이프 새로고침 레이아웃
    private lateinit var locationHelper: LocationHelper // LocationHelper 객체 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 메인 레이아웃 설정

        // LocationHelper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationHelper = LocationHelper(this, fusedLocationClient)
        // 위치 권한 요청
        locationHelper.requestLocationPermission()

        // checkCurrentRegion_main에 지역이름 반영


        // 시간대별 날씨 레이아웃 생성
        generateTimeWeatherLayout()

        //

        // navigationBarBtn2 클릭 시 APITest2Activity로 이동
        findViewById<Button>(R.id.navigationBarBtn2).setOnClickListener {
            startActivity(Intent(this, APITest2Activity::class.java))
        }
        // navigationBarBtn3 클릭 시 Settings로 이동
        findViewById<Button>(R.id.navigationBarBtn3).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        // 스와이프 새로고침 설정
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
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
        // 현재 액티비티를 종료하고 다시 시작
        finish()
        startActivity(intent)
    }
}
