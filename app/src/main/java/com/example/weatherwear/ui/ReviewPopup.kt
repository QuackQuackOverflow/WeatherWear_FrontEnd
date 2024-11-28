package com.example.weatherwear.ui

import LoginHelper
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.*
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.Review
import com.example.weatherwear.util.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class ReviewPopup(context: Context, private val clothingPrefs: SharedPreferences) : Dialog(context) {

    private lateinit var clothingContainer: LinearLayout
    private lateinit var btnCold: Button
    private lateinit var btnHot: Button
    private lateinit var btnGood: Button
    private lateinit var tempTextView: TextView
    private val apiService: ApiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

    init {
        setContentView(R.layout.activity_review_popup)

        clothingContainer = findViewById(R.id.clothingContainer)
        btnCold = findViewById(R.id.btn_cold)
        btnHot = findViewById(R.id.btn_hot)
        btnGood = findViewById(R.id.btn_good)
        tempTextView = findViewById(R.id.tempTextView)

        // ClothingPrefs에서 데이터 로드
        loadClothingRecommendations()

        // 버튼 이벤트 설정
        setupButtonListeners()
    }

    private fun loadClothingRecommendations() {
        val allEntries = clothingPrefs.all
        if (allEntries.isNotEmpty()) {
            // SharedPreferences의 모든 데이터를 순회
            allEntries.forEach { (key, value) ->
                try {
                    // key가 숫자 형태인지 확인
                    if (key.toDoubleOrNull() != null) {
                        displayClothingRecommendationsForKey(key, value.toString())
                    }
                } catch (e: Exception) {
                    handleLoadingError(e) // JSON 파싱 실패 시 처리
                    return
                }
            }
        } else {
            handleEmptyPreferences() // 데이터가 없을 경우 처리
        }
    }

    private fun displayClothingRecommendationsForKey(key: String, value: String) {
        // key 값에서 "°C"가 이미 포함되어 있는지 확인
        val displayKey = if (key.contains("°C")) key else "$key°C"

        // 온도를 사용해 제목 설정
        tempTextView.text = "$displayKey 기준 옷차림 추천"

        // value를 JSON 배열로 처리
        try {
            val clothingArray = JSONArray(value) // JSON 배열로 변환
            for (i in 0 until clothingArray.length()) {
                val clothingItemText = clothingArray.getString(i) // JSON 배열에서 문자열 가져오기
                addClothingItemToContainer(clothingItemText) // UI에 추가
            }
        } catch (e: Exception) {
            handleLoadingError(e) // JSON 파싱 실패 시 처리
        }
    }

    private fun addClothingItemToContainer(clothingItemText: String) {
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(75, 20, 75, 20)
            }
            gravity = Gravity.CENTER_VERTICAL // 아이템을 수평 정렬

            // 둥근 모서리를 가진 배경 설정
            background = GradientDrawable().apply {
                setColor(context.resources.getColor(R.color.midLightSkyblue, null)) // 배경색 설정
                cornerRadius = 16 * context.resources.displayMetrics.density // Corner radius 설정 (16dp)
            }
        }

        // 이미지 추가 (추천 의류 이름에 따라 매핑)
        val imageView = ImageView(context).apply {
            val clothingName = extractClothingName(clothingItemText) // 이름에서 주요 항목 추출
            setImageResource(getClothingImageResource(clothingName))
            layoutParams = LinearLayout.LayoutParams(500, 500).apply { // 크기를 적절히 조정
                gravity = Gravity.CENTER_VERTICAL // 수직 정렬
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE

            // 이미지에 둥근 모서리를 추가
            background = GradientDrawable().apply {
                setColor(context.resources.getColor(R.color.superLightGray, null)) // 배경색 설정 (superLightGray)
                cornerRadius = 16 * context.resources.displayMetrics.density // Corner radius 설정 (16dp)
            }
        }
        itemLayout.addView(imageView)

        // 텍스트 추가 (추천 의류 이름)
        val textView = TextView(context).apply {
            text = formatClothingText(clothingItemText)
            textSize = 20f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                400,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(0, 0, 80, 0)
                setMargins(8, 0, 8, 0)
                gravity = Gravity.CENTER_VERTICAL // 수직 정렬
            }
        }
        itemLayout.addView(textView)

        // 항목 추가
        clothingContainer.addView(itemLayout)
    }

    private fun formatClothingText(clothingText: String): String {
        return clothingText.replace(" - ", "\n\n")
    }

    private fun extractClothingName(fullName: String): String {
        return fullName.substringAfterLast("-").trim()
    }

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
            else -> return context.resources.getIdentifier("t_shirt_100dp", "drawable", context.packageName) // 기본 이미지
        }

        // 리소스 이름을 기반으로 리소스 ID 반환
        return context.resources.getIdentifier("clothing_$resourceName", "drawable", context.packageName)
    }


    private fun handleEmptyPreferences() {
        Toast.makeText(context, "선택됐던 옷이 없습니다!", Toast.LENGTH_SHORT).show()
        dismiss() // 팝업 닫기
    }

    private fun handleLoadingError(e: Exception) {
        Toast.makeText(context, "옷차림 데이터를 불러오는 중 문제가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        dismiss() // 팝업 닫기
    }

    private fun setupButtonListeners() {
        btnCold.setOnClickListener { submitReview(-1) }
        btnHot.setOnClickListener { submitReview(1) }
        btnGood.setOnClickListener { submitReview(0) }
    }

    private fun submitReview(score: Int) {
        val allEntries = clothingPrefs.all
        if (allEntries.isNotEmpty()) {
            val loginHelper = LoginHelper(context)
            val loginData = loginHelper.getLoginInfo()

            if (loginData == null) {
                Toast.makeText(context, "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            allEntries.forEach { (key, _) ->
                val temp = key.toDoubleOrNull()
                if (temp != null) {
                    val review = Review(
                        memberEmail = loginData.email,
                        evaluationScore = score,
                        temp = temp
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = apiService.submitReview(review)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "리뷰가 전송되었습니다!", Toast.LENGTH_SHORT).show()
                                    clearClothingPrefs()
                                    dismiss()
                                } else {
                                    Toast.makeText(context, "리뷰 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "에러 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "온도 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "선택됐던 옷이 없습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearClothingPrefs() {
        val editor = clothingPrefs.edit()
        editor.clear() // 모든 데이터 삭제
        editor.apply()
    }
}
