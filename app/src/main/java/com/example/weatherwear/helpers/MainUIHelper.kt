package com.example.weatherwear.helpers

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.weatherwear.R
import com.example.weatherwear.data.model.ClothingRecommendation
import com.example.weatherwear.data.model.RWCResponse
import com.example.weatherwear.data.model.RegionAndWeather
import com.example.weatherwear.data.model.Weather
import com.example.weatherwear.ui.ClothingPopup
import org.json.JSONArray

/**
 * 메인 UI 업데이트를 관리하는 헬퍼 클래스
 */
class MainUIHelper(private val context: Context) {

    // 아이콘 크기를 관리하는 변수
    public val currentWeatherIconSize = 140 // 현재 날씨 아이콘 크기 (dp)
    public val hourlyWeatherIconSize = 45  // 시간대별 날씨 아이콘 크기 (dp)

    /**
     * 현재 날씨 정보를 UI에 반영
     * @param rwcResponse 날씨 데이터를 포함한 RWCResponse 객체
     * @param regionTextView 지역 이름을 표시할 TextView
     * @param tempTextView 현재 온도를 표시할 TextView
     * @param weatherIconView 날씨 아이콘을 표시할 ImageView
     */
    fun updateCurrentWeatherUI(
        rwcResponse: RWCResponse,
        regionTextView: TextView,
        tempTextView: TextView,
        weatherIconView: ImageView
    ) {
        val regionAndWeather = rwcResponse.regionWeather
        regionAndWeather?.let {
            // 지역 이름과 온도 업데이트
            regionTextView.text = it.regionName
            val currentWeather = it.weather.firstOrNull()
            if (currentWeather != null) {
                tempTextView.text = "${currentWeather.temp?.toInt() ?: "?"}°"

                // 날씨 아이콘 업데이트
                setWeatherIcon(
                    weatherIconView,
                    currentWeather.rainType ?: "0",
                    currentWeather.skyCondition ?: "1",
                    currentWeatherIconSize
                )

                // 특정 ID를 가진 ImageView에만 속성을 추가 (currentWeatherView_Main)
                if (weatherIconView.id == R.id.currentWeatherView_main) {
                    val layoutParams = RelativeLayout.LayoutParams(
                        (currentWeatherIconSize * context.resources.displayMetrics.density).toInt(),
                        (currentWeatherIconSize * context.resources.displayMetrics.density).toInt()
                    ).apply {
                        addRule(RelativeLayout.ALIGN_PARENT_END) // android:layout_alignParentEnd="true"
                        topMargin =
                            (70 * context.resources.displayMetrics.density).toInt() // android:layout_marginTop="70dp"
                        marginEnd =
                            (30 * context.resources.displayMetrics.density).toInt() // android:layout_marginEnd="30dp"
                    }
                    weatherIconView.layoutParams = layoutParams
                }

            } else {
                tempTextView.text = "온도 정보 없음"
                weatherIconView.setImageResource(R.drawable.baseline_question_mark_50dp_outline)
            }
        } ?: run {
            // 데이터가 없을 경우 기본 메시지 표시
            regionTextView.text = "지역 정보 없음"
            tempTextView.text = "온도 정보 없음"
            weatherIconView.setImageResource(R.drawable.baseline_question_mark_50dp_outline)
        }
    }


    /**
     * 시간대별 날씨 정보를 UI에 반영
     * @param rwcResponse RWCResponse 객체
     * @param timeWeatherContainer 시간대별 날씨를 표시할 LinearLayout
     */
    fun generateTimeWeatherLayout(
        rwcResponse: RWCResponse,
        timeWeatherContainer: LinearLayout
    ) {
        val regionAndWeather = rwcResponse.regionWeather
        timeWeatherContainer.removeAllViews() // 기존 뷰 제거

        regionAndWeather?.weather?.take(24)?.forEach { weather ->
            val hourLayout = createHourWeatherLayout(weather.forecastTime ?: "00:00", weather)
            timeWeatherContainer.addView(hourLayout)
        } ?: run {
            // 데이터가 없을 경우 기본 메시지 표시
            val emptyText = TextView(context).apply {
                text = "시간대별 날씨 정보를 불러올 수 없습니다."
                textSize = 16f
            }
            timeWeatherContainer.addView(emptyText)
        }
    }

    /**
     * 날씨 조건에 따라 날씨 아이콘 설정
     * @param imageView 아이콘을 표시할 ImageView
     * @param rainType 강수 유형
     * @param skyCondition 하늘 상태
     * @param iconSizeDp 아이콘 크기(dp)
     */
    private fun setWeatherIcon(
        imageView: ImageView,
        rainType: String,
        skyCondition: String,
        iconSizeDp: Int
    ) {
        val iconRes = getWeatherIconResource(rainType, skyCondition)
        imageView.setImageResource(iconRes)

        imageView.layoutParams = RelativeLayout.LayoutParams(
            (iconSizeDp * context.resources.displayMetrics.density).toInt(),
            (iconSizeDp * context.resources.displayMetrics.density).toInt()
        )
    }

