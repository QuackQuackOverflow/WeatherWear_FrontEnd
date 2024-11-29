package com.example.weatherwear.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
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
import com.example.weatherwear.helpers.requestAIRecommendationInHelper
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
    private lateinit var progressBar: ProgressBar

    // Helper 클래스 및 데이터
    private lateinit var getRWCHelper: GetRWCHelper
    private lateinit var mainUIHelper: MainUIHelper
    private lateinit var apiService: ApiService
    private var rwcResponse: RWCResponse? = null // 현재 RWC 데이터 저장

    // 샘플 데이터 사용 끝
    companion object {
        var useSample : Boolean = false
    }

    /** 고정 nx ny 사용 시 */
    private val useStaticPosition: Boolean = true
    private val staticNx: Int = 63
    private val staticNy: Int = 111

    private val ImageSize = 400

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
        progressBar = findViewById(R.id.progressBar)
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
                        val clothingPrefs = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)

                        // ClothingPrefs가 비어 있는지 확인
                        if (clothingPrefs.all.isNullOrEmpty()) {
                            Toast.makeText(this, "선택됐던 옷이 없습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            val reviewPopup = ReviewPopup(this, clothingPrefs)
                            reviewPopup.show()
                        }
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
        val temperature = rwcResponse?.regionWeather?.weather?.firstOrNull()?.temp // 온도 데이터 추출

        if (temperature == null) {
            Toast.makeText(this, "온도 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        requestAIRecommendationInHelper(
            context = this,
            useSample = useSample,
            mainUIHelper = mainUIHelper,
            apiService = apiService,
            clothesLinearLayout = clothesLinearLayout,
            populateClothingRecommendations = { recommendations ->
                populateClothingRecommendations(recommendations)
            },
            temperature = temperature // 추출한 온도를 전달
        )
    }

    /**
     * RWC 데이터를 가져와 UI에 반영
     */
    private fun fetchAndUpdateUI() {
        progressBar.visibility = View.VISIBLE // ProgressBar 표시

        if (useSample) {
            // 샘플 데이터 사용
            val sampleRWCResponse = SampleRWC.createSampleRWCResponse()
            rwcResponse = sampleRWCResponse
            updateUI(sampleRWCResponse)
            swipeRefreshLayout.isRefreshing = false
            progressBar.visibility = View.GONE // ProgressBar 숨기기
            Toast.makeText(this, "샘플 RWC 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show()
            checkAndShowReviewPopup() // 추가된 기능
        } else {
            val message =
                if (useStaticPosition) "날씨 데이터 불러오는 중" else "GPS에서 RWC 데이터 불러오는 중"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            if (useStaticPosition) {
                // 고정 nx, ny 값을 사용하는 경우
                getRWCHelper.fetchRWCWithParams(staticNx, staticNy) { response ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE // ProgressBar 숨기기
                        if (response != null) {
                            rwcResponse = response
                            updateUI(response)
                        } else {
                            Toast.makeText(this, "날씨 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                        swipeRefreshLayout.isRefreshing = false
                        checkAndShowReviewPopup() // 추가된 기능
                    }
                }
            } else {
                // GPS를 통해 nx, ny 값을 가져오는 경우
                getRWCHelper.fetchRWCFromGPS { response ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE // ProgressBar 숨기기
                        if (response != null) {
                            rwcResponse = response
                            updateUI(response)
                        } else {
                            Toast.makeText(this, "GPS 기반 RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                        swipeRefreshLayout.isRefreshing = false
                        checkAndShowReviewPopup() // 추가된 기능
                    }
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

        // 앱 실행 시 ReviewPopup 딱 한 번 표시
        checkAndShowReviewPopup()
    }


    /**
     * 의상 추천 정보 표시
     */
    private fun populateClothingRecommendations(recommendations: List<ClothingRecommendation>) {
        clothesLinearLayout.removeAllViews()

        recommendations.forEach { recommendation ->
            // 리스트에서 난수를 생성하여 임의의 옷 이름 추출
            val randomClothingName = recommendation.recommendations.takeIf { it.isNotEmpty() }?.let { clothingList ->
                val randomIndex = (0 until clothingList.size).random() // 0부터 리스트 크기 - 1까지의 난수 생성
                val extractedName = extractClothingName(clothingList[randomIndex])
                getClothingImageResource(extractedName)
            } ?: R.drawable.t_shirt_100dp // 기본 이미지

            // ImageButton 생성
            val imageButton = ImageButton(this).apply {
                setImageResource(randomClothingName) // 난수로 선택된 리소스 사용
                layoutParams = LinearLayout.LayoutParams(
                    ImageSize,
                    ImageSize
                ).apply { setMargins(8, 8, 8, 8) }
                setPadding(16, 16, 16, 16)
                scaleType = ImageView.ScaleType.CENTER_INSIDE

                // 둥근 모서리를 가진 배경 설정 (배경색 lightGray)
                background = GradientDrawable().apply {
                    setColor(context.getColor(R.color.superLightGray)) // 배경색 설정
                    cornerRadius = 16 * context.resources.displayMetrics.density // Corner radius in dp
                }
            }

            // 클릭 이벤트 추가
            imageButton.setOnClickListener {
                showClothingPopup(recommendation)
            }

            // TextView 생성
            val textView = TextView(this).apply {
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

                // 부모 레이아웃에 적용할 마진 설정
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 0, 16, 0) // 좌우 16dp 마진
                }
            }

            // 부모 Layout에 추가
            clothesLinearLayout.addView(container)
        }
    }

    /**
     * 옷 이름에서 주요 항목 추출 ("상의 - 티셔츠" -> "티셔츠")
     */
    private fun extractClothingName(fullName: String): String {
        return fullName.substringAfterLast("-").trim()
    }

    /**
     * 옷 이름에 따른 이미지 리소스를 반환
     */
    private fun getClothingImageResource(clothingName: String): Int {
        val resourceName = when (clothingName) {
            "민소매" -> "1_sleeveless"
            "반팔 티셔츠" -> "2_short_sleeve_tshirt"
            "반바지" -> "3_shorts"
            "짧은 치마" -> "4_short_skirt"
            "민소매 원피스" -> "5_sleeveless_dress"
            "린넨 재질 옷" -> "6_linen_clothing"
            "얇은 셔츠" -> "7_light_shirt"
            "얇은 긴팔 티셔츠" -> "8_light_long_sleeve_tshirt"
            "면바지" -> "9_cotton_pants"
            "얇은 가디건" -> "10_light_cardigan"
            "긴팔 티셔츠" -> "11_long_sleeve_tshirt"
            "셔츠" -> "12_shirt"
            "블라우스" -> "13_blouse"
            "후드티" -> "14_hoodie"
            "슬랙스" -> "15_slacks"
            "청바지" -> "16_jeans"
            "얇은 니트" -> "17_light_knit"
            "얇은 재킷" -> "18_light_jacket"
            "바람막이" -> "19_windbreaker"
            "맨투맨" -> "20_sweatshirt"
            "스키니진" -> "21_skinny_jeans"
            "재킷" -> "22_jacket"
            "가디건" -> "23_cardigan"
            "야상" -> "24_field_jacket"
            "스웨트 셔츠" -> "25_sweat_shirt"
            "기모 후드티" -> "26_fleece_hoodie"
            "스타킹" -> "27_stockings"
            "니트" -> "28_knit_sweater"
            "점퍼" -> "29_jumper"
            "트렌치 코트" -> "30_trench_coat"
            "레이어드 니트" -> "31_layered_knit"
            "코트" -> "32_coat"
            "가죽 재킷" -> "33_leather_jacket"
            "레깅스" -> "34_leggings"
            "두꺼운 바지" -> "35_thick_pants"
            "기모 바지" -> "36_fleece_pants"
            "기모 스타킹" -> "37_fleece_stockings"
            "스카프" -> "38_scarf"
            "플리스" -> "39_fleece"
            "내복" -> "40_thermal_underwear"
            "패딩" -> "41_padded_jacket"
            "두꺼운 코트" -> "42_heavy_coat"
            "누빔" -> "43_quilted_clothing"
            "목도리" -> "44_muffler"
            "장갑" -> "45_gloves"
            "방한용품" -> "46_winter_accessories"
            else -> return resources.getIdentifier("t_shirt_100dp", "drawable", packageName) // 기본 이미지
        }

        // 리소스 이름을 기반으로 리소스 ID 반환
        return resources.getIdentifier("clothing_$resourceName", "drawable", packageName)
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

    /**
     * 새로고침 데이터 처리
     */
    private fun refreshWeatherData() {
        if (useSample) {
            testWithSampleData()
        } else {
            fetchAndUpdateUI()
        }
    }

    /**
     * 앱 실행 시 최초 ReviewPopup 표시 여부를 확인하고, 딱 한 번만 표시
     */
    private fun checkAndShowReviewPopup() {
        val preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val hasShownReviewPopup = preferences.getBoolean("HasShownReviewPopup", false)

        // 이미 표시했다면 종료
        if (hasShownReviewPopup) return

        val clothingPrefs = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        if (!clothingPrefs.all.isNullOrEmpty()) {
            val reviewPopup = ReviewPopup(this, clothingPrefs)
            reviewPopup.show()

            // ReviewPopup을 표시한 상태를 기록
            preferences.edit().putBoolean("HasShownReviewPopup", true).apply()
        }
    }

}


