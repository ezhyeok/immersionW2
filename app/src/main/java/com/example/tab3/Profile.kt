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
data class permissionResponse(val allow: Boolean, val userInfo: Profile?)