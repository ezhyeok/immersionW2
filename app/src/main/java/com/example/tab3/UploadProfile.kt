package com.example.tab3
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.Multipart

interface UploadProfile {
    @Multipart
    @POST("/upload/kakaoimage")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("kakaoId") kakaoId: RequestBody
    ): Call<ResponseBody>
}