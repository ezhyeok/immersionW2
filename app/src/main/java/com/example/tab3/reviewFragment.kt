package com.example.tab3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import com.example.tab3.databinding.FragmentDiaryAddBinding
import androidx.fragment.app.Fragment
import com.example.tab3.databinding.ReviewRvBinding
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.tab3.databinding.ProfileInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId

class reviewFragment: Fragment(){
    private var _binding: ReviewRvBinding? = null
    private val binding get() = _binding!!
    var db: AppDatabase? = null
    var reviewId=""
    var reviewImg=""
    var ClientLike=0
    companion object {
        fun newInstance(reviewId:String, reviewImg:String): reviewFragment {
            val fragment = reviewFragment()
            fragment.reviewId=reviewId
            fragment.reviewImg=reviewImg
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dateTimeString = "2023-07-08T14:30:00.000Z"
        Log.d("qqqqqqqqq", formatDateTime(dateTimeString))
        _binding = ReviewRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        db = AppDatabase.getInstance(requireContext())
        //binding.uName.text=
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        Glide.with(this)
            .load(reviewImg)
            .into(binding.reviewImg)
        binding.favorite1.setImageResource(R.drawable.star_empty)
        binding.favorite2.setImageResource(R.drawable.star_empty)
        binding.favorite3.setImageResource(R.drawable.star_empty)
        binding.favorite4.setImageResource(R.drawable.star_empty)
        binding.favorite5.setImageResource(R.drawable.star_empty)
        binding.rImage.setOnClickListener{

            childFragmentManager.beginTransaction()
                .replace(R.id.singleReviewPage, ProfileFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        binding.rLike.setOnClickListener{
            val apiService = RetrofitClient.apiService
            val call = apiService.toggleLike(reviewId!!, ClientData.uniqueId!!)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val reviewItems = response.body()
                        Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                        if(reviewItems!=null) {
                            binding.rlikeNum.text=reviewItems
                        }
                    } else {
                        Log.e("FetchReviewUrls", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
                }
            })

            if(ClientLike==0) {
                binding.rLike.setImageResource(R.drawable.heart_filled)
                ClientLike=1

            }else {
                binding.rLike.setImageResource(R.drawable.heart_empty)
                ClientLike=0

            }
        }
        val apiService = RetrofitClient.apiService
        Log.d("rwrrrr", "reviewId: $reviewId")
        val call = apiService.getReviewDetail(reviewId!!, ClientData.uniqueId!!)
        call.enqueue(object : Callback<ReviewDetail> {
            override fun onResponse(call: Call<ReviewDetail>, response: Response<ReviewDetail>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                    if(reviewItems!=null) {
                        var profile_image_url: String?=null
                        binding.uName.text = reviewItems.uniqueId
                        binding.storeName.text = reviewItems.restaurantName
                        Log.d("qqqqqqqqq", reviewItems.profile_img)
                        binding.dateName.text = formatDateTime(reviewItems.createdAt)
                        binding.rComment.text=reviewItems.reviewContent
                        Glide.with(requireContext())
                            .load(reviewItems.profile_img)
                            .apply(
                                RequestOptions()
                                    .transform(MultiTransformation(CenterCrop(), CircleCrop()))
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                            )
                            .into(binding.rImage)
                        if(reviewItems.uniqueId==ClientData.uniqueId)
                            binding.followingButton.text="나"
                        else if(reviewItems.follow=="true")
                            binding.followingButton.text="팔로잉"
                        else
                            binding.followingButton.text="팔로우"
                        if(reviewItems.clientLike=="true") {
                            binding.rLike.setImageResource(R.drawable.heart_filled)
                            ClientLike=1
                        }else
                            binding.rLike.setImageResource(R.drawable.heart_empty)
                        binding.rlikeNum.text=reviewItems.numLike
                        val rating=reviewItems.rating.toInt()
                        if(rating>=1)
                            binding.favorite1.setImageResource(R.drawable.star_filled)
                        if(rating>=2)
                            binding.favorite2.setImageResource(R.drawable.star_filled)
                        if(rating>=3)
                            binding.favorite3.setImageResource(R.drawable.star_filled)
                        if(rating>=4)
                            binding.favorite4.setImageResource(R.drawable.star_filled)
                        if(rating>=5)
                            binding.favorite5.setImageResource(R.drawable.star_filled)

                    }
                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ReviewDetail>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })

        return root
    }
    fun formatDateTime(dateTimeString: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd\nHH:mm:ss")

        // LocalDateTime으로 파싱
        val localDateTime = LocalDateTime.parse(dateTimeString, inputFormatter)

        // 원하는 형식으로 포맷
        return outputFormatter.format(localDateTime)
    }
}