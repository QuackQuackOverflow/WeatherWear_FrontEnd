package com.example.weatherwear.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.Clothing
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.Review
import com.example.weatherwear.helpers.GetClothingHelper
import com.example.weatherwear.helpers.LocationToWeatherHelper
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // 스와이프 새로고침 레이아웃
    private lateinit var locationToWeatherHelper: LocationToWeatherHelper // LocationToWeatherHelper 객체 선언
    private lateinit var getClothingHelper: GetClothingHelper // GetClothingHelper 객체 선언
    private lateinit var checkCurrentRegionMain: TextView // 지역 이름 표시 텍스트뷰
    private lateinit var currentTempViewMain: TextView // 현재 온도 표시 텍스트뷰
    private lateinit var clothingContainer: LinearLayout // 옷 추천 레이아웃 컨테이너

    /**
     * 옷 출력 테스트용 메서드
     */
    private fun testClothingUI() {
        // 테스트 데이터를 생성
        val testClothingSet = ClothingSet(
            id = 1, // 임의의 식별자 값
            recommendedClothings = listOf(
                Clothing(type = "", name = "셔츠1"),
                Clothing(type = "", name = "바지1"),
                Clothing(type = "", name = "재킷1"),
                Clothing(type = "", name = "셔츠2"),
                Clothing(type = "", name = "바지2"),
                Clothing(type = "", name = "재킷2")
            )
        )
        // UI 업데이트 함수 호출
        updateClothingUI(testClothingSet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // 메인 레이아웃 설정

        // 뷰 초기화
        checkCurrentRegionMain = findViewById(R.id.checkCurrentRegion_main)
        currentTempViewMain = findViewById(R.id.currentTempView_main)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        clothingContainer = findViewById(R.id.clothesLinearLayout) // 옷 추천 컨테이너

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationToWeatherHelper = LocationToWeatherHelper(this, fusedLocationClient)
        getClothingHelper = GetClothingHelper(this)

        // 위치 기반 날씨 정보 가져오기
        fetchWeatherAndUpdateUI()
        // 의류 추천 리스트 가져오기
        fetchClothingSetAndUpdateUI()
        // 시간대별 날씨 레이아웃 생성
        generateTimeWeatherLayout()

        // navigationBarBtn1 클릭 시 ReviewPopup으로 이동
        findViewById<Button>(R.id.navigationBarBtn1).setOnClickListener {
            val reviewPopup = ReviewPopup(this)
            reviewPopup.show() // Dialog를 표시
        }

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
        /**
         * 옷 출력 테스트용
         */
        testClothingUI()
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
                gravity = android.view.Gravity.CENTER
            }

            val weatherIcon = ImageView(this).apply {
                setImageResource(defaultIcon) // 기본 아이콘 설정
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tempText = TextView(this).apply {
                text = defaultTemperature // 온도 텍스트 설정
                textSize = 20f
                gravity = android.view.Gravity.CENTER
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
        fetchClothingSetAndUpdateUI()
        swipeRefreshLayout.isRefreshing = false
    }

    // LocationToWeatherHelper를 사용하여 날씨 정보 가져오고 UI 업데이트
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

    // GetClothingHelper를 사용하여 의류 추천 리스트 가져오고 UI 업데이트
    private fun fetchClothingSetAndUpdateUI() {
        // SharedPreferences에서 userType 가져오기
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val userType =
            sharedPreferences.getString("userType", "default") // 저장된 userType 가져오기, 기본값 "default"

        // 가져온 userType을 fetchClothingSet에 전달
        getClothingHelper.fetchClothingSet(userType = userType ?: "default") { clothingSet ->
            if (clothingSet != null) {
                getClothingHelper.saveClothingSetToPreferences(clothingSet)
                updateClothingUI(clothingSet)
            } else {
                Toast.makeText(this, "의류 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 의류 추천 UI 업데이트 함수
    private fun updateClothingUI(clothingSet: ClothingSet) {
        val clothingLinearLayout = findViewById<LinearLayout>(R.id.clothesLinearLayout)
        clothingLinearLayout.removeAllViews() // 기존 뷰 제거

        clothingSet.recommendedClothings.forEach { clothing ->
            val clothingLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(
                    500, // 고정된 너비
                    500  // 고정된 높이
                ).apply {
                    setMargins(20, 0, 20, 0)
                }
                setBackgroundColor(resources.getColor(R.color.superLightGray)) // 배경색 설정
                gravity = Gravity.CENTER
            }

            val clothingImage = ImageButton(this).apply {
                setImageResource(R.drawable.t_shirt_100dp) // 기본 이미지
                layoutParams = LinearLayout.LayoutParams(
                    350,
                    350 // 고정된 높이
                )
                setBackgroundColor(resources.getColor(android.R.color.transparent)) // 투명 배경
            }

            val clothingName = TextView(this).apply {
                text = clothing.name
                textSize = 20f
                gravity = Gravity.CENTER
            }

            clothingLayout.addView(clothingImage)
            clothingLayout.addView(clothingName)

            clothingLinearLayout.addView(clothingLayout)
        }
    }

}
