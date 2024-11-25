package com.example.weatherwear.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.weatherwear.R
import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.Clothing
import com.example.weatherwear.data.model.ClothingRecommendation
import com.example.weatherwear.data.model.Review
import com.example.weatherwear.util.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class ReviewPopup(context: Context) : Dialog(context) {
//
//    // Clothing ID는 내부에서 설정
//    private var clothingId: Int? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_review_popup)
//
//        // SharedPreferences에서 userEmail 가져오기
//        val loginPrefs = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
//        val userEmail = loginPrefs.getString("memberEmail", null)
//
//        /**
//         * 테스트용 ClothingSet 저장 및 SharedPreferences에서 가져오기
//         */
//        val testClothingSet = createTestClothingSet() // 테스트 데이터 생성
//        saveClothingSetToPreferences(testClothingSet) // SharedPreferences에 저장
//
//        val clothingSet = getClothingSetFromPreferences() // SharedPreferences에서 가져오기
//        if (clothingSet == null) {
//            Toast.makeText(context, "저장된 의류 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
//            dismiss() // 팝업 닫기
//            return
//        }
//
//        // UI 초기화
//        clothingId = clothingSet.id
//        populateClothingUI(clothingSet)
//
//        // Retrofit API 서비스 생성
//        val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
//
//        // 버튼 참조 및 이벤트 설정
//        findViewById<Button>(R.id.btn_cold).setOnClickListener {
//            sendReview(apiService, userEmail, clothingId, "추웠어요")
//        }
//        findViewById<Button>(R.id.btn_hot).setOnClickListener {
//            sendReview(apiService, userEmail, clothingId, "더웠어요")
//        }
//        findViewById<Button>(R.id.btn_good).setOnClickListener {
//            sendReview(apiService, userEmail, clothingId, "마음에 들어요")
//        }
//    }
//
//    /**
//     * SharedPreferences에 ClothingSet 저장
//     */
//    private fun saveClothingSetToPreferences(clothingRecommendation: ClothingRecommendation) {
//        val clothingPrefs = context.getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
//        val editor = clothingPrefs.edit()
//        val gson = Gson()
//        val clothingSetJson = gson.toJson(clothingRecommendation) // ClothingSet 객체를 JSON 문자열로 변환
//        editor.putString("clothingSet", clothingSetJson)
//        editor.apply()
//        Log.d("ReviewPopup", "ClothingSet이 SharedPreferences에 저장되었습니다.")
//    }
//
//    /**
//     * SharedPreferences에서 ClothingSet 가져오기
//     */
//    private fun getClothingSetFromPreferences(): ClothingRecommendation? {
//        val clothingPrefs = context.getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
//        val clothingSetJson = clothingPrefs.getString("clothingSet", null)
//
//        if (clothingSetJson.isNullOrEmpty()) {
//            Log.e("ReviewPopup", "ClothingPrefs에 저장된 데이터가 없습니다.")
//            return null
//        }
//
//        return try {
//            Gson().fromJson(clothingSetJson, ClothingRecommendation::class.java)
//        } catch (e: Exception) {
//            Log.e("ReviewPopup", "ClothingSet JSON 파싱 실패: ${e.message}")
//            null
//        }
//    }
//
//    /**
//     * UI에 의류 데이터를 동적으로 추가하는 함수
//     */
//    private fun populateClothingUI(clothingRecommendation: ClothingRecommendation) {
//        val container = findViewById<LinearLayout>(R.id.clothingContainer)
//        container.removeAllViews() // 기존 UI 제거
//
//        clothingRecommendation.recommendedClothings.forEach { clothing ->
//            // 동적 레이아웃 생성
//            val itemLayout = LinearLayout(context).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, // 콘텐츠 너비에 맞게
//                    ViewGroup.LayoutParams.WRAP_CONTENT // 콘텐츠 높이에 맞게
//                ).apply {
//                    setMargins(16, 16, 16, 16) // 마진 설정
//                }
//                orientation = LinearLayout.HORIZONTAL // 가로 정렬
//                gravity = Gravity.CENTER // 중앙 정렬
//            }
//
//            // ImageView 생성
//            val clothingImage = ImageView(context).apply {
//                layoutParams = LinearLayout.LayoutParams(400, 400).apply {
//                    setMargins(0, 0, 0, 0) // 이미지와 텍스트 간격 설정
//                }
//                setImageResource(R.drawable.t_shirt_100dp) // 기본 이미지 설정
//                setBackgroundColor(resources.getColor(R.color.superLightGray)) // 배경색 설정
//            }
//
//            // TextView 생성
//            val clothingName = TextView(context).apply {
//                layoutParams = LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                ).apply {
//                    setMargins(150, 0, 150, 0) // 좌우 마진 설정
//                }
//                text = clothing.name
//                textSize = 30f
//                gravity = Gravity.CENTER // 텍스트를 TextView 중앙에 정렬
//                setTextColor(resources.getColor(R.color.black)) // 텍스트 색상 설정
//            }
//
//            // 아이템 레이아웃에 추가
//            itemLayout.addView(clothingImage) // 왼쪽에 이미지
//            itemLayout.addView(clothingName) // 오른쪽에 텍스트
//
//            // 컨테이너에 추가
//            container.addView(itemLayout)
//        }
//
//        // 컨테이너의 부모 레이아웃 정렬 확인
//        container.gravity = Gravity.CENTER_HORIZONTAL // 아이템 전체를 가로 중앙 정렬
//    }
//
//    /**
//     * 리뷰 전송 함수
//     */
//    private fun sendReview(apiService: ApiService, userEmail: String?, clothingId: Int?, feedback: String) {
//        if (userEmail == null || clothingId == null) {
//            Toast.makeText(context, "리뷰 전송에 필요한 정보가 없습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val review = Review(clothingId = clothingId, feedback = feedback)
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = apiService.submitReview(userEmail, review)
//                CoroutineScope(Dispatchers.Main).launch {
//                    if (response.isSuccessful) {
//                        Toast.makeText(context, "리뷰가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show()
//                        dismiss() // 팝업 닫기
//                    } else {
//                        Toast.makeText(context, "리뷰 전송 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } catch (e: Exception) {
//                CoroutineScope(Dispatchers.Main).launch {
//                    Toast.makeText(context, "네트워크 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//                Log.e("ReviewPopup", "리뷰 전송 중 오류 발생: ${e.message}")
//            }
//        }
//    }
//}

/**
//     * 테스트용 ClothingSet 생성 함수
//     */
//    private fun createTestClothingSet(): ClothingRecommendation {
//        return ClothingRecommendation(
//            id = 1, // 테스트용 ID
//            recommendedClothings = listOf(
//                Clothing(name = "셔츠1", type = "상의"),
//                Clothing(name = "바지1", type = "하의"),
//                Clothing(name = "재킷1", type = "아우터"),
//                Clothing(name = "셔츠2", type = "상의"),
//                Clothing(name = "바지2", type = "하의"),
//                Clothing(name = "재킷2", type = "아우터")
//            )
//        )
//    }
