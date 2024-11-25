package com.example.weatherwear.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.helpers.GetRWCHelper
import com.example.weatherwear.helpers.MainUIHelper
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    // Activity에서 다룰 UI요소 선언
    private lateinit var checkCurrentRegionMain: TextView
    private lateinit var currentTempViewMain: TextView
    private lateinit var currentWeatherViewMain: ImageView
    private lateinit var timeWeatherContainer: LinearLayout
    private lateinit var clothesLinearLayout: LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // Activity에서 다룰 Helper클래스들
    private lateinit var getRWCHelper: GetRWCHelper
    private lateinit var mainUIHelper: MainUIHelper

    /**
     * 샘플 데이터를 사용할지 여부를 결정하는 변수
     */
    private var useSample: Boolean = true // true면 샘플 데이터를 사용, false면 서버 데이터를 사용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        checkCurrentRegionMain = findViewById(R.id.checkCurrentRegion_main)
        currentTempViewMain = findViewById(R.id.currentTempView_main)
        currentWeatherViewMain = findViewById(R.id.currentWeatherView_main)
        timeWeatherContainer = findViewById(R.id.hourlyWeatherScrollView_main)
        clothesLinearLayout = findViewById(R.id.clothesLinearLayout)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getRWCHelper = GetRWCHelper(this, fusedLocationClient)
        mainUIHelper = MainUIHelper(this) // MainUIHelper 초기화

        // RWC 데이터를 가져와서 UI에 반영
        refreshWeatherData()

        // Navigation Bar 버튼 초기화 및 클릭 이벤트 추가
        val navigationBarBtn1: Button = findViewById(R.id.navigationBarBtn1)
        val navigationBarBtn2: Button = findViewById(R.id.navigationBarBtn2)
        val navigationBarBtn3: Button = findViewById(R.id.navigationBarBtn3)

//        // 버튼 1: ReviewPopup 호출
//        navigationBarBtn1.setOnClickListener {
//            val reviewPopup = ReviewPopup(this) // ReviewPopup 생성
//            reviewPopup.show() // Popup을 화면에 표시
//        }

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

        // DetailedWeatherActivity로 이동
        val toDetailedWeatherActivity: Button = findViewById(R.id.toDetailedWeatherActivity)
        toDetailedWeatherActivity.setOnClickListener {
            val intent = Intent(this, DetailedWeatherActivity::class.java)
            startActivity(intent)
        }

        // SwipeRefreshLayout 리스너 추가
        swipeRefreshLayout.setOnRefreshListener {
            refreshWeatherData() // 새로고침 동작
        }
    }

    /**
     * RWC 데이터를 가져와 UI에 반영하는 함수
     * 1. 현재 날씨 정보 업데이트
     * 2. 시간대별 날씨 정보 생성
     * 3. 의상 추천 정보 생성
     */
    private fun fetchAndUpdateUI() {
        // useSample 값에 따라 메시지 설정
        val message = if (useSample) {"샘플 RWC 불러오는 중" } else { "서버에서 데이터 불러오는 중" }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // GPS 기반으로 RWC 데이터를 가져옴
        getRWCHelper.fetchRWCFromGPS { rwcResponse ->
            if (rwcResponse != null) {
                // 현재 날씨 정보를 UI에 반영
                mainUIHelper.updateCurrentWeatherUI(
                    rwcResponse,
                    checkCurrentRegionMain,    // 지역 이름 TextView
                    currentTempViewMain,       // 현재 온도 TextView
                    currentWeatherViewMain     // 현재 날씨 아이콘 ImageView
                )

                // 시간대별 날씨 정보를 UI에 반영
                mainUIHelper.generateTimeWeatherLayout(
                    rwcResponse, // RWCResponse 객체에서 시간대별 데이터
                    timeWeatherContainer          // 시간대별 날씨 LinearLayout
                )
                // 의상 추천 정보를 UI에 반영
                mainUIHelper.populateClothingRecommendations(
                    rwcResponse,                 // RWCResponse 데이터
                    clothesLinearLayout          // 의상 추천 HorizontalScrollView 내부 LinearLayout
                )
            } else {
                // RWC 데이터를 가져오지 못했을 경우 사용자에게 메시지 표시
                Toast.makeText(this, "RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            // SwipeRefreshLayout 로딩 해제
            swipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * 샘플 데이터를 사용하여 UI 테스트
     */
    private fun testWithSampleData() {
        val sampleRWCResponse = SampleRWC.createSampleRWCResponse() // 샘플 데이터 생성

        // 현재 날씨 정보 반영
        mainUIHelper.updateCurrentWeatherUI(
            sampleRWCResponse,
            checkCurrentRegionMain,
            currentTempViewMain,
            currentWeatherViewMain
        )

        // 시간대별 날씨 정보 반영
        mainUIHelper.generateTimeWeatherLayout(
            sampleRWCResponse,
            timeWeatherContainer
        )

        // 의상 추천 정보 반영
        mainUIHelper.populateClothingRecommendations(
            sampleRWCResponse,
            clothesLinearLayout
        )

        // SwipeRefreshLayout 로딩 해제
        swipeRefreshLayout.isRefreshing = false
    }

    /**
     * 화면 새로고침을 통해 데이터를 다시 불러오고 UI에 반영
     */
    private fun refreshWeatherData() {
        if (useSample) {
            testWithSampleData() // 샘플 데이터를 사용한 테스트
        } else {
            fetchAndUpdateUI() // 서버에서 데이터 다시 불러오기
        }
    }
}
