package com.example.weatherwear.util

import com.example.weatherwear.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance 싱글톤 객체 : App 실행 중 단일 객체만 생성됨
object RetrofitInstance {

    //임시 url, network_security_config도 수정 해 줘야 함.
    private const val BASE_URL = "http://192.168.0.4:8000" // 임시 URL

    // 로깅 인터셉터를 사용하여 네트워크 요청/응답 로그를 확인
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청 및 응답의 상세 정보를 로그로 출력
    }

    // OkHttpClient에 로깅 인터셉터 추가
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // lazy 키워드를 사용하여 Retrofit 인스턴스를 초기화
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // OkHttpClient를 Retrofit에 연결
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API 서비스 인터페이스를 사용하여 Retrofit API 호출 생성
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
