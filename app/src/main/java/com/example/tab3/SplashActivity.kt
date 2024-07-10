package com.example.tab3

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import com.bumptech.glide.Glide
import retrofit2.Callback
import retrofit2.Response
import com.kakao.sdk.auth.TokenManagerProvider
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import okhttp3.ResponseBody
import okhttp3.MultipartBody
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException

data class TokenRequest(val token: String)
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_READ_MEDIA_IMAGES = 1001

    }

    lateinit var addProfile: ActivityResultLauncher<String>
    var kakaoId: String? = null
    var deviceId: String? = null

    private var latestItem: String =
        "http://34.125.165.162:3000/uploads/defaultprofile/ic_person.xml"
    private var latestUri: Uri? = null
    lateinit var dialogView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        addProfile =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    latestItem = it.toString()
                    latestUri = uri
                    Glide.with(this@SplashActivity)
                        .load(uri)
                        .apply(
                            RequestOptions().transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    CircleCrop()
                                )
                            )
                        )
                        .into(dialogView.findViewById<ImageView>(R.id.editImage))
                }
            }

        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
        //val backgroundImage: ImageView = findViewById(R.id.SplashScreenImage)
        //val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side)
        //backgroundImage.startAnimation(slideAnimation)

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        val kakaoAppKey = "eac2660fae734efa43c479c8ff043fd7"
        KakaoSdk.init(this, kakaoAppKey)//"YOUR_KAKAO_APP_KEY")
        Handler().postDelayed({
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                Log.d("ttoken Infom", "tokenInfo: $tokenInfo, error: $error")
                if (error != null) {
                    val kakaoLoginButton: ImageView = findViewById(R.id.kakaoLoginButton)
                    val noLoginButton: Button = findViewById(R.id.noLoginButton)
                    kakaoLoginButton.visibility=View.VISIBLE
                    noLoginButton.visibility=View.VISIBLE
                    kakaoLoginButton.setOnClickListener {
                        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                            if (error != null) {
                                Toast.makeText(this, "Login Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                            } else if (token != null) {
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                Log.d("KakaoLogin", "AccessToken: ${token.accessToken}")

                                // 백엔드로 액세스 토큰을 전달하는 코드 추가
                                Log.d("토큰정보", "$token")
                                sendTokenToServer(token.accessToken)
                            }
                        }
                    }
                    noLoginButton.setOnClickListener{
                       // kakaoId="guest"

                        showAddContactDialog("guest")
                    }

                } else if (tokenInfo != null) {
                    //
                    val token = TokenManagerProvider.instance.manager.getToken()?.accessToken
                    Log.d("ttoken Infom", "tokenInfo2: $token")
                    if (token != null)
                        sendTokenToServer(token)
                    //val intent = Intent(this, MainActivity::class.java)
                    //startActivity(intent)
                    //finish()
                }
            }
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            //finish()
        }, 2500) // 3000 is the delayed time in milliseconds.
    }

    private fun sendTokenToServer(token: String) {
        val apiService = RetrofitClient.apiService
        val call = apiService.sendKakaoToken(mapOf("token" to token))
        call.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val userInfoResponse = response.body()
                    if (userInfoResponse != null) {
                        Log.i("LLLoginActivity", "Response: $userInfoResponse")
                        if (userInfoResponse.exists) {
                            Log.d("LLLoginActivity", "User info: ${userInfoResponse.userInfo}")
                            Toast.makeText(
                                this@SplashActivity,
                                "User exists: ${userInfoResponse.userInfo}",
                                Toast.LENGTH_SHORT
                            ).show()
                            userInfoResponse.userInfo?.let { userInfo ->
                                ClientData.nickname = userInfo.nickname
                                ClientData.uniqueId = userInfo.uniqueId
                                ClientData.profile_image_url = userInfo.profile_image_url
                                Toast.makeText(this@SplashActivity, "Account allowed", Toast.LENGTH_SHORT).show()
                                Log.d("MainActivity", "Account allowed $ClientData")
                                ImmData.nickname = userInfo.nickname
                                ImmData.uniqueId = userInfo.uniqueId
                                ImmData.profile_image_url = userInfo.profile_image_url
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } ?: run {
                                Log.e("MainActivity", "User info is null")
                            }

                        } else {
                            Log.d("LLLoginActivity", "User does not exist")
                            Toast.makeText(
                                this@SplashActivity,
                                "User does not exist",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d("LLLoginActivity", "Image URL: $userInfoResponse")
                            userInfoResponse.imgUrl?.let { imageUrl ->
                                //val imageView: ImageView = findViewById(R.id.profileImageView)
                                //Glide.with(this@SplashActivity).load(imageUrl).into(imageView)
                                latestItem = imageUrl
                                Log.d("LLLoginActivity", "Image URL: $imageUrl")
                            }
                            kakaoId = userInfoResponse.kakaoId
                            showAddContactDialog(userInfoResponse.name)
                        }
                    }
                } else {
                    Log.e("LLLoginActivity", "Response failed: ${response.message()}")
                    Toast.makeText(
                        this@SplashActivity,
                        "Request failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("LLLoginActivity", "Request failed", t)
                Toast.makeText(
                    this@SplashActivity,
                    "Request failed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showAddContactDialog(name: String?) {
//        if(kakaoId=="guest")
//        {
//
//        }
        dialogView =
            LayoutInflater.from(this@SplashActivity).inflate(R.layout.dialog_add_contact, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
        var pUri: Uri? = null

        val editImage = dialogView.findViewById<ImageView>(R.id.editImage)
        var latestSelect: Int = 1

        Log.d("latestttt", "${latestItem::class}")
        Glide.with(this@SplashActivity)
            .load(latestItem)
            .apply(
                RequestOptions()
                    .transform(MultiTransformation(CenterCrop(), CircleCrop()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(dialogView.findViewById<ImageView>(R.id.editImage))
        if (name != null)
            editTextName.setText(name)
        editImage.setOnClickListener {
            val profileView =
                LayoutInflater.from(this@SplashActivity).inflate(R.layout.profile_select, null)
            val profile1 = profileView.findViewById<ImageView>(R.id.profile1)
            val profile2 = profileView.findViewById<ImageView>(R.id.profile2)
            val profile3 = profileView.findViewById<ImageView>(R.id.profile3)
            val profile4 = profileView.findViewById<ImageView>(R.id.profile4)
            val profile5 = profileView.findViewById<ImageView>(R.id.profile5)
            val profile6 = profileView.findViewById<ImageView>(R.id.profile6)
            val profile7 = profileView.findViewById<ImageView>(R.id.profile7)
            val profile8 = profileView.findViewById<ImageView>(R.id.profile8)
            val profilePlus = profileView.findViewById<ImageView>(R.id.profilePlus)
            val alertDialog = AlertDialog.Builder(this@SplashActivity)
                .setTitle("Select Profile")
                .setView(profileView)
                .setNegativeButton("Cancel", null)
                .create()
            profile1.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_01)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 1
                alertDialog.dismiss()
            }
            profile2.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_02)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 2
                alertDialog.dismiss()
            }
            profile3.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_03)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 3
                alertDialog.dismiss()
            }
            profile4.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_04)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 4
                alertDialog.dismiss()
            }
            profile5.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_05)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 5
                alertDialog.dismiss()
            }
            profile6.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_06)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 6
                alertDialog.dismiss()
            }
            profile7.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_07)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 7
                alertDialog.dismiss()
            }
            profile8.setOnClickListener {
                Glide.with(this@SplashActivity)
                    .load(R.drawable.ic_person_08)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                CircleCrop()
                            )
                        )
                    )
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect = 8
                alertDialog.dismiss()
            }
            profilePlus.setOnClickListener {
                checkProfilePermission()
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        AlertDialog.Builder(this@SplashActivity, R.style.customCheckboxFontStyle)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->

                val name = editTextName.text.toString()
                val number = editTextNumber.text.toString()

                if (name.isNotBlank() && number.isNotBlank()) {
                    //
                } else {
                    Toast.makeText(
                        this@SplashActivity,
                        "Please enter both name and phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val xx = kakaoId
                if (latestUri != null && xx != null)
                    uploadImageToServer(latestUri, xx)
                else if (xx != null)
                    uploadDefaultImageToServer(latestSelect, xx)
                if(name=="guest"){
                    kakaoId=editTextName.text.toString()
                }
                val apiService = RetrofitClient.apiService
                val request = mapOf("kakaoId" to (kakaoId?:""), "deviceId" to (deviceId?:""), "uniqueId" to editTextNumber.text.toString(), "nickname" to editTextName.text.toString(), "imageUrl" to "http://34.125.165.162:3000/uploads/kakaoprofile/${kakaoId}.jpg")
                val call=apiService.getpermission(request)
                call.enqueue(object : Callback<permissionResponse> {
                    override fun onResponse(call: Call<permissionResponse>, response: Response<permissionResponse>) {
                        if (response.isSuccessful) {
                            val imageCopyResponse = response.body()
                            if (imageCopyResponse != null) {
                                Log.i("MainActivity", "Response: $imageCopyResponse")
                                if (imageCopyResponse.allow) {
                                    imageCopyResponse.userInfo?.let { userInfo ->
                                        ClientData.nickname = userInfo.nickname
                                        ClientData.uniqueId = userInfo.uniqueId
                                        ClientData.profile_image_url = userInfo.profile_image_url
                                        ImmData.nickname = userInfo.nickname
                                        ImmData.uniqueId = userInfo.uniqueId
                                        ImmData.profile_image_url = userInfo.profile_image_url

                                        Toast.makeText(this@SplashActivity, "Account allowed", Toast.LENGTH_SHORT).show()
                                        Log.d("MainActivity", "Account allowed $ClientData")
                                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } ?: run {
                                        Log.e("MainActivity", "User info is null")
                                    }
                                    //Toast.makeText(this@MainActivity, "Image copied successfully: ${imageCopyResponse.imgUrl}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.d("MainActivity", "Image copy failed")
                                    //Toast.makeText(this@MainActivity, "Image copy failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("MainActivity", "Response failed: ${response.message()}")
                            //Toast.makeText(this@MainActivity, "Request failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }

                    }
                    override fun onFailure(call: Call<permissionResponse>, t: Throwable) {
                        Log.e("MainActivity", "Request failed", t)
                        //Toast.makeText(this@MainActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkProfilePermission() {
        return loadProfile()
        Log.d("DashboardFragment", "Image Clicked")
        when {
            ContextCompat.checkSelfPermission(
                this@SplashActivity,
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadProfile()
            }

            shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                showPermissionInfoDialog()
            }

            else -> {
                requestReadMediaImages()
            }
        }

    }

    private fun loadProfile() {
        Log.d("DashboardFragment", "loadImage called")
        return addProfile.launch("image/*")
    }

    private fun showPermissionInfoDialog() {
        AlertDialog.Builder(this@SplashActivity).apply {
            setMessage("이미지를 가져오기 위해 외부 저장소 읽기 권한이 필요합니다.")
            setNegativeButton("Cancel", null)
            setPositiveButton("OK") { _, _ ->
                requestReadMediaImages()
            }
        }.show()
    }

    private fun requestReadMediaImages() {
        requestPermissions(
            arrayOf(READ_EXTERNAL_STORAGE),
            REQUEST_READ_MEDIA_IMAGES
        )
    }

    private fun uploadDefaultImageToServer(defaultId: Int, kakaoId: String) {
        val apiService = RetrofitClient.apiService

        val request = mapOf("kakaoId" to kakaoId, "defaultId" to defaultId.toString())
        val call = apiService.copyDefaultImage(request)
        call.enqueue(object : Callback<ImageCopyResponse> {
            override fun onResponse(call: Call<ImageCopyResponse>, response: Response<ImageCopyResponse>) {
                if (response.isSuccessful) {
                    val imageCopyResponse = response.body()
                    if (imageCopyResponse != null) {
                        Log.i("MainActivity", "Response: $imageCopyResponse")
                        if (imageCopyResponse.success) {
                            Log.d("MainActivity", "Image URL: ${imageCopyResponse.imgUrl}")
                            //Toast.makeText(this@MainActivity, "Image copied successfully: ${imageCopyResponse.imgUrl}", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("MainActivity", "Image copy failed")
                            //Toast.makeText(this@MainActivity, "Image copy failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("MainActivity", "Response failed: ${response.message()}")
                    //Toast.makeText(this@MainActivity, "Request failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageCopyResponse>, t: Throwable) {
                Log.e("MainActivity", "Request failed", t)
                //Toast.makeText(this@MainActivity, "Request failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun uploadImageToServer(imageUri: Uri?, kakaoId: String) {
        if (imageUri == null) {
            Log.e("Upload", "Image URI is null")
            Toast.makeText(this@SplashActivity, "Image URI is null", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a temporary file
        val tempFile = createTempFile("upload", ".jpg", cacheDir)
        tempFile?.let { file ->
            val contentResolver = contentResolver
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = contentResolver.openInputStream(imageUri)
                outputStream = FileOutputStream(file)

                // Copy the input stream to the output stream
                inputStream?.copyTo(outputStream)

                val apiService = RetrofitClient.apiService
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val kakaoIdRequestBody = kakaoId.toRequestBody("text/plain".toMediaTypeOrNull())

                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val call = apiService.uploadImage(kakaoIdRequestBody, body)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("Upload", "Image upload successful")
                            Toast.makeText(
                                this@SplashActivity,
                                "Image upload successful",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Log.e("Upload", "Image upload failed: ${response.message()}")
                            Toast.makeText(
                                this@SplashActivity,
                                "Image upload failed: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("Upload", "Image upload failed", t)
                        Toast.makeText(
                            this@SplashActivity,
                            "Image upload failed: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } catch (e: IOException) {
                Log.e("Upload", "Failed to upload image", e)
                Toast.makeText(this@SplashActivity, "Failed to upload image", Toast.LENGTH_SHORT)
                    .show()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } ?: run {
            Log.e("Upload", "Failed to create temp file")
            Toast.makeText(this@SplashActivity, "Failed to create temp file", Toast.LENGTH_SHORT)
                .show()
        }
    }
}