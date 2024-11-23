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
    @POST("api/members")
    suspend fun registerUser(@Body user: User): Response<User>
    @POST("api/members/login")
    suspend fun loginUser(@Body user: User): Response<Member>

    /**
     * Region 을 보내고 필요한 데이터를 받는 API
     */
    @GET("api/weather")
    suspend fun getRWC
                (@Query("nx") nx: Int,
                 @Query("ny") ny: Int,
                 @Query("userType") userType: String
    ): Response<RWCResponse>

    @GET("api/weather")
    suspend fun getRegionAndWeather
                (@Query("nx") nx: Int,
                 @Query("ny") ny: Int
    ): Response<List<RWResponse>>

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
    ): Response<ClothingSet>



}
