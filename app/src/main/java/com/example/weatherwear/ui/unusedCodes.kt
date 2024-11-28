package com.example.weatherwear.ui

//APITest2Activity.kt
/**
 *     // 의류 추천 데이터 가져오기
 *     private fun sendClothingSetRequest(nx: Int, ny: Int) {
 *         val apiService = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
 *         CoroutineScope(Dispatchers.IO).launch {
 *             val response = apiService.getClothingSet(nx, ny)
 *             withContext(Dispatchers.Main) {
 *                 if (response.isSuccessful) {
 *                     val clothingSet = response.body()
 *                     val sb = StringBuilder("의상 세트:\n")
 *                     clothingSet?.recommendedClothings?.forEach {
 *                         sb.append("${it.type}: ${it.name}\n")
 *                     }
 *                     resultTextView.text = sb.toString()
 *                 } else {
 *                     Toast.makeText(this@APITest2Activity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
 *                 }
 *             }
 *         }
 *     }
 */

//APIService.kt
/**
 *     // 2. Region 객체를 보내고 지역 이름만 반환받는 API
 *     @GET("api/weather")
 *     suspend fun getRegionName(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<GPSreport>
 *     // 3. Region 객체를 보내고 날씨 정보만 반환받는 API
 *     @GET("api/weather")
 *     suspend fun getWeather(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<Weather>
 *     // 4. Region 객체를 보내고 의상 세트만 반환받는 API
 *     @GET("api/weather")
 *     suspend fun getClothingSet(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<ClothingSet>
 */

//// navigationBarBtn1 이벤트
//val navigationBarBtn1: Button = findViewById(R.id.navigationBarBtn1)
//navigationBarBtn1.setOnClickListener {
//    // SharedPreferences 객체를 가져옴
//    val clothingPrefs = getSharedPreferences("ClothingPrefs", Context.MODE_PRIVATE)
//    // ReviewPopup 생성 및 호출
//    val reviewPopup = ReviewPopup(this, clothingPrefs)
//    reviewPopup.show()
//}
//
//// navigationBarBtn2 이벤트
//val navigationBarBtn2: Button = findViewById(R.id.navigationBarBtn2)
//navigationBarBtn2.setOnClickListener {
//    val intent = Intent(this, APITest2Activity::class.java)
//    startActivity(intent)
//}
//
//// navigationBarBtn3 이벤트
//val navigationBarBtn3: Button = findViewById(R.id.navigationBarBtn3)
//navigationBarBtn3.setOnClickListener {
//    val intent = Intent(this, SettingsActivity::class.java)
//    startActivity(intent)
//}

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

//    // 강수 형태를 텍스트로 변환하는 함수
//    private fun mapRainType(rainType: String?): String {
//        return when (rainType) {
//            "0" -> "없음"
//            "1" -> "비"
//            "2" -> "비/눈"
//            "3" -> "눈"
//            "4" -> "소나기"
//            else -> "정보 없음"
//        }
//    }
//
//    // 하늘 상태를 텍스트로 변환하는 함수
//    private fun mapSkyCondition(skyCondition: String?): String {
//        return when (skyCondition) {
//            "1" -> "맑음"
//            "3" -> "구름 많음"
//            "4" -> "흐림"
//            else -> "정보 없음"
//        }
//    }
