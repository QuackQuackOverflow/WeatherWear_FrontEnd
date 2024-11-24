// DetailedWeatherActivity.kt

package com.example.weatherwear.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R

class DetailedWeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_weather)

        // 시간별 날씨 레이아웃을 생성하는 함수 호출
        generateHourlyWeatherLayout()
    }

    // 시간별 날씨 레이아웃을 동적으로 생성하는 함수
    private fun generateHourlyWeatherLayout() {
        // LinearLayout 참조
        val hourlyWeatherContainer = findViewById<LinearLayout>(R.id.hourlyWeatherContainer)

        // 기본 아이콘 및 기온 설정
        val defaultIcon = R.drawable.baseline_sunny_40dp_outline
        val defaultTemperature = "0°C"

        // 0시부터 23시까지 반복하여 레이아웃을 동적으로 생성
        for (hour in 0..23) {
            val hourLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(10, 0, 10, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // 시간 텍스트뷰
            val hourText = TextView(this).apply {
                text = "${hour}시"
                textSize = 20f
                gravity = Gravity.CENTER
            }

            // 날씨 아이콘 이미지뷰
            val weatherIcon = ImageView(this).apply {
                setImageResource(defaultIcon)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // 온도 텍스트뷰
            val tempText = TextView(this).apply {
                text = defaultTemperature
                textSize = 20f
                gravity = Gravity.CENTER
            }

            // hourLayout에 추가
            hourLayout.addView(hourText)
            hourLayout.addView(weatherIcon)
            hourLayout.addView(tempText)

            // hourlyWeatherContainer에 hourLayout 추가
            hourlyWeatherContainer.addView(hourLayout)
        }
    }
}
