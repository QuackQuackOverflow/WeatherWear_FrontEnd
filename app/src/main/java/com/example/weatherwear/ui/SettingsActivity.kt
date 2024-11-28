package com.example.weatherwear.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.ui.account.LoginActivity
import LoginHelper

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // LoginHelper 인스턴스 생성
        val loginHelper = LoginHelper(this)

        // LoginHelper에서 사용자 정보 가져오기
        val loginData = loginHelper.getLoginInfo()

        // 사용자 정보를 UI에 반영
        if (loginData != null) {
            val userTypeDisplay = when (loginData.userType?.lowercase()) {
                "hotsensitive" -> "더위를 잘 탐"
                "coldsensitive" -> "추위를 잘 탐"
                "average" -> "평범한 체온 체질"
                else -> "알 수 없음"
            }

            findViewById<TextView>(R.id.userName).text = "이름: ${loginData.name ?: "알 수 없음"}"
            findViewById<TextView>(R.id.userEmail).text = "아이디: ${loginData.email}"
            findViewById<TextView>(R.id.userType).text = "체질: $userTypeDisplay"
        } else {
            // 사용자 정보가 없는 경우 처리
            findViewById<TextView>(R.id.userName).text = "이름: 알 수 없음"
            findViewById<TextView>(R.id.userEmail).text = "아이디: 알 수 없음"
            findViewById<TextView>(R.id.userType).text = "체질: 알 수 없음"
        }

        // 로그아웃 버튼 이벤트 설정
        val buttonLogout: Button = findViewById(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            loginHelper.clearLoginInfo() // 로그인 정보 초기화
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

            // LoginActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 기존 Activity 스택 초기화
            startActivity(intent)
            finish() // 현재 Activity 종료
        }
    }
}
