package com.example.weatherwear.helpers

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.weatherwear.R
import com.example.weatherwear.data.model.*
import com.example.weatherwear.ui.ClothingPopup
import org.json.JSONArray

/**
 * 메인 UI 업데이트를 관리하는 헬퍼 클래스
 */
class MainUIHelper(private val context: Context) {

    // 아이콘 크기를 관리하는 변수
    val currentWeatherIconSize = 140 // 현재 날씨 아이콘 크기 (dp)
    val hourlyWeatherIconSize = 50  // 시간대별 날씨 아이콘 크기 (dp)

    /**
     * 현재 날씨 정보를 UI에 반영
     */
    fun updateCurrentWeatherUI(
        rwcResponse: RWCResponse,
        regionTextView: TextView,
        tempTextView: TextView,
        weatherIconView: ImageView
    ) {
        val regionAndWeather = rwcResponse.regionWeather

        regionAndWeather?.let { region ->
            // 지역 이름 및 온도 설정
            regionTextView.text = region.regionName
            val currentWeather = region.weather.firstOrNull()

            if (currentWeather != null) {
                tempTextView.text = "${currentWeather.temp?.toInt() ?: "?"}°"
                setWeatherIcon(
                    weatherIconView,
                    currentWeather.rainType ?: "0",
                    currentWeather.skyCondition ?: "1",
                    currentWeatherIconSize
                )
                adjustWeatherIconLayout(weatherIconView)
            } else {
                setEmptyWeatherInfo(tempTextView, weatherIconView)
            }
        } ?: setEmptyWeatherInfo(tempTextView, weatherIconView)
    }

    private fun adjustWeatherIconLayout(weatherIconView: ImageView) {
        if (weatherIconView.id == R.id.currentWeatherView_main) {
            val layoutParams = RelativeLayout.LayoutParams(
                (currentWeatherIconSize * context.resources.displayMetrics.density).toInt(),
                (currentWeatherIconSize * context.resources.displayMetrics.density).toInt()
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                topMargin = (70 * context.resources.displayMetrics.density).toInt()
                marginEnd = (30 * context.resources.displayMetrics.density).toInt()
            }
            weatherIconView.layoutParams = layoutParams
        }
    }

    private fun setEmptyWeatherInfo(tempTextView: TextView, weatherIconView: ImageView) {
        tempTextView.text = "온도 정보 없음"
        weatherIconView.setImageResource(R.drawable.baseline_question_mark_50dp_outline)
    }

    /**
     * 시간대별 날씨 정보를 UI에 반영
     */
    fun generateTimeWeatherLayout(
        rwcResponse: RWCResponse,
        timeWeatherContainer: LinearLayout
    ) {
        timeWeatherContainer.removeAllViews() // 기존 뷰 제거

        val weatherList = rwcResponse.regionWeather?.weather?.take(24)
        if (weatherList.isNullOrEmpty()) {
            timeWeatherContainer.addView(createEmptyMessage("시간대별 날씨 정보를 불러올 수 없습니다."))
        } else {
            weatherList.forEach { weather ->
                timeWeatherContainer.addView(createHourWeatherLayout(weather.forecastTime ?: "00:00", weather))
            }
        }
    }

    private fun createEmptyMessage(message: String): TextView {
        return TextView(context).apply {
            text = message
            textSize = 16f
        }
    }

