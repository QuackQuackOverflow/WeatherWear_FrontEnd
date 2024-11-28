package com.example.weatherwear.ui

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
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
        titleTextView.text = "${recommendation.temperature} 기준 옷차림 추천"

        // 추천 리스트를 동적으로 추가
        val clothingContainer: LinearLayout = findViewById(R.id.clothingContainer)
        recommendation.recommendations.forEach { clothingItem ->
            addClothingItem(clothingContainer, clothingItem)
        }

        // "이렇게 입을게요!" 버튼 클릭 이벤트
        val selectButton: Button = findViewById(R.id.btn_selectThis)
        selectButton.setOnClickListener {
            saveToClothingPrefs(recommendation)
            dismiss()
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
                setMargins(75, 20, 75, 20)
            }
            gravity = Gravity.CENTER_VERTICAL // 아이템을 수평 정렬
        }

        // 이미지 추가 (추천 의류 이름에 따라 매핑)
        val imageView = ImageView(context).apply {
            val clothingName = extractClothingName(clothingItem) // 이름에서 주요 항목 추출
            setImageResource(getClothingImageResource(clothingName))
            layoutParams = LinearLayout.LayoutParams(500, 500).apply { // 크기를 적절히 조정
                gravity = Gravity.CENTER_VERTICAL // 수직 정렬
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE

            // 둥근 모서리를 가진 배경 설정
            background = GradientDrawable().apply {
                setColor(context.resources.getColor(R.color.superLightGray, null)) // 배경색 설정 (superLightGray)
                cornerRadius = 16 * context.resources.displayMetrics.density // Corner radius 설정 (16dp)
            }
        }
        itemLayout.addView(imageView)


        // 텍스트 추가 (추천 의류 이름)
        val textView = TextView(context).apply {
            text = formatClothingText(clothingItem)
            textSize = 20f
            gravity = Gravity.CENTER // 텍스트 중앙 정렬
            layoutParams = LinearLayout.LayoutParams(
                400,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(0, 0, 80, 0)
                setMargins(8, 0, 8, 0)
            }
        }
        itemLayout.addView(textView)

        // 항목 추가
        container.addView(itemLayout)
    }

    /**
     * "아우터 - 패딩" -> "아우터\n패딩"으로 변환하는 함수
     */
    private fun formatClothingText(clothingText: String): String {
        return clothingText.replace(" - ", "\n\n")
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
            else -> return context.resources.getIdentifier("t_shirt_100dp", "drawable", context.packageName)
        }

        // 리소스 이름을 기반으로 리소스 ID 반환
        return context.resources.getIdentifier("clothing_$resourceName", "drawable", context.packageName)
    }


    /**
     * `SharedPreferences`에 저장
     */
    private fun saveToClothingPrefs(recommendation: ClothingRecommendation) {
        val prefs = context.getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // 기존 데이터를 모두 삭제
        editor.clear()
        editor.apply()

        // 온도에서 °C 제거 후 Double로 변환
        val temperatureString = recommendation.temperature.replace("°C", "").trim() // °C 제거
        val temperature = temperatureString.toDoubleOrNull() // 숫자로 변환

        if (temperature != null) {
            // 새로운 데이터를 JSON 배열로 생성
            val clothingList = JSONArray(recommendation.recommendations)

            // SharedPreferences에 저장
            editor.putString(temperature.toString(), clothingList.toString())
            editor.apply()

            Toast.makeText(context, "옷차림이 저장되었습니다!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "온도 정보를 저장하는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
