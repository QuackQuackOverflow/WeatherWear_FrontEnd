package com.example.weatherwear.data.api

import com.example.weatherwear.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * ApiService: 앱의 모든 네트워크 API 호출을 관리하는 인터페이스.
 */
interface ApiService {

    /**
     * 서버 테스트용
     */
    @GET("/")
    suspend fun testConnection(): Response<Void>

    /**
     * 계정 관련 API
     */
    @POST("api/members")
    suspend fun registerUser(@Body user: User): Response<User>

    @POST("api/members/login")
    suspend fun loginUser(@Body user: User): Response<Member>

    /**
     * Region 을 보내고 필요한 데이터를 받는 API
     */
    @GET("api/clothing")
    suspend fun getRawRWC(
        @Query("nx") nx: Int,
        @Query("ny") ny: Int,
        @Query("userType") userType: String
    ): Response<List<Map<String, Any>>>

    @GET("api/weather")
    suspend fun getRegionAndWeather(
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Response<List<RegionAndWeather>>

    /**
     * Review 관련 API
     */
    @POST("/")
    suspend fun submitReview(
        @Query("userEmail") userEmail: String, // 쿼리 파라미터로 이메일 전송
        @Body review: Review                   // 요청 본문으로 Review 데이터 전송
    ): Response<Void>

    /**
     * userType을 보내고 ClothingSet을 반환받는 API
     */
    @GET("/")
    suspend fun getClothingSet(
        @Query("userType") userType: String
    ): Response<ClothingRecommendation>
}

// 확장 함수 정의
suspend fun ApiService.getRWC(
    nx: Int,
    ny: Int,
    userType: String
): RWCResponse? {
    val response = getRawRWC(nx, ny, userType) // ApiService의 기존 메서드 호출
    if (response.isSuccessful) {
        val rawResponse = response.body()
        return rawResponse?.let { parseRWCResponse(it) }
    }
    return null
}

/**
 * List<Map<String, Any>>를 RWCResponse로 변환
 */
private fun parseRWCResponse(response: List<Map<String, Any>>): RWCResponse? {
    if (response.isEmpty()) return null

    val regionWeatherMap = response[0]
    val regionWeather = regionWeatherMap["regionName"]?.let { regionName ->
        val weatherList = (regionWeatherMap["weather"] as? List<Map<String, Any>>)?.map { weatherMap ->
            Weather(
                id = weatherMap["id"] as? Int,
                forecastDate = weatherMap["forecastDate"] as? String,
                forecastTime = weatherMap["forecastTime"] as? String,
                temp = weatherMap["temp"] as? Double,
                minTemp = weatherMap["minTemp"] as? Double,
                maxTemp = weatherMap["maxTemp"] as? Double,
                rainAmount = weatherMap["rainAmount"] as? Double,
                humid = weatherMap["humid"] as? Double,
                windSpeed = weatherMap["windSpeed"] as? Double,
                rainProbability = weatherMap["rainProbability"] as? Double,
                rainType = weatherMap["rainType"] as? String,
                skyCondition = weatherMap["skyCondition"] as? String,
                lastUpdateTime = weatherMap["lastUpdateTime"] as? String
            )
        } ?: emptyList()

        RegionAndWeather(regionName = regionName as String, weather = weatherList)
    }

    val clothingRecommendations = response.drop(1).mapNotNull { clothingMap ->
        val temperature = clothingMap["temperature"] as? String
        val recommendations = clothingMap["recommendations"] as? List<String>
        if (temperature != null && recommendations != null) {
            ClothingRecommendation(temperature = temperature, recommendations = recommendations)
        } else {
            null
        }
    }

    return RWCResponse(regionWeather = regionWeather, clothingRecommendations = clothingRecommendations)
}
