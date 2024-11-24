package com.example.weatherwear.helpers

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.weatherwear.R
import com.example.weatherwear.data.model.RWCResponse
import com.example.weatherwear.data.model.RegionAndWeather

/**
 * 메인 UI 업데이트를 관리하는 헬퍼 클래스
 */
class MainUIHelper(private val context: Context) {

    // 아이콘 크기를 관리하는 변수
    private val currentWeatherIconSize = 140 // 현재 날씨 아이콘 크기 (dp)
    private val hourlyWeatherIconSize = 45  // 시간대별 날씨 아이콘 크기 (dp)

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
        val firstRegionAndWeather = rwcResponse.regionAndWeather.firstOrNull()

        if (firstRegionAndWeather != null) {
            // 지역 이름과 온도 업데이트
            regionTextView.text = firstRegionAndWeather.regionName
            tempTextView.text = "${firstRegionAndWeather.weather.temp.toInt()}°"

            // 날씨 아이콘 업데이트
            setWeatherIcon(
                weatherIconView,
                firstRegionAndWeather.weather.rainType,
                firstRegionAndWeather.weather.skyCondition,
                currentWeatherIconSize // 현재 날씨 아이콘 크기
            )
        } else {
            // 데이터가 없을 경우 기본 메시지 표시
            regionTextView.text = "지역 정보 없음"
            tempTextView.text = "온도 정보 없음"
            weatherIconView.setImageResource(R.drawable.baseline_question_mark_50dp_outline)
        }
    }

    /**
     * 시간대별 날씨 정보를 UI에 반영
     * @param regionAndWeatherList 시간대별 날씨 데이터를 포함한 리스트
     * @param timeWeatherContainer 시간대별 날씨를 표시할 LinearLayout
     */
    fun generateTimeWeatherLayout(
        regionAndWeatherList: List<RegionAndWeather>,
        timeWeatherContainer: LinearLayout
    ) {
        timeWeatherContainer.removeAllViews() // 기존 뷰 제거

        // 첫 24개의 시간대별 데이터를 추가
        regionAndWeatherList.take(24).forEach { regionAndWeather ->
            val hourLayout = createHourWeatherLayout(regionAndWeather)
            timeWeatherContainer.addView(hourLayout)
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
        // 적절한 아이콘 리소스를 결정
        val iconRes = getWeatherIconResource(rainType, skyCondition)

        // Drawable 리소스를 설정
        imageView.setImageResource(iconRes)

        // 기본 LayoutParams 확인 및 설정
        if (imageView.layoutParams == null) {
            // 만약 layoutParams가 null이라면 기본 RelativeLayout.LayoutParams 생성
            imageView.layoutParams = RelativeLayout.LayoutParams(
                (iconSizeDp * context.resources.displayMetrics.density).toInt(),
                (iconSizeDp * context.resources.displayMetrics.density).toInt()
            )
        } else {
            // layoutParams가 존재하면 크기만 수정
            imageView.layoutParams.width = (iconSizeDp * context.resources.displayMetrics.density).toInt()
            imageView.layoutParams.height = (iconSizeDp * context.resources.displayMetrics.density).toInt()
        }
    }

    /**
     * 날씨 조건에 따른 아이콘 리소스를 반환
     * @param rainType 강수 유형
     * @param skyCondition 하늘 상태
     * @return 아이콘 리소스 ID
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

    /**
     * 시간대별 날씨 레이아웃 생성
     * @param regionAndWeather 특정 시간대의 날씨 데이터
     * @return 시간대별 날씨를 표시하는 RelativeLayout
     */
    private fun createHourWeatherLayout(regionAndWeather: RegionAndWeather): RelativeLayout {
        return RelativeLayout(context).apply {
            // 레이아웃 설정
            layoutParams = createHourLayoutParams()

            // 배경색 설정
            setBackgroundColor(context.resources.getColor(R.color.hourlyTempViewSkyblue, null))

            // 시간 텍스트 추가
            addHourText(regionAndWeather)

            // 날씨 아이콘 추가
            addWeatherIcon(regionAndWeather)

            // 온도 텍스트 추가
            addTemperatureText(regionAndWeather)
        }
    }

    /**
     * 시간대별 레이아웃의 LayoutParams 생성
     * @return LinearLayout.LayoutParams 객체
     */
    private fun createHourLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            (60 * context.resources.displayMetrics.density).toInt(), // 50dp
            (120 * context.resources.displayMetrics.density).toInt() // 100dp
        ).apply {
            setMargins(20, 0, 20, 0) // 마진 설정
        }
    }

    /**
     * 시간 텍스트 추가
     * @param regionAndWeather 특정 시간대의 날씨 데이터
     */
    private fun RelativeLayout.addHourText(regionAndWeather: RegionAndWeather) {
        val hourText = TextView(context).apply {
            id = View.generateViewId()
            text = "${regionAndWeather.weather.forecastTime.substring(0, 2)}시"
            textSize = 20f
            gravity = android.view.Gravity.CENTER
        }
        addView(hourText, RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.CENTER_HORIZONTAL)
        })
    }

    /**
     * 날씨 아이콘 추가
     * @param regionAndWeather 특정 시간대의 날씨 데이터
     */
    private fun RelativeLayout.addWeatherIcon(regionAndWeather: RegionAndWeather) {
        val weatherIcon = ImageView(context).apply {
            id = View.generateViewId()
            setWeatherIcon(
                this,
                regionAndWeather.weather.rainType,
                regionAndWeather.weather.skyCondition,
                hourlyWeatherIconSize // 시간대별 아이콘 크기
            )
        }

        // 아이콘 크기를 명시적으로 설정
        val iconSizePx = (hourlyWeatherIconSize * context.resources.displayMetrics.density).toInt() // 30dp -> px 변환
        addView(weatherIcon, RelativeLayout.LayoutParams(
            iconSizePx,
            iconSizePx
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT)
        })
    }

    /**
     * 온도 텍스트 추가
     * @param regionAndWeather 특정 시간대의 날씨 데이터
     */
    private fun RelativeLayout.addTemperatureText(regionAndWeather: RegionAndWeather) {
        val tempText = TextView(context).apply {
            text = "${regionAndWeather.weather.temp.toInt()}°"
            textSize = 23f
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

    /**
     * RWCResponse 데이터를 기반으로 의상 추천을 LinearLayout에 표시
     * @param rwcResponse RWCResponse 객체
     * @param container 의상을 표시할 LinearLayout
     */
    fun populateClothingRecommendations(
        rwcResponse: RWCResponse,
        container: LinearLayout
    ) {
        container.removeAllViews() // 기존 뷰 제거

        val clothingSet = rwcResponse.clothingSet
        clothingSet.recommendedClothings.forEach { clothing ->
            // 개별 의상 아이템 레이아웃 생성
            val itemLayout = createClothingItemLayout(clothing.name)
            container.addView(itemLayout)
        }
    }

    /**
     * 개별 의상 아이템을 위한 레이아웃 생성
     * @param clothingName 의상 이름
     * @return LinearLayout 아이템 레이아웃
     */
    private fun createClothingItemLayout(clothingName: String): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    (50 * context.resources.displayMetrics.density).toInt(), // 좌측/우측 10dp 마진
                    0, // 상단 마진 없음
                    (50 * context.resources.displayMetrics.density).toInt(), // 좌측/우측 10dp 마진
                    0  // 하단 마진 없음
                )
            }
            gravity = android.view.Gravity.CENTER

            // 의상 이름 TextView 추가
            addView(createClothingTextView(clothingName))

            // 의상 이미지 ImageView 추가
            addView(createClothingImageView(clothingName))
        }
    }

    /**
     * 의상 이름 TextView 생성
     * @param clothingName 의상 이름
     * @return TextView
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
     * @param clothingName 의상 이름
     * @return ImageView
     */
    private fun createClothingImageView(clothingName: String): ImageView {
        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                (100 * context.resources.displayMetrics.density).toInt(), // 100dp
                (100 * context.resources.displayMetrics.density).toInt()  // 100dp
            )
            setImageResource(getClothingImageResource(clothingName))
        }
    }

    /**
     * 의상 이름에 따른 이미지 리소스 반환
     * @param clothingName 의상 이름
     * @return 리소스 ID
     */
    private fun getClothingImageResource(clothingName: String): Int {
        return when (clothingName) {

            /**
             * 여기에 옷 종류에 따라 이미지 Mapping
             */

            else -> R.drawable.t_shirt_100dp // 기본 이미지
        }
    }

}
