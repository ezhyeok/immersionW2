package com.example.tab3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.security.MessageDigest
import android.util.Base64
import java.security.NoSuchAlgorithmException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        try {
            val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash:", keyHash)
                Toast.makeText(this, "KeyHash: $keyHash", Toast.LENGTH_LONG).show()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    // 카카오 SDK 초기화


        val kakaoLoginButton: Button = findViewById(R.id.kakaoLoginButton)
        kakaoLoginButton.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                if (error != null) {
                    Toast.makeText(this, "Login Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                } else if (token != null) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    Log.d("KakaoLogin", "AccessToken: ${token.accessToken}")

                    // 백엔드로 액세스 토큰을 전달하는 코드 추가
                    Log.d("토큰정보", "$token")
                    //getUserInfo(token.accessToken)
                    //sendTokenToServer(token.accessToken)

                    // 로그인 성공 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
    private fun getUserInfo(token: String) {
        val kakaoUserUrl = "https://kapi.kakao.com/v2/user/me"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(kakaoUserUrl)
            .header("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "User info request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("UUserInfo", "Responsedata $responseData")
                    try {
                        val jsonObject = JSONObject(responseData)
                        val id = jsonObject.getLong("id")
                        val nickname = jsonObject.getJSONObject("properties").getString("nickname")
                        //val email = jsonObject.getJSONObject("kakao_account").getString("email")

                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "User Info: $nickname", Toast.LENGTH_SHORT).show()
                            Log.d("UUserInfo", "json: $jsonObject")

                            Log.d("UUserInfo", "ID: $id, Nickname: $nickname")
                        }
                    } catch (e: Exception) {
                        Log.d("UUserInfo", "REQUEST failed1 $e")
                        e.printStackTrace()
                    }
                } else {
                    runOnUiThread {
                        Log.d("UUserInfo", "REQUEST failed")
                        Toast.makeText(this@LoginActivity, "User info request failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
/*
    private fun sendTokenToServer(token: String) {
        val apiService = RetrofitClient.apiService
        val call = apiService.sendKakaoToken(mapOf("token" to token))

        call.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i("MainActivity", "토큰 전송 성공")
                } else {
                    Log.e("MainActivity", "토큰 전송 실패: $response")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("MainActivity", "토큰 전송 오류", t)
            }
        })
    }

 */
}
