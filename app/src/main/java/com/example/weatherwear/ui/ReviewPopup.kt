package com.example.weatherwear.ui

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
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

    /**
     * 특정 키(온도)에 대한 의상 데이터를 UI에 표시
     */
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

    /**
     * 의상 데이터를 LinearLayout에 추가
     */
    private fun addClothingItemToContainer(clothingItemText: String) {
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            gravity = Gravity.CENTER_VERTICAL // 아이템을 수평 정렬
        }

        // 이미지 추가 (기본 티셔츠 이미지)
        val imageView = ImageView(context).apply {
            setImageResource(R.drawable.t_shirt_100dp)
            layoutParams = LinearLayout.LayoutParams(500, 500).apply { // 크기를 적절히 조정
                gravity = Gravity.CENTER_VERTICAL // 수직 정렬
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        itemLayout.addView(imageView)

        // 텍스트 추가 (추천 의류 이름)
        val textView = TextView(context).apply {
            text = clothingItemText
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 0, 0, 0)
                gravity = Gravity.CENTER_VERTICAL // 수직 정렬
            }
        }
        itemLayout.addView(textView)

        // 항목 추가
        clothingContainer.addView(itemLayout)
    }

    /**
     * SharedPreferences 데이터가 없을 경우 처리
     */
    private fun handleEmptyPreferences() {
        Toast.makeText(context, "선택됐던 옷이 없습니다!", Toast.LENGTH_SHORT).show()
        dismiss() // 팝업 닫기
    }

    /**
     * JSON 파싱 실패 시 처리
     */
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
            val memberEmail = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                .getString("memberEmail", null)
            if (memberEmail == null) {
                Toast.makeText(context, "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            allEntries.forEach { (key, _) ->
                val temp = key.toDoubleOrNull()
                if (temp != null) {
                    val review = Review(
                        memberEmail = memberEmail,
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