    /**
     * 시간대별 날씨 레이아웃 생성
     */
    private fun createHourWeatherLayout(forecastTime: String, weather: Weather): RelativeLayout {
        return RelativeLayout(context).apply {
            layoutParams = createHourLayoutParams()

            // 둥근 테두리와 배경색 설정
            background = GradientDrawable().apply {
                setColor(context.getColor(R.color.hourlyTempViewSkyblue)) // 배경색
                cornerRadius = 16 * context.resources.displayMetrics.density // Corner radius in dp
            }

            // 시간 텍스트
            addView(createHourTextView(forecastTime), RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })

            // 날씨 아이콘
            addView(createWeatherIcon(weather), RelativeLayout.LayoutParams(
                (hourlyWeatherIconSize * context.resources.displayMetrics.density).toInt(),
                (hourlyWeatherIconSize * context.resources.displayMetrics.density).toInt()
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            })

            // 온도 텍스트
            addView(createTempTextView(weather), RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })
        }
    }


    private fun createHourTextView(forecastTime: String): TextView {
        return TextView(context).apply {
            text = "${forecastTime.substring(0, 2)}시"
            textSize = 20f
            gravity = android.view.Gravity.CENTER
        }
    }

    private fun createWeatherIcon(weather: Weather): ImageView {
        return ImageView(context).apply {
            setWeatherIcon(
                this,
                weather.rainType ?: "0",
                weather.skyCondition ?: "1",
                hourlyWeatherIconSize
            )
        }
    }

    private fun createTempTextView(weather: Weather): TextView {
        return TextView(context).apply {
            text = "${weather.temp?.toInt() ?: "?"}°"
            textSize = 23f
            gravity = android.view.Gravity.CENTER
        }
    }

    private fun createHourLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            (70 * context.resources.displayMetrics.density).toInt(),
            (120 * context.resources.displayMetrics.density).toInt()
        ).apply {
            setMargins(20, 0, 20, 0)
        }
    }

    /**
     * 날씨 조건에 따른 아이콘 리소스를 반환
     */
    fun getWeatherIconResource(rainType: String, skyCondition: String): Int {
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

    private fun setWeatherIcon(
        imageView: ImageView,
        rainType: String,
        skyCondition: String,
        iconSizeDp: Int
    ) {
        imageView.setImageResource(getWeatherIconResource(rainType, skyCondition))
        imageView.layoutParams = RelativeLayout.LayoutParams(
            (iconSizeDp * context.resources.displayMetrics.density).toInt(),
            (iconSizeDp * context.resources.displayMetrics.density).toInt()
        )
    }

    /**
     * JSON 문자열을 List<String>으로 변환
     */
    fun parseOptimizedClothing(jsonString: String): List<String> {
        val jsonArray = JSONArray(jsonString)
        return List(jsonArray.length()) { i ->
            val jsonObject = jsonArray.getJSONObject(i)
            "${jsonObject.getString("type")}-${jsonObject.getString("item")}"
        }
    }

    /**
     * RegionAndWeather 데이터를 받아 날짜별 요약 정보를 추가
     */
    fun addForecastSummary(regionAndWeather: RegionAndWeather, container: LinearLayout) {
        container.removeAllViews() // 기존 뷰 제거

        // 날씨 데이터 그룹화 (forecastDate 기준)
        val groupedByDate = regionAndWeather.weather.groupBy { it.forecastDate }

        groupedByDate.forEach { (date, weatherList) ->
            if (date == null || weatherList.isEmpty()) return@forEach

            // 가장 자주 등장하는 rainType 또는 skyCondition을 결정
            val commonRainType = weatherList.groupingBy { it.rainType }
                .eachCount().maxByOrNull { it.value }?.key ?: "0"
            val commonSkyCondition = weatherList.groupingBy { it.skyCondition }
                .eachCount().maxByOrNull { it.value }?.key ?: "1"

            // 해당 날짜의 minTemp와 maxTemp 찾기 (예보 시간이 1200인 경우)
            val noonWeather = weatherList.firstOrNull { it.forecastTime == "1200" }
            val minTemp = noonWeather?.minTemp?.toInt() ?: "?"
            val maxTemp = noonWeather?.maxTemp?.toInt() ?: "?"

            // RelativeLayout 생성
            val relativeLayout = RelativeLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 16, 8, 16) // 수직 마진 16으로 설정
                }

                // 모서리를 둥글게 하고 배경색을 설정
                background = GradientDrawable().apply {
                    setColor(context.getColor(R.color.skyblue)) // 배경색 skyblue
                    cornerRadius = 16 * context.resources.displayMetrics.density // 모서리 둥글기 (16dp)
                }
            }

            // 날짜 TextView
            val dateTextView = TextView(context).apply {
                text = "${date.substring(4, 6)}-${date.substring(6, 8)}" // "20241127" -> "11-27"
                textSize = 20f
                setTextColor(context.getColor(android.R.color.black))
                id = View.generateViewId()
            }
            relativeLayout.addView(dateTextView, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                addRule(RelativeLayout.CENTER_VERTICAL) // 수직으로 중앙 정렬
                setMargins(16, 0, 0, 0) // 좌측 마진만 유지
            })

            // 날씨 이미지
            val weatherImageView = ImageView(context).apply {
                setImageResource(getWeatherIconResource(commonRainType, commonSkyCondition))
                id = View.generateViewId()
            }
            relativeLayout.addView(weatherImageView, RelativeLayout.LayoutParams(
                100, 100 // 크기 설정
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_LEFT)   // 수평으로 중앙 정렬
                addRule(RelativeLayout.CENTER_VERTICAL)     // 수직으로 중앙 정렬
                setMargins(200, 0, 0, 0) // 왼쪽 여백 200 설정
            })

            // 기온 TextView
            val tempTextView = TextView(context).apply {
                text = "$maxTemp°C / $minTemp°C" // 최고기온 먼저 표시되도록 변경
                textSize = 16f
                setTextColor(context.getColor(android.R.color.black))
                id = View.generateViewId()
            }
            relativeLayout.addView(tempTextView, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.CENTER_VERTICAL) // 수직으로 중앙 정렬
                setMargins(16, 0, 16, 0) // 좌우 마진만 설정
            })

            // LinearLayout에 추가
            container.addView(relativeLayout)
        }
    }


    private fun createForecastSummaryLayout(date: String, weatherList: List<Weather>): RelativeLayout {
        val commonRainType = weatherList.groupingBy { it.rainType }.eachCount().maxByOrNull { it.value }?.key ?: "0"
        val commonSkyCondition = weatherList.groupingBy { it.skyCondition }.eachCount().maxByOrNull { it.value }?.key ?: "1"

        val minTemp = weatherList.minOfOrNull { it.minTemp?.toInt() ?: Int.MAX_VALUE } ?: "?"
        val maxTemp = weatherList.maxOfOrNull { it.maxTemp?.toInt() ?: Int.MIN_VALUE } ?: "?"

        return RelativeLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8, 16, 8, 16) }
            setBackgroundColor(context.getColor(R.color.skyblue))

            // 날짜 텍스트
            addView(createDateTextView(date), RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
                setMargins(16, 0, 0, 0)
            })

            // 날씨 아이콘
            addView(createWeatherIcon(commonRainType, commonSkyCondition), RelativeLayout.LayoutParams(
                100, 100
            ).apply { addRule(RelativeLayout.CENTER_HORIZONTAL) })

            // 기온 텍스트
            addView(createTempSummaryTextView(minTemp, maxTemp), RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                setMargins(0, 0, 16, 0)
            })
        }
    }

    private fun createDateTextView(date: String): TextView {
        return TextView(context).apply {
            text = "${date.substring(4, 6)}-${date.substring(6, 8)}"
            textSize = 20f
            setTextColor(context.getColor(android.R.color.black))
        }
    }

    private fun createWeatherIcon(rainType: String, skyCondition: String): ImageView {
        return ImageView(context).apply {
            setImageResource(getWeatherIconResource(rainType, skyCondition))
        }
    }

    private fun createTempSummaryTextView(minTemp: Any, maxTemp: Any): TextView {
        return TextView(context).apply {
            text = "$minTemp°C / $maxTemp°C"
            textSize = 16f
            setTextColor(context.getColor(android.R.color.black))
        }
    }
}
