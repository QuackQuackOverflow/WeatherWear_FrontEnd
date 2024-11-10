package com.example.weatherwear.data.api

import com.example.weatherwear.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// 서버와의 통신을 위한 API 요청 정의

interface ApiService {
    // 로그인 요청을 서버로 보냄
    @POST("api/login")
    suspend fun loginUser(@Body user: User): Response<User>

    // 회원가입 요청을 서버로 보냄
    @POST("api/register")
    suspend fun registerUser(@Body user: User): Response<User>
}
