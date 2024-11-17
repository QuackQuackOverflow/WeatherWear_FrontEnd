package com.example.weatherwear.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
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

        // 로딩 ProgressBar 가져오기
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)

        // 계정 생성 버튼 가져오기
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)

        // 기본값 설정 (온도 선호도)
        coldRadioButton.isChecked = true // "추위를 잘 타요"를 기본값으로 설정

        // 계정 생성 버튼 클릭 이벤트 처리
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

            // 입력 검증
            if (!validateInputs(username, id, password)) return@setOnClickListener

            // 회원가입 요청
            register(username, id, password, temperaturePreference, loadingProgressBar)
        }
    }

    // 입력값 검증 함수
    private fun validateInputs(username: String, id: String, password: String): Boolean {
        if (username.isEmpty()) {
            Toast.makeText(this, "사용자 이름을 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        if (id.isEmpty()) {
            Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "비밀번호는 최소 6자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // 회원가입 요청을 서버로 보내는 함수
    private fun register(
        username: String,
        id: String,
        password: String,
        temperaturePreference: String,
        loadingProgressBar: ProgressBar
    ) {
        // User 객체를 생성하여 서버로 보낼 데이터 준비
        val newUser = User(
            username = username,
            id = id,
            password = password,
            temperaturePreference = temperaturePreference
        )

        // CoroutineScope를 사용하여 비동기 네트워크 요청 수행
        CoroutineScope(Dispatchers.IO).launch {
            // 로딩 시작
            showLoading(loadingProgressBar, true)

            try {
                val response = userRepository.registerUser(newUser)
                // 로딩 종료
                showLoading(loadingProgressBar, false)

                if (response.isSuccessful) {
                    // 회원가입 성공 시
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()

                        // 로그인 화면으로 이동
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish() // 회원가입 완료 후 현재 액티비티 종료
                    }
                } else {
                    // 서버 응답 실패 처리
                    val errorMessage = response.errorBody()?.string() ?: "회원가입 실패: 서버 에러"
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // 네트워크 오류 발생 처리
                showLoading(loadingProgressBar, false)
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "네트워크 오류 발생!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 로딩 ProgressBar 표시 함수
    private fun showLoading(loadingProgressBar: ProgressBar, isLoading: Boolean) {
        runOnUiThread {
            loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
