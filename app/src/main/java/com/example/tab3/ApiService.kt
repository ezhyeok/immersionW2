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
    @GET("/getReviewSort")
    fun getReviewSort(): Call<List<ReviewItem>>

    @GET("/getUserData")//내가 팔로우하는 사람들 이미지
    fun getUserData(@Query("uniqueId") uniqueId:String):Call<ProfileItem>


    @GET("/getFollowingProfileSort")//내가 팔로우하는 사람들 이미지
    fun getProfileSort(@Query("uniqueId") uniqueId:String):Call<List<ProfileItem>>

    @GET("/getFollowProfileSort")//나를 팔로우하는 사람들 이미지
    fun getProfileSortFollow(@Query("uniqueId") uniqueId:String):Call<List<ProfileItem>>

    @GET("/getStore")
    fun getStore():Call<List<StoreItem>>

    @GET("/getFollowNum")
    fun getFollowNum(@Query("uniqueId") uniqueId: String):Call<FollowNum>

    @POST("/showFollow")
    fun showFollow(
        @Query("id") id: String,
        @Query("opid") opid: String
    ): Call<Boolean>

    @POST("/addFollow")
    fun addFollow(
        @Query("id") id: String,
        @Query("opid") opid: String
    ): Call<ResponseMessage>

    @POST("/delFollow")
    fun delFollow(
        @Query("id") id: String,
        @Query("opid") opid: String
    ): Call<ResponseMessage>

    @GET("/getReviewDetail")
    fun getReviewDetail(
        @Query("reviewId") reviewId: String,
        @Query("uniqueId") uniqueId: String
    ): Call<ReviewDetail>

    @POST("/toggleLike")
    fun toggleLike(
        @Query("reviewId") reviewId: String,
        @Query("uniqueId") uniqueId: String
    ): Call<String>

}

