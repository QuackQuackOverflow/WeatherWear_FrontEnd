package com.example.weatherwear.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.User
import com.example.weatherwear.repository.UserRepository
import com.example.weatherwear.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    // UserRepository 인스턴스를 생성하여 서버 통신 준비
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // UI 요소 가져오기
        val usernameEditText = findViewById<EditText>(R.id.editUsername)
        val idEditText = findViewById<EditText>(R.id.editID)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val coldRadioButton = findViewById<RadioButton>(R.id.rbtn_userFeelsColdWell)
        val averageRadioButton = findViewById<RadioButton>(R.id.rbtn_userFeelsAvg)
        val hotRadioButton = findViewById<RadioButton>(R.id.rbtn_userFeelsHotWell)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)

        // 기본값 설정: "평균"이 기본 선택
        averageRadioButton.isChecked = true

        // "계정 생성" 버튼 클릭 시 이벤트 처리
        createAccountButton.setOnClickListener {
            // 사용자가 입력한 값 가져오기
            val username = usernameEditText.text.toString().trim()
            val id = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // 온도 선호도 결정
            val temperaturePreference = when {
                coldRadioButton.isChecked -> "cold"
                averageRadioButton.isChecked -> "average"
                hotRadioButton.isChecked -> "hot"
                else -> "average" // 기본값
            }

            // 입력값 검증 후 회원가입 요청
            if (validateInputs(username, id, password)) {
                register(username, id, password, temperaturePreference)
            }
        }
    }

    // 사용자 입력값 검증 함수
    private fun validateInputs(username: String, id: String, password: String): Boolean {
        // 사용자 이름 검증
        if (username.isEmpty()) {
            Toast.makeText(this, "사용자 이름을 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        // ID 검증
        if (id.isEmpty()) {
            Toast.makeText(this, "ID를 입력하세요", Toast.LENGTH_SHORT).show()
            return false
        }
        // 비밀번호 검증 (6자 이상)
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
        temperaturePreference: String
    ) {
        // 서버로 전송할 User 객체 생성
        val newUser = User(
            memberName = username,
            memberEmail = id,
            memberPassword = password,
            userType = temperaturePreference
        )

        // ProgressBar 표시
        runOnUiThread { showLoading(findViewById(R.id.loadingProgressBar), true) }
        // 비동기 네트워크 요청 수행
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 회원가입 API 호출
                val response = userRepository.registerUser(newUser)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    runOnUiThread {
                        showLoading(findViewById(R.id.loadingProgressBar), false) // ProgressBar 숨김
                        if (responseBody != null) {
                            Toast.makeText(this@RegisterActivity, "회원가입 성공", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "회원가입 성공",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // 회원가입 성공 시 LoginActivity로 이동
                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                LoginActivity::class.java
                            ).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    runOnUiThread {
                        showLoading(findViewById(R.id.loadingProgressBar), false) // ProgressBar 숨김
                        Toast.makeText(
                            this@RegisterActivity,
                            "회원가입 실패: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showLoading(findViewById(R.id.loadingProgressBar), false) // ProgressBar 숨김
                    Toast.makeText(
                        this@RegisterActivity,
                        "네트워크 오류 발생: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // ProgressBar를 표시하거나 숨기는 함수
    private fun showLoading(loadingProgressBar: ProgressBar, isLoading: Boolean) {
        runOnUiThread {
            // isLoading 값에 따라 ProgressBar 표시 여부 변경
            loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
