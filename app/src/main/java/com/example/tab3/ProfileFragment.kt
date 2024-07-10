package com.example.tab3

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.tab3.databinding.ProfileInfoBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: ProfileInfoBinding? = null
    private val binding get() = _binding!!
    private var f=0
    private var _position: Int = 0
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var photoURI: Uri
    private lateinit var profileAdapter: ProfileAdapter
    val apiService = RetrofitClient.apiService
    private var ProfileData=ProfileItem(uniqueId = ImmData.uniqueId!!, profileImg = ImmData.profile_image_url!!, name = ImmData.nickname!!)
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
        fun newInstance(profileItem: ProfileItem): ProfileFragment {
            val fragment = ProfileFragment()
            fragment.ProfileData=profileItem
            return fragment
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val today = CalendarDay.today()
        _binding = ProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if(ProfileData.uniqueId==ClientData.uniqueId) {
            binding.followingButton.visibility = View.INVISIBLE
            binding.addReviewButton.visibility=View.VISIBLE
        }
        if(ProfileData.uniqueId!=ClientData.uniqueId) {
            binding.addReviewButton.visibility=View.GONE
        }

        val call3 = apiService.getFollowNum(ProfileData.uniqueId!!)
        call3.enqueue(object : Callback<FollowNum> {
            override fun onResponse(call: Call<FollowNum>, response: Response<FollowNum>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                    if(reviewItems!=null) {
                        binding.followNum.text=reviewItems.followNum
                        binding.followingNum.text=reviewItems.followingNum
                    }
                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FollowNum>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })
        val call1 = apiService.showFollow(ClientData.uniqueId!!, ProfileData.uniqueId!!)
        call1.enqueue(object : Callback<Boolean> {
            override fun onResponse(
                call1: Call<Boolean>,
                response: Response<Boolean>
            ) {
                Log.d("rqrrrrrr", "d1")
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("rqrrrrrr", "d2")

                    Log.d("rqrrrrrr", "$reviewItems")
                    if(reviewItems!!){
                        Log.d("rqrrrrrr", "d3")

                        binding.followingButton.visibility=View.VISIBLE
                        binding.followingButton.text="팔로잉"
                        f=1
                    }else{
                        Log.d("rqrrrrrr", "d4")

                        binding.followingButton.visibility=View.VISIBLE
                        binding.followingButton.text="팔로우"
                        f=0
                    }

                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call1: Call<Boolean>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })


        // Profile 이미지 설정
        Glide.with(requireContext())
            .load(ProfileData.profileImg)
            .apply(
                RequestOptions()
                    .transform(MultiTransformation(CenterCrop(), CircleCrop()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(binding.pImage)


        binding.uName.text = ProfileData.uniqueId
        binding.uNickName.text = ProfileData.name
        binding.followingButton.setOnClickListener{

            Log.d("MaaainActivity", "Success click")
            val call1 = if (f == 0) {
                apiService.addFollow(ClientData.uniqueId!!, ProfileData.uniqueId!!)
            } else {
                apiService.delFollow(ClientData.uniqueId!!, ProfileData.uniqueId!!)
            }

            call1.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                    if (response.isSuccessful) {
                        // 성공적인 응답 처리
                        f = 1 - f
                        if (binding.followingButton.text == "팔로우")
                            binding.followingButton.text = "팔로잉"
                        else if (binding.followingButton.text == "팔로잉")
                            binding.followingButton.text = "팔로우"

                        val call3 = apiService.getFollowNum(ProfileData.uniqueId!!)
                        call3.enqueue(object : Callback<FollowNum> {
                            override fun onResponse(call: Call<FollowNum>, response: Response<FollowNum>) {
                                if (response.isSuccessful) {
                                    val reviewItems = response.body()
                                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                                    if (reviewItems != null) {
                                        binding.followNum.text = reviewItems.followNum
                                        binding.followingNum.text = reviewItems.followingNum
                                    }
                                } else {
                                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                                }
                            }

                            override fun onFailure(call: Call<FollowNum>, t: Throwable) {
                                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
                            }
                        })

                        Log.d("MainActivity", "Success: ${response.body()?.message}")
                    } else {
                        // 실패한 응답 처리
                        Log.e("MainActivity", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    // 네트워크 실패 처리
                    Log.e("MainActivity", "Failure: ${t.message}")
                }
            })
        }
        binding.followNum.setOnClickListener {

            val call = apiService.getProfileSortFollow(ProfileData.uniqueId!!)
            call.enqueue(object : Callback<List<ProfileItem>> {
                override fun onResponse(
                    call: Call<List<ProfileItem>>,
                    response: Response<List<ProfileItem>>
                ) {
                    if (response.isSuccessful) {
                        val reviewItems = response.body()
                        Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                        val followFragment = FollowFragment.newInstance(reviewItems ?: emptyList(), 0)
                        binding.followingButton.visibility=View.GONE
                        childFragmentManager.beginTransaction()
                            .replace(R.id.originLayout, followFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Log.e("FetchReviewUrls", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<ProfileItem>>, t: Throwable) {
                    Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
                }
            })
        }
        binding.followingNum.setOnClickListener{
            val call = apiService.getProfileSort(ProfileData.uniqueId!!)
            call.enqueue(object : Callback<List<ProfileItem>> {
                override fun onResponse(call: Call<List<ProfileItem>>, response: Response<List<ProfileItem>>) {
                    if (response.isSuccessful) {
                        val reviewItems = response.body()
                        Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                        val followFragment = FollowFragment.newInstance(reviewItems?: emptyList(), 1)
                        binding.followingButton.visibility=View.GONE
                        childFragmentManager.beginTransaction()
                            .replace(R.id.originLayout, followFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Log.e("FetchReviewUrls", "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<ProfileItem>>, t: Throwable) {
                    Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
                }
            })
        }
        binding.addReviewButton.setOnClickListener {
            val newDiary = NewDiary.newInstance(today.year, today.month, today.day)
            binding.followingButton.visibility=View.GONE
            childFragmentManager.beginTransaction()
                .replace(R.id.originLayout, newDiary)
                .addToBackStack(null)
                .commit()
        }

        initRecyclerView()
        childFragmentManager.setFragmentResultListener("newDiaryClosed", this) { _, _ ->
            // newDiaryClosed 결과를 받으면 initRecyclerView() 실행
            Log.d("NNNNNNewdiaryClosed", "detected")
            initRecyclerView()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        return root
    }

    private fun initRecyclerView() {
        val call3 = apiService.getFollowNum(ProfileData.uniqueId!!)
        call3.enqueue(object : Callback<FollowNum> {
            override fun onResponse(call: Call<FollowNum>, response: Response<FollowNum>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                    if(reviewItems!=null) {
                        binding.followNum.text=reviewItems.followNum
                        binding.followingNum.text=reviewItems.followingNum
                    }
                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FollowNum>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })

        if(ProfileData.uniqueId==ClientData.uniqueId)
            binding.followingButton.visibility=View.INVISIBLE
        val call1 = apiService.showFollow(ClientData.uniqueId!!, ProfileData.uniqueId!!)
        call1.enqueue(object : Callback<Boolean> {
            override fun onResponse(
                call1: Call<Boolean>,
                response: Response<Boolean>
            ) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    if(reviewItems!!){
                        binding.followingButton.visibility=View.VISIBLE
                        binding.followingButton.text="팔로잉"
                        f=1
                    }else{
                        binding.followingButton.visibility=View.VISIBLE
                        binding.followingButton.text="팔로우"
                        f=0
                    }

                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call1: Call<Boolean>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })


        // Profile 이미지 설정
        Glide.with(requireContext())
            .load(ProfileData.profileImg)
            .apply(
                RequestOptions()
                    .transform(MultiTransformation(CenterCrop(), CircleCrop()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(binding.pImage)
        val call = apiService.getFollowNum(ProfileData.uniqueId!!)
        call.enqueue(object : Callback<FollowNum> {
            override fun onResponse(call: Call<FollowNum>, response: Response<FollowNum>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                    if(reviewItems!=null) {
                        binding.followNum.text=reviewItems.followNum
                        binding.followingNum.text=reviewItems.followingNum
                    }
                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FollowNum>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })


        profileAdapter = ProfileAdapter(
            object : ProfileAdapter.ImageClickListener {
                override fun onImageClick(position: Int) {
                    // 이미지 클릭시의 처리 로직
                    binding.followingButton.visibility=View.GONE
                    val reviewItem = profileAdapter.getItemAtPosition(position)

                    val newReviewFragment=reviewFragment.newInstance(reviewItem!!.reviewId, reviewItem!!.reviewImg)
                    //binding.originLayout.visibility=View.GONE
                    binding.followingButton.visibility=View.GONE
                    childFragmentManager.beginTransaction()
                        .replace(R.id.originLayout, newReviewFragment)
                        .addToBackStack(null)
                        .commit()
                    println("Clicked item: $position")
                    _position = position
                }
            }
        )

        binding.imageRecyclerView.apply {
            adapter = profileAdapter
            layoutManager = GridLayoutManager(context, 3)
        }

        fetchReviewItems()
    }


    private fun fetchReviewItems() {
        val call = apiService.getReviewImgs(ProfileData.uniqueId!!)

        call.enqueue(object : Callback<List<ReviewItem>> {
            override fun onResponse(call: Call<List<ReviewItem>>, response: Response<List<ReviewItem>>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
                    binding.postNum.text = (reviewItems?:emptyList()).size.toString()
                    if (reviewItems != null) {

                        profileAdapter.submitList(reviewItems)
                    }
                } else {
                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ReviewItem>>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.followingButton.visibility=View.INVISIBLE
        _binding = null
    }
}

