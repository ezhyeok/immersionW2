package com.example.tab3

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
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

    private var _position: Int = 0
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var photoURI: Uri
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val today = CalendarDay.today()
        _binding = ProfileInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Profile 이미지 설정
        Glide.with(requireContext())
            .load(ClientData.profile_image_url)
            .apply(
                RequestOptions()
                    .transform(MultiTransformation(CenterCrop(), CircleCrop()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(binding.pImage)

        binding.uName.text = ProfileData.uniqueId
        binding.uNickName.text = ProfileData.nickname

        binding.addReviewButton.setOnClickListener {
            val newDiary = NewDiary.newInstance(today.year, today.month, today.day)
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
        return root
    }

    private fun initRecyclerView() {
        profileAdapter = ProfileAdapter(
            object : ProfileAdapter.ImageClickListener {
                override fun onImageClick(position: Int) {
                    // 이미지 클릭시의 처리 로직
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
        val apiService = RetrofitClient.apiService
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
        _binding = null
    }
}
