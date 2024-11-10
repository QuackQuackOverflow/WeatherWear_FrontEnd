package com.example.weatherwear.util

import com.example.weatherwear.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit 인스턴스를 생성하고 관리하는 객체
object RetrofitInstance {
    // Retrofit 객체를 초기화하고 기본 설정을 구성.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("") // 백엔드 서버의 기본 URL을 설정.
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환을 위한 GSON 변환기를 추가.
            .build()
    }

    // API 인터페이스의 구현체를 제공.
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
