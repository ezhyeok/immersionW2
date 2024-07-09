package com.example.tab3

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.example.tab3.databinding.FragmentDashboardBinding
import android.app.Activity
import com.example.tab3.ui.dashboard.DashboardViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment(){

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var _position:Int=0
    private var _position2:Int=0
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var photoURI: Uri
    private lateinit var imageModel: DashboardViewModel
    private lateinit var imageAdapter: ImageAdapter //ImageAdapter
    private lateinit var profileAdapter: ProfileAdapter

//    private val imageLoadLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
//        updateImages(uriList)
//    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//
//        imageModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
//            DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (findNavController().currentDestination?.id == R.id.navigation_dashboard) {
                    // 현재 프래그먼트가 DashboardFragment라면 아무 작업도 하지 않음 (혹은 원하는 다른 작업 수행)
                } else {
                    // 그렇지 않으면 기본 뒤로 가기 동작 수행
                    findNavController().navigateUp()
                }
            }
        })





        initRecyclerView()

        return root
    }


    private fun initRecyclerView() {

        profileAdapter=ProfileAdapter(//이게 리뷰들
            object:ProfileAdapter.ImageClickListener{
                override fun onImageClick(position:Int){
                    println("Clicked item:$position")
                    _position=position
                    val reviewItem = profileAdapter.getItemAtPosition(position)
                    val newReviewFragment=reviewFragment.newInstance(reviewItem!!.reviewId, reviewItem!!.reviewImg)
                    childFragmentManager.beginTransaction()
                        .replace(R.id.originLayout, newReviewFragment)
                        .addToBackStack(null)
                        .commit()
                    //updateSecondRecyclerView(position)

                }
            }
        )
        binding.imageRecyclerView.apply{
            adapter=profileAdapter
            layoutManager=GridLayoutManager(context,3)
        }
        imageAdapter=ImageAdapter(
            object:ImageAdapter.ImageClickListener{
                override fun onImageClick(position:Int){
                    println("Clicked item:$position")
                    _position2=position
                    updateSecondRecyclerView(position)

                }
            }
        )
        binding.profileRecyclerView.apply{
            adapter=imageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        fetchReviewItems()
        fetchProfileItems()
    }
    private fun fetchReviewItems() {
        val apiService = RetrofitClient.apiService
        val call = apiService.getReviewSort()

        call.enqueue(object : Callback<List<ReviewItem>> {
            override fun onResponse(call: Call<List<ReviewItem>>, response: Response<List<ReviewItem>>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    Log.d("FetchReviewUrls", "Review Items: $reviewItems")
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
    private fun fetchProfileItems() {
        val apiService = RetrofitClient.apiService
        val call = apiService.getProfileSort(ClientData.uniqueId!!)//수정해야됨

        call.enqueue(object : Callback<List<ProfileItem>> {
            override fun onResponse(call: Call<List<ProfileItem>>, response: Response<List<ProfileItem>>) {
                if (response.isSuccessful) {
                    val reviewItems = response.body()
                    if(reviewItems!=null) {
                        val mutableReviewItems = reviewItems.toMutableList()

                        // 새로운 항목 추가 (리스트의 첫 번째 위치에)
                        val newItem =
                            ClientData.profile_image_url?.let {
                                ProfileItem(
                                    ClientData.uniqueId!!,
                                    it,
                                    ClientData.nickname!!
                                )
                            }
                        if (newItem != null) {
                            mutableReviewItems.add(0, newItem)
                        }

                        // 어댑터에 업데이트된 리스트 전달
                        imageAdapter.submitList(mutableReviewItems)
                    }

                } else {
                    Log.e("FetchhReviewUrls", "Error: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<ProfileItem>>, t: Throwable) {
                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
            }
        })
//        val call = apiService.getProfileSort()//수정해야됨
//
//        call.enqueue(object : Callback<List<ProfileItem>> {
//            override fun onResponse(call: Call<List<ProfileItem>>, response: Response<List<ProfileItem>>) {
//                if (response.isSuccessful) {
//                    val profileItems = response.body()
//                    Log.d("FetchReviewUrls", "Review Items: $profileItems")
//                    if (profileItems != null) {
//
//                        imageAdapter.submitList(profileItems)
//                    }
//                } else {
//                    Log.e("FetchReviewUrls", "Error: ${response.message()}")
//                }
//            }
//            override fun onFailure(call: Call<List<ProfileItem>>, t: Throwable) {
//                Log.e("FetchReviewUrls", "Failed to fetch review URLs", t)
//            }
//        })
    }



//    private fun loadImage() {
//        imageLoadLauncher.launch("image/*")
//        Log.d("DashboardFragment", "loadImage called")
//    }





//    private fun updateImages(uriList: List<Uri>) {
//        imageModel.addImages(uriList)
//        imageModel.loadImages()
//        val images2 = imageModel.images.value?.map { ImageItems.Image(it) } ?: emptyList()
//        Log.d("updateImagesXX", "${images2}")
//        imageAdapter.submitList(images2)
//
//    }


    private fun toFrameActivity() {
        val images = imageAdapter.currentList.filterIsInstance<ImageItems.Image>().map { it.uri.toString() }.toTypedArray()
        val intent = Intent(requireContext(), FrameActivity::class.java)
            .putExtra("images", images)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateSecondRecyclerView(position: Int) {
        // 여기에 클릭한 아이템에 따라 두 번째 RecyclerView를 업데이트하는 코드를 작성합니다.
        // 예를 들어, position 값에 따라 다른 데이터를 가져오도록 설정할 수 있습니다.
        val selectedReviewItem = imageAdapter.currentList[position]

        // 예를 들어, 선택한 항목과 관련된 새로운 데이터를 가져오는 API 호출을 합니다.
        val apiService = RetrofitClient.apiService
        val call = apiService.getReviewImgs(selectedReviewItem.uniqueId!!) // 적절한 API 엔드포인트를 사용하세요.

        call.enqueue(object : Callback<List<ReviewItem>> {
            override fun onResponse(call: Call<List<ReviewItem>>, response: Response<List<ReviewItem>>) {
                if (response.isSuccessful) {
                    val relatedItems = response.body()
                    Log.d("FetchRrelatedItems", "Related Items: $relatedItems")
                    if (relatedItems != null) {
                        profileAdapter.submitList(relatedItems)
                    }
                } else {
                    Log.e("FetchRelatedItems", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ReviewItem>>, t: Throwable) {
                Log.e("FetchRelatedItems", "Failed to fetch related items", t)
            }
        })
    }
}