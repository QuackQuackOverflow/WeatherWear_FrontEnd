package com.example.weatherwear.ui

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.RegionAndWeather
import com.example.weatherwear.data.model.Weather

class DetailedWeatherActivity : AppCompatActivity() {

    private lateinit var regionAndWeather: RegionAndWeather

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_weather)

        // Intent로 전달된 RegionAndWeather 객체를 가져옴
        regionAndWeather = intent.getSerializableExtra("regionWeather") as? RegionAndWeather
            ?: throw IllegalArgumentException("RegionAndWeather 객체가 전달되지 않았습니다.")

        // 작업 1: Toolbar TextView 설정
        val toolbarTextView = findViewById<TextView>(R.id.toolbarTextView).apply {
            text = "${regionAndWeather.regionName ?: "알 수 없는 지역"}\n 상세 예보"
        }

        // 작업 2: 현재 날씨 정보 설정
        val currentWeather = regionAndWeather.weather.firstOrNull()
        if (currentWeather != null) {
            setCurrentWeatherInfo(currentWeather)
        }

        // 작업 3: 상세 날씨 정보 설정
        populateDetailedWeatherInfo()
    }

    /**
     * 작업 2: 현재 날씨 정보를 설정합니다.
     */
    private fun setCurrentWeatherInfo(currentWeather: Weather) {
        findViewById<TextView>(R.id.currentTempView_detailed).apply {
            text = "${currentWeather.temp?.toInt() ?: "?"}°"
        }

        val weatherIconView = findViewById<ImageView>(R.id.currentWeatherView_detailed)
        weatherIconView.setImageResource(getWeatherIconResource(
            rainType = currentWeather.rainType ?: "0",
            skyCondition = currentWeather.skyCondition ?: "1"
        ))
    }

    /**
     * 작업 3: 상세 날씨 정보를 설정합니다.
     */
    private fun populateDetailedWeatherInfo() {
        val hourlyTempScrollView = findViewById<LinearLayout>(R.id.hourlyTempScrollView)
        val hourlyRainAmountScrollView = findViewById<LinearLayout>(R.id.hourlyRainAmountScrollView)
        val hourlyHumidScrollView = findViewById<LinearLayout>(R.id.hourlyHumidScrollView)
        val hourlyWindspeedScrollView = findViewById<LinearLayout>(R.id.hourlyWindspeedScrollView)

        // 기존 뷰 제거
        hourlyTempScrollView.removeAllViews()
        hourlyRainAmountScrollView.removeAllViews()
        hourlyHumidScrollView.removeAllViews()
        hourlyWindspeedScrollView.removeAllViews()

        var currentDate: String? = null

        // 각 Weather 데이터를 추가
        regionAndWeather.weather.forEachIndexed { index, weather ->
            val formattedTime = formatTime(weather.forecastTime)
            val formattedDate = formatDate(weather.forecastDate)

            // 날짜가 변경된 경우 회색 배경의 날짜 표시 RelativeLayout 추가
            if (currentDate != weather.forecastDate) {
                currentDate = weather.forecastDate
                addDateHeaderToScrollView(hourlyTempScrollView, formattedDate)
                addDateHeaderToScrollView(hourlyRainAmountScrollView, formattedDate)
                addDateHeaderToScrollView(hourlyHumidScrollView, formattedDate)
                addDateHeaderToScrollView(hourlyWindspeedScrollView, formattedDate)
            }

            // 시간별 데이터를 추가
            addWeatherDataToScrollView(hourlyTempScrollView, formattedTime, "${weather.temp?.toInt() ?: "?"}°C", weather, addIcon = true)
            addWeatherDataToScrollView(hourlyRainAmountScrollView, formattedTime, "${weather.rainAmount?.toInt() ?: "?"}mm", weather, addIcon = false)
            addWeatherDataToScrollView(hourlyHumidScrollView, formattedTime, "${weather.humid?.toInt() ?: "?"}%", weather, addIcon = false)
            addWeatherDataToScrollView(hourlyWindspeedScrollView, formattedTime, "${String.format("%.1f", weather.windSpeed ?: 0.0)}m/s", weather, addIcon = false)
        }
    }

    /**
     * ScrollView에 날짜 헤더를 추가하는 함수
     */
    private fun addDateHeaderToScrollView(container: LinearLayout, date: String, matchIconHeight: Boolean = false) {
        val dateLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 폭은 전체 화면에 맞춤
                if (matchIconHeight) (100 * resources.displayMetrics.density).toInt() else (80 * resources.displayMetrics.density).toInt() // 높이 조정
            ).apply {
                setMargins(
                    (16 * resources.displayMetrics.density).toInt(), // 좌측 마진 16dp
                    8, // 상단 마진 8dp
                    (16 * resources.displayMetrics.density).toInt(), // 우측 마진 16dp
                    8 // 하단 마진 8dp
                )
            }

            // 둥근 모서리 배경색 설정
            background = GradientDrawable().apply {
                setColor(resources.getColor(R.color.lightSkyblue, null)) // 배경색 lightSkyblue로 설정
                cornerRadius = 16 * resources.displayMetrics.density // 둥근 모서리 반경 16dp
            }
        }

        val dateTextView = TextView(this).apply {
            text = date // 헤더 날짜 텍스트
            textSize = 18f // 텍스트 크기
            setTextColor(resources.getColor(R.color.black, null)) // 텍스트 색상 black
            gravity = Gravity.CENTER // 텍스트 중앙 정렬
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL) // 수평 가운데 정렬
                addRule(RelativeLayout.CENTER_VERTICAL) // 수직 가운데 정렬
            }
        }

        // 텍스트 뷰를 레이아웃에 추가
        dateLayout.addView(dateTextView)

        // 전체 헤더를 컨테이너에 추가
        container.addView(dateLayout)
    }


    /**
     * ScrollView에 데이터를 추가하는 유틸리티 함수
     */
    private fun addWeatherDataToScrollView(container: LinearLayout, time: String, data: String, weather: Weather, addIcon: Boolean = false) {
        val itemLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                (80 * resources.displayMetrics.density).toInt(), // width in dp
                if (addIcon) (100 * resources.displayMetrics.density).toInt() else (80 * resources.displayMetrics.density).toInt() // 아이콘 포함 시 높이 증가
            ).apply {
                setMargins(18, 8, 18, 8)
            }

            // 둥근 모서리를 가진 배경 설정
            background = GradientDrawable().apply {
                setColor(resources.getColor(R.color.hourlyTempViewSkyblue, null)) // 배경색 설정
                cornerRadius = 16 * resources.displayMetrics.density // 둥근 모서리 반경 설정 (16dp)
            }
        }

        // 시간 텍스트
        val timeTextView = TextView(this).apply {
            id = View.generateViewId()
            text = time
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 4)
        }

        // 데이터 텍스트
        val dataTextView = TextView(this).apply {
            id = View.generateViewId()
            text = data
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(8, 4, 8, 8)
        }

        // 시간 텍스트 추가 (상단)
        val timeLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        }
        itemLayout.addView(timeTextView, timeLayoutParams)

        // 날씨 아이콘 추가 (중앙, 조건부)
        if (addIcon) {
            val weatherIconView = ImageView(this).apply {
                id = View.generateViewId()
                layoutParams = RelativeLayout.LayoutParams(
                    (40 * resources.displayMetrics.density).toInt(),
                    (40 * resources.displayMetrics.density).toInt()
                ).apply {
                    addRule(RelativeLayout.CENTER_IN_PARENT)
                }
                setImageResource(getWeatherIconResource(weather.rainType ?: "0", weather.skyCondition ?: "1"))
            }
            itemLayout.addView(weatherIconView)
        }

        // 데이터 텍스트 추가 (하단)
        val dataLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        }
        itemLayout.addView(dataTextView, dataLayoutParams)

        container.addView(itemLayout)
    }


    /**
     * 날짜 문자열을 "20241129" -> "11월\n29일"로 변환
     */
    private fun formatDate(date: String?): String {
        if (date.isNullOrEmpty() || date.length != 8) return "?"
        val month = date.substring(4, 6).toIntOrNull() ?: return "?"
        val day = date.substring(6, 8).toIntOrNull() ?: return "?"
        return "${month}월\n${day}일"
    }

    /**
     * ScrollView에 데이터를 추가하는 유틸리티 함수
     */
    private fun addWeatherDataToScrollView(container: LinearLayout, time: String, data: String, weather: Weather) {
        val itemLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                (80 * resources.displayMetrics.density).toInt(), // width in dp
                (100 * resources.displayMetrics.density).toInt()  // height in dp (아이콘 포함으로 높이 증가)
            ).apply {
                setMargins(8, 8, 8, 8)
            }

            // 둥근 모서리를 가진 배경 설정
            background = GradientDrawable().apply {
                setColor(resources.getColor(R.color.hourlyTempViewSkyblue, null)) // 배경색 설정
                cornerRadius = 16 * resources.displayMetrics.density // 둥근 모서리 반경 설정 (16dp)
            }
        }

        // 시간 텍스트
        val timeTextView = TextView(this).apply {
            id = View.generateViewId()
            text = time
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 4)
        }

        // 날씨 아이콘
        val weatherIconView = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                (40 * resources.displayMetrics.density).toInt(),
                (40 * resources.displayMetrics.density).toInt()
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            }
            setImageResource(getWeatherIconResource(weather.rainType ?: "0", weather.skyCondition ?: "1"))
        }

        // 데이터 텍스트
        val dataTextView = TextView(this).apply {
            id = View.generateViewId()
            text = data
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(8, 4, 8, 8)
        }

        // 시간 텍스트 추가 (상단)
        val timeLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        }
        itemLayout.addView(timeTextView, timeLayoutParams)

        // 날씨 아이콘 추가 (중앙)
        itemLayout.addView(weatherIconView)

        // 데이터 텍스트 추가 (하단)
        val dataLayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        }
        itemLayout.addView(dataTextView, dataLayoutParams)

        container.addView(itemLayout)
    }


    /**
     * 시간 문자열을 "1200" -> "12시"로 변환
     */
    private fun formatTime(time: String?): String {
        if (time.isNullOrEmpty() || time.length != 4) return "?"
        val hour = time.substring(0, 2).toIntOrNull() ?: return "?"
        return "${hour}시"
    }

    /**
     * 날씨 조건에 따른 아이콘 리소스를 반환합니다.
     */
    private fun getWeatherIconResource(rainType: String, skyCondition: String): Int {
        return when (rainType) {
            "1" -> R.drawable.weathericon_rainy_24dp
            "2" -> R.drawable.weathericon_rainyandsnowy_24dp
            "3" -> R.drawable.weathericon_snowy_24dp
            "4" -> R.drawable.weathericon_rainy_24dp
            else -> when (skyCondition) {
                "1" -> R.drawable.weathericon_sunny_24dp
                "3" -> R.drawable.weathericon_partlycloudy_24dp
                "4" -> R.drawable.weathericon_cloudy_24dp
                else -> R.drawable.baseline_question_mark_50dp_outline
            }
        }
    }
}
