package com.example.weatherwear.util

import com.example.weatherwear.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance 싱글톤 객체는 애플리케이션에서 하나의 Retrofit 인스턴스를 공유하기 위해 사용됩니다.
object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.4:80" // 서버 URL로 변경 필요

    // lazy 키워드를 사용하여 Retrofit 인스턴스를 초기화합니다. 필요한 시점에만 초기화됨.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API 서비스 인터페이스를 사용하여 Retrofit API 호출을 만듭니다.
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

