package com.example.weatherwear.repository

import com.example.weatherwear.data.api.ApiService
import com.example.weatherwear.data.model.User
import com.example.weatherwear.util.RetrofitInstance
import retrofit2.Response

// 사용자 관련 데이터를 관리하고 API 요청을 처리하는 리포지토리 클래스

class UserRepository {

    // Retrofit API 인스턴스
    private val api = RetrofitInstance.api

    // 서버에 로그인 요청을 보내는 함수
    suspend fun loginUser(user: User): Response<User> {
        return api.loginUser(user)
    }

    // 서버에 회원가입 요청을 보내는 함수
    suspend fun registerUser(user: User): Response<User> {
        return api.registerUser(user)
    }

    // 서버와의 연결을 테스트하는 함수
    suspend fun testConnection(): Response<Void> {
        return RetrofitInstance.api.testConnection()
    }

}
