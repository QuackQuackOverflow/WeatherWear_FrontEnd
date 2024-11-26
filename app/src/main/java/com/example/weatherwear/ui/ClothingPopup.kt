package com.example.weatherwear.ui

import android.app.Dialog
import android.content.Context
import android.widget.*
import com.example.weatherwear.R
import com.example.weatherwear.data.model.ClothingRecommendation
import org.json.JSONArray

class ClothingPopup(
    context: Context,
    private val recommendation: ClothingRecommendation
) : Dialog(context) {

    init {
        setContentView(R.layout.activity_clothing_popup)

        // 제목 업데이트
        val titleTextView: TextView = findViewById(R.id.titleClothingPopup)
        titleTextView.text = "${recommendation.temperature}°C 기준 옷차림 추천"

        // 추천 리스트를 동적으로 추가
        val clothingContainer: LinearLayout = findViewById(R.id.clothingContainer)
        recommendation.recommendations.forEach { clothingItem ->
            addClothingItem(clothingContainer, clothingItem)
        }

        // "이렇게 입을게요!" 버튼 클릭 이벤트
        val selectButton: Button = findViewById(R.id.btn_selectThis)
        selectButton.setOnClickListener {
            saveToClothingPrefs(recommendation)
            dismiss() // 팝업 종료
        }
    }

    /**
     * 옷 항목을 수직 스크롤 뷰에 추가
     */
    private fun addClothingItem(container: LinearLayout, clothingItem: String) {
        val itemLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
        }

        // 이미지 추가 (기본 티셔츠 이미지)
        val imageView = ImageView(context).apply {
            setImageResource(R.drawable.t_shirt_100dp)
            layoutParams = LinearLayout.LayoutParams(100, 100)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        itemLayout.addView(imageView)

        // 텍스트 추가 (추천 의류 이름)
        val textView = TextView(context).apply {
            text = clothingItem
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 0, 0, 0)
            }
        }
        itemLayout.addView(textView)

        // 항목 추가
        container.addView(itemLayout)
    }

    /**
     * `SharedPreferences`에 저장
     */
    private fun saveToClothingPrefs(recommendation: ClothingRecommendation) {
        val prefs = context.getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 온도를 Double로 변환
        val temperature = recommendation.temperature.toDoubleOrNull()
        if (temperature != null) {
            // 기존 저장된 옷차림 데이터를 불러옴
            val existingData = prefs.getString(temperature.toString(), null)

            val clothingList = if (existingData != null) {
                // 기존 데이터가 있으면 JSON 배열로 변환하여 추가
                val jsonArray = JSONArray(existingData)
                recommendation.recommendations.forEach { jsonArray.put(it) }
                jsonArray
            } else {
                // 새로운 JSON 배열 생성
                JSONArray(recommendation.recommendations)
            }

            // SharedPreferences에 저장
            editor.putString(temperature.toString(), clothingList.toString())
            editor.apply()

            Toast.makeText(context, "옷차림이 저장되었습니다!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "온도 정보를 저장하는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
