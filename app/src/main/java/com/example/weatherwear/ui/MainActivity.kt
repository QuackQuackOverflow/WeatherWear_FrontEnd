package com.example.weatherwear.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.ClothingRecommendation
import com.example.weatherwear.data.model.LearnedRecommendation
import com.example.weatherwear.data.model.RWCResponse
import com.example.weatherwear.data.model.RegionAndWeather
import com.example.weatherwear.data.sample.SampleAIRecommendation
import com.example.weatherwear.data.sample.SampleRWC
import com.example.weatherwear.helpers.GetRWCHelper
import com.example.weatherwear.helpers.MainUIHelper
import com.example.weatherwear.util.RetrofitInstance
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.weatherwear.helpers.requestAIRecommendation
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    // UI 요소
    private lateinit var checkCurrentRegionMain: TextView
    private lateinit var currentTempViewMain: TextView
    private lateinit var currentWeatherViewMain: ImageView
    private lateinit var timeWeatherContainer: LinearLayout
    private lateinit var clothesLinearLayout: LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var btnRequestAIRecommendation: Button
    private lateinit var btnToDetailedWeatherActivity: Button

    // Helper 클래스 및 데이터
    private lateinit var getRWCHelper: GetRWCHelper
    private lateinit var mainUIHelper: MainUIHelper
    private lateinit var apiService: ApiService
    private var rwcResponse: RWCResponse? = null // 현재 RWC 데이터 저장

    companion object {
        var useSample : Boolean = false
    }

    /** 고정 nx ny 사용 시 */
    private val useStaticPosition: Boolean = true
    private val staticNx: Int = 63
    private val staticNy: Int = 111

    private val ImageSize = 400

    /**
     * 죽겠어요
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 초기화
        checkCurrentRegionMain = findViewById(R.id.checkCurrentRegion_main)
        currentTempViewMain = findViewById(R.id.currentTempView_main)
        currentWeatherViewMain = findViewById(R.id.currentWeatherView_main)
        timeWeatherContainer = findViewById(R.id.hourlyWeatherScrollView_main)
        clothesLinearLayout = findViewById(R.id.clothesLinearLayout)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        btnRequestAIRecommendation = findViewById(R.id.btn_requestAIrecommendation)
        btnToDetailedWeatherActivity = findViewById(R.id.toDetailedWeatherActivity)
        val menuButton: ImageButton = findViewById(R.id.menuButton)

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getRWCHelper = GetRWCHelper(this, fusedLocationClient)
        mainUIHelper = MainUIHelper(this)
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        refreshWeatherData()

        // AI 추천 요청 버튼 이벤트
        btnRequestAIRecommendation.setOnClickListener {
            requestAIRecommendation()
        }

        swipeRefreshLayout.setOnRefreshListener {
            refreshWeatherData()
        }

        btnToDetailedWeatherActivity.setOnClickListener {
            rwcResponse?.regionWeather?.let { regionWeather ->
                val intent = Intent(this, DetailedWeatherActivity::class.java).apply {
                    putExtra("regionWeather", regionWeather) // Serializable 객체 전달
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "상세 날씨 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 메뉴 버튼 클릭 이벤트
        menuButton.setOnClickListener {
            // PopupMenu 생성
            val popupMenu = PopupMenu(this, menuButton)
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

            // 메뉴 항목 클릭 이벤트 처리
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.review -> {
                        // 리뷰 남기기
                        val clothingPrefs =
                            getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
                        val reviewPopup = ReviewPopup(this, clothingPrefs)
                        reviewPopup.show()
                        true
                    }

                    R.id.settings -> {
                        // 설정 이동
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent)
                        true
                    }

                    R.id.developer -> {
                        // 개발자용 이동
                        val intent = Intent(this, APITest2Activity::class.java)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show() // 팝업 메뉴 표시
        }


    }


    private fun requestAIRecommendation() {
        requestAIRecommendation(
            context = this,
            useSample = useSample,
            mainUIHelper = mainUIHelper,
            apiService = apiService,
            clothesLinearLayout = clothesLinearLayout
        ) { recommendations ->
            populateClothingRecommendations(recommendations)
        }
    }

    /**
     * RWC 데이터를 가져와 UI에 반영
     */
    private fun fetchAndUpdateUI() {
        // 최상위 조건: useSample 여부
        if (useSample) {
            // 샘플 데이터 사용
            val sampleRWCResponse = SampleRWC.createSampleRWCResponse()
            rwcResponse = sampleRWCResponse
            updateUI(sampleRWCResponse)
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(this, "샘플 RWC 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show()
        } else {
            val message =
                if (useStaticPosition) "고정 위치에서 RWC 데이터 불러오는 중" else "GPS에서 RWC 데이터 불러오는 중"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            if (useStaticPosition) {
                // 고정 nx, ny 값을 사용하는 경우
                getRWCHelper.fetchRWCWithParams(staticNx, staticNy) { response ->
                    if (response != null) {
                        rwcResponse = response
                        updateUI(response)
                    } else {
                        Toast.makeText(this, "고정 위치의 RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }
            } else {
                // GPS를 통해 nx, ny 값을 가져오는 경우
                getRWCHelper.fetchRWCFromGPS { response ->
                    if (response != null) {
                        rwcResponse = response
                        updateUI(response)
                    } else {
                        Toast.makeText(this, "GPS 기반 RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }


    /**
     * UI 업데이트
     */
    private fun updateUI(response: RWCResponse) {
        // 현재 날씨 UI 업데이트
        mainUIHelper.updateCurrentWeatherUI(
            response, checkCurrentRegionMain, currentTempViewMain, currentWeatherViewMain
        )

        // 시간대별 날씨 UI 생성
        mainUIHelper.generateTimeWeatherLayout(response, timeWeatherContainer)

        // 의상 추천 정보 업데이트
        response.clothingRecommendations?.let { populateClothingRecommendations(it) }

        // forecastDate별 요약 정보 추가
        val forecastSummaryContainer = findViewById<LinearLayout>(R.id.forecastSummaryContainer)
        response.regionWeather?.let { regionAndWeather ->
            mainUIHelper.addForecastSummary(regionAndWeather, forecastSummaryContainer)
        }
    }


    /**
     * 의상 추천 정보 표시
     */
    private fun populateClothingRecommendations(recommendations: List<ClothingRecommendation>) {
        clothesLinearLayout.removeAllViews()

        recommendations.forEach { recommendation ->
            // ImageButton 생성
            val imageButton = ImageButton(this).apply {
                setImageResource(R.drawable.t_shirt_100dp) // 올바른 리소스 이름 사용
                layoutParams = LinearLayout.LayoutParams(
                    ImageSize,
                    ImageSize
                ).apply { setMargins(8, 8, 8, 8) }
                setPadding(16, 16, 16, 16)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            // 클릭 이벤트 추가
            imageButton.setOnClickListener {
                showClothingPopup(recommendation)
            }

            // TextView 생성
            val textView = TextView(this).apply {
                // TextView에 온도를 직접 설정 (문자열 리소스 없이)
                text = recommendation.temperature
                textSize = 20f
                gravity = android.view.Gravity.CENTER
            }

            // LinearLayout으로 묶음
            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                addView(imageButton)
                addView(textView)
            }

            // 부모 Layout에 추가
            clothesLinearLayout.addView(container)
        }
    }

    /**
     * ClothingPopup 표시
     */
    private fun showClothingPopup(recommendation: ClothingRecommendation) {
        val popup = ClothingPopup(this, recommendation)
        popup.show()
    }

    /**
     * 샘플 데이터 사용
     */
    private fun testWithSampleData() {
        val sampleRWCResponse = SampleRWC.createSampleRWCResponse()
        rwcResponse = sampleRWCResponse
        updateUI(sampleRWCResponse)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun refreshWeatherData() {
        if (useSample) {
            testWithSampleData()
        } else {
            fetchAndUpdateUI()
        }
    }


}


