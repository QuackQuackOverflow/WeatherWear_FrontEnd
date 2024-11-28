import android.content.Context
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LoginHelper(private val context: Context) {

    companion object {
        private const val KEY_ALIAS = "loginKeyAlias"
        private const val PREFS_NAME = "LoginPrefs"
        private const val AES_MODE = "AES/CBC/PKCS5Padding"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // AES 키 생성
    private fun generateKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128)
        return keyGen.generateKey()
    }

    // AES 키 로드
    private fun loadKey(): SecretKey {
        val storedKey = sharedPreferences.getString(KEY_ALIAS, null)
        return if (storedKey != null) {
            val keyBytes = Base64.decode(storedKey, Base64.DEFAULT)
            SecretKeySpec(keyBytes, "AES")
        } else {
            val newKey = generateKey()
            saveKey(newKey)
            newKey
        }
    }

    // AES 키 저장
    private fun saveKey(secretKey: SecretKey) {
        val editor = sharedPreferences.edit()
        val keyString = Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)
        editor.putString(KEY_ALIAS, keyString)
        editor.apply()
    }

    // 암호화
    fun encrypt(plainText: String): String {
        val secretKey = loadKey()
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        val ivAndEncrypted = iv + encryptedBytes
        return Base64.encodeToString(ivAndEncrypted, Base64.DEFAULT)
    }

    // 복호화
    fun decrypt(encryptedText: String): String {
        try {
            val secretKey = loadKey()
            val cipher = Cipher.getInstance(AES_MODE)
            val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)

            // IV와 암호화 데이터를 분리
            val iv = encryptedBytes.copyOfRange(0, 16) // AES는 16바이트 IV 사용
            val data = encryptedBytes.copyOfRange(16, encryptedBytes.size)

            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            val decryptedBytes = cipher.doFinal(data)
            return String(decryptedBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException("복호화에 실패했습니다.")
        }
    }

    // 로그인 정보 저장
    fun saveLoginInfo(email: String, password: String, name: String, userType: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", encrypt(password)) // 비밀번호 암호화 후 저장
        editor.putString("name", name)
        editor.putString("userType", userType)
        editor.apply()
    }

    // 로그인 정보 로드
    fun getLoginInfo(): LoginData? {
        val email = sharedPreferences.getString("email", null)
        val encryptedPassword = sharedPreferences.getString("password", null)
        val name = sharedPreferences.getString("name", null)
        val userType = sharedPreferences.getString("userType", null)

        return if (!email.isNullOrEmpty() && !encryptedPassword.isNullOrEmpty()) {
            try {
                val password = decrypt(encryptedPassword) // 비밀번호 복호화
                LoginData(email, password, name, userType)
            } catch (e: Exception) {
                e.printStackTrace()
                null // 복호화 실패 시 null 반환
            }
        } else {
            null
        }
    }

    // 로그인 정보 초기화
    fun clearLoginInfo() {
        val editor = sharedPreferences.edit()
        editor.clear() // 모든 데이터 제거
        editor.apply()
    }
}

// 로그인 데이터 클래스
data class LoginData(
    val email: String,
    val password: String,
    val name: String?,
    val userType: String?
)
