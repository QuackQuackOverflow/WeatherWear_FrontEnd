package com.example.weatherwear.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.User
import com.example.weatherwear.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // UserRepository 인스턴스 생성 (서버 통신용)
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 사용자 입력 필드 가져오기
        val usernameEditText = findViewById<EditText>(R.id.editUsername)
        val idEditText = findViewById<EditText>(R.id.editID)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        // 온도 선호도 선택 라디오 버튼 가져오기
        val coldRadioButton = findViewById<RadioButton>(R.id.rbtn_userFeelsColdWell)
        val averageRadioButton = findViewById<RadioButton>(R.id.rbtn_userFeelsAvg)
        val hotRadioButton = findViewById<RadioButton>(R.id.rbtn_userFeelsHotWell)

        // 계정 생성 버튼 클릭 시 서버로 회원가입 요청 보내기
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val id = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // 사용자의 온도 선호도 선택 처리
            val temperaturePreference = when {
                coldRadioButton.isChecked -> "cold"
                averageRadioButton.isChecked -> "average"
                hotRadioButton.isChecked -> "hot"
                else -> "average" // 기본값 설정
            }

            // 회원가입 함수 호출
            register(username, id, password, temperaturePreference)
        }
    }

    // 회원가입 요청을 서버로 보내는 함수
    private fun register(username: String, id: String, password: String, temperaturePreference: String) {
        // User 객체를 생성하여 서버로 보낼 데이터 준비
        val newUser = User(username = username, id = id, password = password, temperaturePreference = temperaturePreference)

        // 비동기 작업 수행 (네트워크 요청)
        CoroutineScope(Dispatchers.IO).launch {
            val response = userRepository.registerUser(newUser)
            if (response.isSuccessful) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish() // 회원가입 완료 후 액티비티 종료
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "회원가입 실패: 다시 시도하세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
