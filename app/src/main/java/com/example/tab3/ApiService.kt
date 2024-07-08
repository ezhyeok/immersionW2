package com.example.tab3

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    //@Headers("Content-Type: application/json")
    @POST("/sendCode")
    fun sendKakaoToken(@Body token: Map<String, String>): Call<UserInfoResponse>


    @Multipart
    @POST("/upload/kakaoimage")
    fun uploadImage(
        @Part("kakaoId") kakaoId: RequestBody?,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @POST("/copyDefaultImage")
    fun copyDefaultImage(@Body request: Map<String, String>): Call<ImageCopyResponse>

    @POST("/permission")
    fun getpermission(@Body request: Map<String, String>): Call<permissionResponse>

    @Multipart
    @POST("/upload/review")
    fun uploadReview(
        @Part("uniqueId") uniqueId: RequestBody,
        @Part("restaurantId") restaurantId: RequestBody?,
        @Part("rating") rating: RequestBody,
        @Part("reviewContent") reviewContent: RequestBody?,
        @Part("name") name: RequestBody,
        @Part("category") category: RequestBody?,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("x") x: RequestBody,
        @Part("y") y: RequestBody,
        @Part reviewImage: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("/getReviewImg")
    fun getReviewImgs(@Query("uniqueId") uniqueId: String): Call<List<ReviewItem>>
    @GET("/getReviewNum")
    fun getReviewNum(@Query("uniqueId") uniqueId: String): Call<String>


}
