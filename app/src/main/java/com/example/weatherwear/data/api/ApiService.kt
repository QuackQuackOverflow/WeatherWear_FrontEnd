package com.example.weatherwear.data.api

import com.example.weatherwear.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // 회원가입 요청, POST 요청을 통해 사용자 정보를 서버로 전송.
    @POST("api/auth/register")
    suspend fun registerUser(@Body user: User): Response<User> // Response를 사용하여 비동기적으로 요청

    // 로그인 요청, POST 요청을 통해 사용자 인증 정보를 서버로 전송.
    @POST("api/auth/login")
    suspend fun loginUser(@Body user: User): Response<User> // Response를 사용하여 비동기적으로 요청

    // 단순히 HTTP GET을 요청하는 함수. (서버접속 테스트용)
    @GET("/")
    suspend fun testConnection(): Response<Void>

}
