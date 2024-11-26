package com.example.weatherwear.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.APITest2Activity
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.ClothingRecommendation
import com.example.weatherwear.data.model.LearnedRecommendation
import com.example.weatherwear.data.model.RWCResponse
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

    // Helper 클래스 및 데이터
    private lateinit var getRWCHelper: GetRWCHelper
    private lateinit var mainUIHelper: MainUIHelper
    private lateinit var apiService: ApiService
    private var rwcResponse: RWCResponse? = null // 현재 RWC 데이터 저장
    private var useSample: Boolean = true // 샘플 데이터 사용 여부

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

        // Helper 초기화
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getRWCHelper = GetRWCHelper(this, fusedLocationClient)
        mainUIHelper = MainUIHelper(this)
        apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        refreshWeatherData()

        // navigationBarBtn1 이벤트
        val navigationBarBtn1: Button = findViewById(R.id.navigationBarBtn1)
        navigationBarBtn1.setOnClickListener {
            // SharedPreferences 객체를 가져옴
            val clothingPrefs = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
            // ReviewPopup 생성 및 호출
            val reviewPopup = ReviewPopup(this, clothingPrefs)
            reviewPopup.show()
        }

        // navigationBarBtn2 이벤트
        val navigationBarBtn2: Button = findViewById(R.id.navigationBarBtn2)
        navigationBarBtn2.setOnClickListener {
            val intent = Intent(this, APITest2Activity::class.java)
            startActivity(intent)
        }

        // navigationBarBtn3 이벤트
        val navigationBarBtn3: Button = findViewById(R.id.navigationBarBtn3)
        navigationBarBtn3.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // AI 추천 요청 버튼 이벤트
        btnRequestAIRecommendation.setOnClickListener {
            requestAIRecommendation()
        }

        swipeRefreshLayout.setOnRefreshListener {
            refreshWeatherData()
        }
    }

    /**
     * AI 기반 추천 요청
     */
    private fun requestAIRecommendation() {
        if (useSample) {
            // 샘플 데이터 사용
            val sampleRecommendations = SampleAIRecommendation.createSampleRecommendations()
            val finalFromOfAIRecommendation = transformToClothingRecommendations(sampleRecommendations)
            populateClothingRecommendations(finalFromOfAIRecommendation)
        } else {
            // 실제 AI 추천 데이터 가져오기
            val loginPrefs = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
            val memberEmail = loginPrefs.getString("memberEmail", null)
            val userType = loginPrefs.getString("userType", null)

            if (memberEmail == null || userType == null) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.getAIRecommendation(memberEmail)
                    if (response.isSuccessful) {
                        val aiRecommendations = response.body()
                        aiRecommendations?.let {
                            val finalFromOfAIRecommendation = transformToClothingRecommendations(it)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "AI 추천 데이터를 성공적으로 가져왔습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                populateClothingRecommendations(finalFromOfAIRecommendation)
                            }
                        } ?: run {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "AI 추천 데이터를 가져올 수 없습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "AI 추천 요청 실패: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "오류 발생: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }


    /**
     * LearnedRecommendation 리스트를 ClothingRecommendation 리스트로 변환
     */
    private fun transformToClothingRecommendations(
        aiRecommendations: List<LearnedRecommendation>
    ): List<ClothingRecommendation> {
        return aiRecommendations.map { learnedRecommendation ->
            val recommendationsList = mainUIHelper.parseOptimizedClothing(learnedRecommendation.optimizedClothing)
            ClothingRecommendation(
                temperature = "${learnedRecommendation.temperature.toInt()}°C",
                recommendations = recommendationsList
            )
        }
    }


    /**
     * RWC 데이터를 가져와 UI에 반영
     */
    private fun fetchAndUpdateUI() {
        val message = if (useSample) "샘플 RWC 불러오는 중" else "서버에서 데이터 불러오는 중"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        getRWCHelper.fetchRWCFromGPS { response ->
            if (response != null) {
                rwcResponse = response
                updateUI(response)
            } else {
                Toast.makeText(this, "RWC 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    /**
     * UI 업데이트
     */
    private fun updateUI(response: RWCResponse) {
        mainUIHelper.updateCurrentWeatherUI(
            response, checkCurrentRegionMain, currentTempViewMain, currentWeatherViewMain
        )
        mainUIHelper.generateTimeWeatherLayout(response, timeWeatherContainer)
        response.clothingRecommendations?.let { populateClothingRecommendations(it) }
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
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
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
                textSize = 14f
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
