package com.example.weatherwear.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.ui.account.LoginActivity

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
            "hotSensitive" -> "더위를 잘 탐"
            "coldSensitive" -> "추위를 잘 탐"
            "average" -> "평범한 체온 체질"
            else -> "알 수 없음"
        }

        // 사용자 정보를 UI에 반영
        findViewById<TextView>(R.id.userName).text = "이름: $userName"
        findViewById<TextView>(R.id.userEmail).text = "아이디: $userEmail"
        findViewById<TextView>(R.id.userType).text = "체질: $userTypeDisplay"

        // 로그아웃 버튼 이벤트 설정
        val buttonLogout: Button = findViewById(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            clearLoginInfo(loginedData)
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

            // LoginActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 기존 Activity 스택 초기화
            startActivity(intent)
            finish() // 현재 Activity 종료
        }

    }

    /**
     * SharedPreferences에 저장된 로그인 정보 초기화
     */
    private fun clearLoginInfo(loginPrefs: SharedPreferences) {
        val editor = loginPrefs.edit()
        editor.putString("memberEmail", "") // 이메일 초기화
        editor.putString("memberPassword", "") // 비밀번호 초기화
        editor.putString("memberName", "") // 이름 초기화
        editor.putString("userType", "") // 사용자 체질 타입 초기화
        editor.apply() // 변경 사항 저장
    }
}
