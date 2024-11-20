package com.example.weatherwear.data.api

import RWCResponse
import RWResponse
import android.graphics.Region
import com.example.weatherwear.data.model.*
import retrofit2.Call
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
    // 회원가입 요청: 사용자 정보를 서버에 전송하여 회원가입 처리
    @POST("api/members")
    suspend fun registerUser(@Body user: User): Response<User>
    // 로그인 요청: 사용자 인증 정보를 서버에 전송하여 로그인 처리
    @POST("api/members/login")
    suspend fun loginUser(@Body user: User): Response<User>

    /**
     * Region 관련 API
     */
    // 1. Region 객체를 보내고 지역 이름, 날씨 정보, 의상 세트를 포함한 RWCResponse를 받는 API
    @POST("api/weather")
    suspend fun getRegionDetails(@Body region: GPSreport): Response<RWCResponse>
    // 2. Region 객체를 보내고 지역 이름만 반환받는 API
    @GET("api/weather")
    suspend fun getRegionName(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<GPSreport>
    // 3. Region 객체를 보내고 날씨 정보만 반환받는 API
    @GET("api/weather")
    suspend fun getWeather(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<Weather>
    // 4. Region 객체를 보내고 의상 세트만 반환받는 API
    @GET("api/weather")
    suspend fun getClothingSet(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<ClothingSet>
    // 5. Region 객체를 보내고 지역과 날씨 반환받는 API
    @GET("api/weather")
    suspend fun getRegionAndWeather(@Query("nx") nx: Int, @Query("ny") ny: Int): Response<RWResponse>

    /**
     * Review 관련 API
     */
    // 리뷰 데이터를 서버에 전송
    @POST("/")
    fun submitReviews(@Body reviews: List<Review>): Call<Void>

    @POST("api/sendLocation")
    fun sendLocation(
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Call<RWCResponse>

}
