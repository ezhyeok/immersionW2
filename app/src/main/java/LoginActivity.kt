package com.example.tab3

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 카카오 SDK 초기화
        KakaoSdk.init(this, "bcde670c93a2b96ce96a58214d188a99")

        // 키 해시 얻기
        getKeyHash()

        val kakaoLoginButton: Button = findViewById(R.id.kakaoLoginButton)
        kakaoLoginButton.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                if (error != null) {
                    Toast.makeText(this, "Login Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.d("KakaoLogin", "Login Failed: ${error.message}")
                } else if (token != null) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    Log.d("KakaoLogin", "AccessToken: ${token.accessToken}")

                    // 로그인 성공 시 사용자 정보 요청
                    fetchUserProfile()

                    // 로그인 성공 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = String(Base64.encode(md.digest(), 0))
                Log.d("KeyHash", keyHash)
                Toast.makeText(this, "KeyHash: $keyHash", Toast.LENGTH_LONG).show()
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash", "Unable to get MessageDigest. signature=$e")
        } catch (e: Exception) {
            Log.e("KeyHash", "Unable to get PackageInfo. signature=$e")
        }
    }

    private fun fetchUserProfile() {
        UserApiClient.instance.me { user: User?, error: Throwable? ->
            if (error != null) {
                Toast.makeText(this, "Failed to get user info: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.d("KakaoUserInfo", "Failed to get user info: ${error.message}")
            } else if (user != null) {
                val nickname = user.kakaoAccount?.profile?.nickname
                Toast.makeText(this, "User: $nickname", Toast.LENGTH_SHORT).show()
                Log.d("KakaoUserInfo", "User: $nickname")
            }
        }
    }
}
