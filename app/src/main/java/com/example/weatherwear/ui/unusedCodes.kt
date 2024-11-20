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
