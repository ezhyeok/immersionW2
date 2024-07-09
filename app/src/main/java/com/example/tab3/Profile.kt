package com.example.tab3

data class Profile(
    val deviceId: String?,
    val kakaoId: String?,
    val uniqueId: String?,
    val nickname: String?,
    val profile_image_url: String?
)
data class UserInfoResponse(val exists: Boolean, val userInfo: Profile?, val imgUrl: String?, val name: String?, val kakaoId: String)
data class ImageCopyResponse(
    val success: Boolean,
    val imgUrl: String?
)
data class ProfileItem(val uniqueId:String,val profileImg:String)

data class StoreItem(val restaurantId:String,val name:String,val starScore:String,val num:String,val reviewCount:String)//float??

data class permissionResponse(val allow: Boolean, val userInfo: Profile?)

data class ReviewItem(val reviewId:String, val reviewImg:String)

data class ReviewDetail(val uniqueId:String, val rating:String, val reviewContent:String, val createdAt: String, val restaurantName:String, val profile_img: String, val follow: String, val clientLike:String, val numLike:String)