    /**
     * 날씨 조건에 따른 아이콘 리소스를 반환
     */
    public fun getWeatherIconResource(rainType: String, skyCondition: String): Int {
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

    /**
     * 시간대별 날씨 레이아웃 생성
     */
    private fun createHourWeatherLayout(forecastTime: String, weather: Weather): RelativeLayout {
        return RelativeLayout(context).apply {
            layoutParams = createHourLayoutParams()
            setBackgroundColor(context.resources.getColor(R.color.hourlyTempViewSkyblue, null))

            // 시간 텍스트 추가
            val hourText = TextView(context).apply {
                text = "${forecastTime.substring(0, 2)}시"
                textSize = 18f
                gravity = android.view.Gravity.CENTER
            }
            addView(hourText, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })

            // 날씨 아이콘 추가
            val weatherIcon = ImageView(context).apply {
                setWeatherIcon(
                    this,
                    weather.rainType ?: "0",
                    weather.skyCondition ?: "1",
                    hourlyWeatherIconSize
                )
            }
            addView(weatherIcon, RelativeLayout.LayoutParams(
                (hourlyWeatherIconSize * context.resources.displayMetrics.density).toInt(),
                (hourlyWeatherIconSize * context.resources.displayMetrics.density).toInt()
            ).apply {
                addRule(RelativeLayout.CENTER_IN_PARENT)
            })

            // 온도 텍스트 추가
            val tempText = TextView(context).apply {
                text = "${weather.temp?.toInt() ?: "?"}°"
                textSize = 20f
                gravity = android.view.Gravity.CENTER
            }
            addView(tempText, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
            })
        }
    }

    /**
     * 시간대별 레이아웃의 LayoutParams 생성
     */
    private fun createHourLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            (60 * context.resources.displayMetrics.density).toInt(),
            (100 * context.resources.displayMetrics.density).toInt()
        ).apply {
            setMargins(20, 0, 20, 0)
        }
    }

//    /**
//     * RWCResponse 데이터를 기반으로 의상 추천을 LinearLayout에 표시
//     * @param rwcResponse RWCResponse 객체
//     * @param container 의상을 표시할 LinearLayout
//     */
//    fun populateClothingRecommendations(
//        rwcResponse: RWCResponse,
//        container: LinearLayout
//    ) {
//        container.removeAllViews() // 기존 뷰 제거
//
//        rwcResponse.clothingRecommendations?.forEach { recommendation ->
//            // 각 의상 추천을 위한 레이아웃 생성
//            val itemLayout = createClothingItemLayout(recommendation)
//
//            // 레이아웃 클릭 시 팝업 표시
//            itemLayout.setOnClickListener {
//                showClothingPopup(recommendation) // 의상 팝업 표시
//            }
//
//            container.addView(itemLayout) // LinearLayout에 추가
//        }
//    }

//    /**
//     * 개별 의상 아이템을 위한 레이아웃 생성
//     */
//    private fun createClothingItemLayout(recommendation: ClothingRecommendation): LinearLayout {
//        return LinearLayout(context).apply {
//            orientation = LinearLayout.VERTICAL
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(
//                    (16 * context.resources.displayMetrics.density).toInt(),
//                    (8 * context.resources.displayMetrics.density).toInt(),
//                    (16 * context.resources.displayMetrics.density).toInt(),
//                    (8 * context.resources.displayMetrics.density).toInt()
//                )
//            }
//            gravity = android.view.Gravity.CENTER
//
//            // 의상 이미지 추가
//            val imageView = createClothingImageView("")
//            addView(imageView)
//
//            // 의상 온도 텍스트 추가
//            val textView = createClothingTextView(recommendation.temperature)
//            addView(textView)
//        }
//    }


    /**
     * 의상 이름 TextView 생성
     */
    private fun createClothingTextView(clothingName: String): TextView {
        return TextView(context).apply {
            text = clothingName
            textSize = 16f
            gravity = android.view.Gravity.CENTER
        }
    }

    /**
     * 의상 이미지 ImageView 생성
     */
    private fun createClothingImageView(clothingName: String): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (100 * context.resources.displayMetrics.density).toInt(),
                (100 * context.resources.displayMetrics.density).toInt()
            )
            setImageResource(R.drawable.t_shirt_100dp) // 기본 이미지 리소스
        }
    }

    private fun showClothingPopup(recommendation: ClothingRecommendation) {
        val popup = ClothingPopup(context, recommendation)
        popup.show()
    }

    // ㅈ같이 생긴 옷차림 추천 String을 List<String>으로 변환
    fun parseOptimizedClothing(jsonString: String): List<String> {
        val list = mutableListOf<String>()
        val jsonArray = JSONArray(jsonString) // JSON 배열로 변환
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val type = jsonObject.getString("type") // "type" 값 가져오기
            val item = jsonObject.getString("item") // "item" 값 가져오기
            list.add("$type-$item") // "type-item" 형태로 리스트에 추가
        }
        return list
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
                setBackgroundColor(context.getColor(R.color.skyblue)) // skyblue로 배경 설정
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
                text = "$minTemp°C / $maxTemp°C"
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


}
