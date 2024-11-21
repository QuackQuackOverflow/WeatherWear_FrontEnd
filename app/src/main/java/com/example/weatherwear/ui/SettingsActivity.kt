package com.example.weatherwear.ui

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // loginedData에서 사용자 정보 가져오기 (기본 값은 알 수 없음)
        val loginedData = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val userName = loginedData.getString("memberName", "알 수 없음") ?: "알 수 없음"
        val userEmail = loginedData.getString("memberEmail", "알 수 없음") ?: "알 수 없음"
        val userType = loginedData.getString("userType", "알 수 없음") ?: "알 수 없음"

        // 체질 정보를 명확한 한국어로 변환
        val userTypeDisplay = when (userType.lowercase()) {
            "hot" -> "더위를 잘 탐"
            "cold" -> "추위를 잘 탐"
            "average" -> "평범한 체온 체질"
            else -> "알 수 없음"
        }

        // 사용자 정보를 UI에 반영
        findViewById<TextView>(R.id.userName).text = "이름: $userName"
        findViewById<TextView>(R.id.userEmail).text = "아이디: $userEmail"
        findViewById<TextView>(R.id.userType).text = "체질: $userTypeDisplay"
    }
}
