package com.example.tab3
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import ted.gun0912.clustering.clustering.TedClusterItem
import ted.gun0912.clustering.geometry.TedLatLng

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
data class ProfileItem(val uniqueId:String,val profileImg:String, val name: String)

@Parcelize
data class StoreItem(val restaurantId:String,val name:String,val starScore:String,val num:String,val reviewCount:String, val x:String?=null, val y:String?=null, val category_name:String?=null, val address_name:String?=null): TedClusterItem, Parcelable {
    override fun getTedLatLng(): TedLatLng {
        return TedLatLng((y?:"0").toDouble(), (x?:"0").toDouble())
    }
}

data class permissionResponse(val allow: Boolean, val userInfo: Profile?)

data class FollowNum(val followNum: String, val followingNum: String)

data class ReviewItem(val reviewId:String, val reviewImg:String)
data class ReviewRecycle(val reviewImg:String, val nickname: String, val reviewContent:String, val createdAt: String,  val rating:String)


data class ReviewDetail(val uniqueId:String, val rating:String, val reviewContent:String, val createdAt: String, val restaurantName:String, val profile_img: String, val follow: String, val clientLike:String, val numLike:String)

data class ResponseMessage(
    val message: String
)