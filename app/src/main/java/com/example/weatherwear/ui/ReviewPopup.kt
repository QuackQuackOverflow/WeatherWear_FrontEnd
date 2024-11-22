package com.example.weatherwear.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.ClothingSet
import com.example.weatherwear.data.model.Review
import com.example.weatherwear.util.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewPopup(context: Context) : Dialog(context) {

    private var clothingId: Int? = null // Clothing ID는 내부에서 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_popup)

        // SharedPreferences에서 userEmail 가져오기
        val loginPrefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val userEmail = loginPrefs.getString("memberEmail", null)

        // SharedPreferences에서 ClothingSet 가져오기
        val clothingPrefs = context.getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        val clothingSetJson = clothingPrefs.getString("clothingSet", null)

        if (clothingSetJson.isNullOrEmpty()) {
            Toast.makeText(context, "의류 세트 데이터가 없습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            dismiss() // 팝업 닫기
            return
        }

        val clothingSet: ClothingSet? = try {
            Gson().fromJson(clothingSetJson, ClothingSet::class.java)
        } catch (e: Exception) {
            Log.e("ReviewPopup", "ClothingSet JSON 파싱 실패: ${e.message}")
            null
        }

        if (clothingSet == null) {
            Toast.makeText(context, "의류 세트 데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            dismiss() // 팝업 닫기
            return
        }

        clothingId = clothingSet.id

        // UI에 동적으로 의류 리스트 추가
        val container = findViewById<LinearLayout>(R.id.clothingContainer)

        clothingSet.recommendedClothings.forEach { clothing ->
            // 동적 레이아웃 생성
            val itemLayout = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16) // 마진 설정
                }
                orientation = LinearLayout.HORIZONTAL
            }

            // ImageView 생성
            val clothingImage = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                    setMargins(0, 0, 16, 0) // 마진 설정
                }
                setImageResource(R.drawable.t_shirt_100dp) // 기본 이미지 설정
            }

            // TextView 생성
            val clothingName = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1f
                }
                text = clothing.name
                textSize = 18f
            }

            // 아이템 레이아웃에 추가
            itemLayout.addView(clothingImage)
            itemLayout.addView(clothingName)

            // 컨테이너에 추가
            container.addView(itemLayout)
        }

        // Retrofit API 서비스 생성
        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        // 버튼 참조 및 이벤트 설정
        findViewById<Button>(R.id.btn_cold).setOnClickListener { sendReview(apiService, userEmail, clothingId, "추웠어요") }
        findViewById<Button>(R.id.btn_hot).setOnClickListener { sendReview(apiService, userEmail, clothingId, "더웠어요") }
        findViewById<Button>(R.id.btn_good).setOnClickListener { sendReview(apiService, userEmail, clothingId, "마음에 들어요") }
    }

    private fun sendReview(apiService: ApiService, userEmail: String?, clothingId: Int?, feedback: String) {
        if (userEmail == null || clothingId == null) {
            Toast.makeText(context, "리뷰 전송에 필요한 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val review = Review(clothingId = clothingId, feedback = feedback)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.submitReview(userEmail, review)
                CoroutineScope(Dispatchers.Main).launch {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "리뷰가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                        dismiss() // 팝업 닫기
                    } else {
                        Toast.makeText(context, "리뷰 전송 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("ReviewPopup", "리뷰 전송 중 오류 발생: ${e.message}")
            }
        }
    }
}
