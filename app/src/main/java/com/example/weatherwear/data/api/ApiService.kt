package com.example.weatherwear.data.api

import RWCResponse
import com.example.weatherwear.data.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
    @POST("api/regions/details")
    suspend fun getRegionDetails(@Body region: Region): Response<RWCResponse>

    // 2. Region 객체를 보내고 지역 이름만 반환받는 API
    @POST("api/regions/name")
    suspend fun getRegionName(@Body region: Region): Response<Region>

    // 3. Region 객체를 보내고 날씨 정보만 반환받는 API
    @POST("api/regions/weather")
    suspend fun getWeather(@Body region: Region): Response<Weather>

    // 4. Region 객체를 보내고 의상 세트만 반환받는 API
    @POST("api/regions/clothing-set")
    suspend fun getClothingSet(@Body region: Region): Response<ClothingSet>

    /**
     * Review 관련 API
     */
    // 리뷰 데이터를 서버에 전송
    @POST("api/reviews")
    fun submitReviews(@Body reviews: List<Review>): Call<Void>
}
