package com.example.weatherwear.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.helpers.GetRWCHelper
import com.example.weatherwear.helpers.MainUIHelper
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var checkCurrentRegionMain: TextView
    private lateinit var currentTempViewMain: TextView
    private lateinit var currentWeatherViewMain: ImageView
    private lateinit var timeWeatherContainer: LinearLayout

    private lateinit var getRWCHelper: GetRWCHelper
    private lateinit var mainUIHelper: MainUIHelper

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
        mainUIHelper = MainUIHelper(this) // MainUIHelper 초기화

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
                mainUIHelper.updateCurrentWeatherUI(
                    rwcResponse,
                    checkCurrentRegionMain,
                    currentTempViewMain,
                    currentWeatherViewMain
                )
                mainUIHelper.generateTimeWeatherLayout(
                    rwcResponse.regionAndWeather,
                    timeWeatherContainer
                )
            } else {
                Toast.makeText(this, "RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 샘플 데이터를 사용하여 UI 테스트
     */
    private fun testWithSampleData() {
        val sampleRWCResponse = SampleRWC.createSampleRWCResponse() // 싱글톤 객체 활용
        mainUIHelper.updateCurrentWeatherUI(
            sampleRWCResponse,
            checkCurrentRegionMain,
            currentTempViewMain,
            currentWeatherViewMain
        )
        mainUIHelper.generateTimeWeatherLayout(
            sampleRWCResponse.regionAndWeather,
            timeWeatherContainer
        )
    }
}
