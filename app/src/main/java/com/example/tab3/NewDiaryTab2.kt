package com.example.tab3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.tab3.databinding.FragmentDiaryAddBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class NewDiaryTab2: Fragment() {
    private lateinit var binding: FragmentDiaryAddBinding
    lateinit var getDiary: ActivityResultLauncher<String>
    lateinit var mapActivityResultLauncher: ActivityResultLauncher<Intent>
    var db: AppDatabase? = null
    var DiaryData = mutableListOf<Diary>()
    var inputimage: Uri? = null
    var addYear = 0
    var addMonth = 0
    var addDay = 0
    var selectedStoreItem: StoreResponseItem? = null
    var place_name: String?=null
    var category_name: String?=null
    var address_name: String?=null
    var id: String?=null
    var phone: String?=null
    var x: Double?=null
    var y: Double?=null
    var latestRating:Int=0
    companion object {
        fun newInstance(year: Int, month: Int, day: Int): NewDiaryTab2 {
            val PICK_IMAGE_REQUEST = 1
            val fragment = NewDiaryTab2()
            fragment.addYear = year
            fragment.addMonth = month
            fragment.addDay = day
            return fragment
        }
        const val REQUEST_CODE=100
    }
    val selectedDate = "$addYear-$addMonth-$addDay"


    /*
    interface SaveClick {
        fun onSaveClick(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    }
    var saveClick: com.example.tab3.NewDiary.SaveClick? = null


     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiaryAddBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        Log.d("date", "$selectedDate")

        getDiary =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    inputimage = it
                    binding.diaryImage.setImageURI(it)
                }

            }
        binding.selectdiaryimg.setOnClickListener {
            openImagePicker()
        }
        binding.selectPosition.setOnClickListener{

            val i = Intent(requireContext(), MapActivity::class.java)
            startActivityForResult(i, REQUEST_CODE)

            /*
            val intent = Intent(requireContext(), MapActivity::class.java)
            mapActivityResultLauncher.launch(intent)

             */
        }
        binding.favorite1.setOnClickListener{
            binding.favorite1.setImageResource(R.drawable.star_filled)
            binding.favorite2.setImageResource(R.drawable.star_empty)
            binding.favorite3.setImageResource(R.drawable.star_empty)
            binding.favorite4.setImageResource(R.drawable.star_empty)
            binding.favorite5.setImageResource(R.drawable.star_empty)
        }
        binding.favorite2.setOnClickListener{
            binding.favorite1.setImageResource(R.drawable.star_filled)
            binding.favorite2.setImageResource(R.drawable.star_filled)
            binding.favorite3.setImageResource(R.drawable.star_empty)
            binding.favorite4.setImageResource(R.drawable.star_empty)
            binding.favorite5.setImageResource(R.drawable.star_empty)
        }
        binding.favorite3.setOnClickListener{
            binding.favorite1.setImageResource(R.drawable.star_filled)
            binding.favorite2.setImageResource(R.drawable.star_filled)
            binding.favorite3.setImageResource(R.drawable.star_filled)
            binding.favorite4.setImageResource(R.drawable.star_empty)
            binding.favorite5.setImageResource(R.drawable.star_empty)
        }
        binding.favorite4.setOnClickListener{
            binding.favorite1.setImageResource(R.drawable.star_filled)
            binding.favorite2.setImageResource(R.drawable.star_filled)
            binding.favorite3.setImageResource(R.drawable.star_filled)
            binding.favorite4.setImageResource(R.drawable.star_filled)
            binding.favorite5.setImageResource(R.drawable.star_empty)
        }
        binding.favorite5.setOnClickListener{
            binding.favorite1.setImageResource(R.drawable.star_filled)
            binding.favorite2.setImageResource(R.drawable.star_filled)
            binding.favorite3.setImageResource(R.drawable.star_filled)
            binding.favorite4.setImageResource(R.drawable.star_filled)
            binding.favorite5.setImageResource(R.drawable.star_filled)
        }


        binding.diarySave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                createWorkoutAndNavigateBack(inputimage)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navigateToCalendar()
        }

        return binding.root

    }

    private fun openImagePicker() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        intent.type = "image/*"
        getDiary.launch("image/*")
    }

    private fun createWorkoutAndNavigateBack(img: Uri?) {

        val diarytext = binding.diaryText.text.toString()
        val dateDiary = db?.diaryDao()?.getWorkoutByDate(addYear, addMonth, addDay)
        var selectedWorkout: Diary? = dateDiary?.firstOrNull()

        if(diarytext.isNotEmpty() || img!=null){
            uploadReviewToServer(img, diarytext)
        }else{

        }

        //update decorators when a new diary entry is added
        /*
        CoroutineScope(Dispatchers.Main).launch {
            (requireParentFragment() as ProfileFragment)
            parentFragmentManager.setFragmentResult("NewDiaryClosed", Bundle())
        }

         */
    }

    private fun navigateToCalendar() {
        parentFragmentManager.popBackStack()
    }

    private fun CheckDataBase(){
        val savedDiary = db?.diaryDao()?.getAll()?: emptyList()
        Log.d("savedDiary", savedDiary.toString())
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // 결과를 설정하여 부모 프래그먼트에 알림
        parentFragmentManager.setFragmentResult("newDiaryClosed", Bundle())
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            selectedStoreItem = data?.getParcelableExtra(StoreDetailActivity.STORE_ITEM)
            selectedStoreItem?.let {
                // Update UI with selected store item details
                //binding.storeName.text = it.place_name
                //binding.storeAddress.text = it.address_name
                place_name=it.place_name
                category_name=it.category_name
                address_name=it.address_name
                id=it.id
                phone=it.phone
                x=it.x.toDouble()
                y=it.y.toDouble()
            }
            val ppp=selectedStoreItem?.place_name
            if(ppp!=null)
                binding.storeName.text=ppp
        }
    }
    private fun uploadReviewToServer(imageUri: Uri?, reviewContent: String) {
        if (imageUri == null) {
            Log.e("Upload", "Image URI is null")
            Toast.makeText(requireContext(), "Image URI is null", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a temporary file
        val tempFile = createTempFile("upload", ".jpg", requireContext().cacheDir)
        tempFile?.let { file ->
            val contentResolver = requireContext().contentResolver
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = contentResolver.openInputStream(imageUri)
                outputStream = FileOutputStream(file)

                // Copy the input stream to the output stream
                inputStream?.copyTo(outputStream)

                val apiService = RetrofitClient.apiService
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val uniqueIdRequestBody = ClientData.uniqueId!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val restaurantIdRequestBody=(id?:"").toRequestBody("text/plain".toMediaTypeOrNull())
                val ratingRequestBody=latestRating.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val reviewContentRequestBody=reviewContent.toRequestBody("text/plain".toMediaTypeOrNull())
                val name=(place_name?:"").toRequestBody("text/plain".toMediaTypeOrNull())
                val category=(category_name?:"").toRequestBody("text/plain".toMediaTypeOrNull())
                val address=(address_name?:"").toRequestBody("text/plain".toMediaTypeOrNull())
                val phone=(phone?:"").toRequestBody("text/plain".toMediaTypeOrNull())
                val x=x.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val y=y.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("reviewImage", file.name, requestFile)
                val call = apiService.uploadReview(uniqueIdRequestBody, restaurantIdRequestBody, ratingRequestBody, reviewContentRequestBody, name, category, address, phone, x, y, body)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("Upload", "Image upload successful $response")
                            Toast.makeText(
                                requireContext(),
                                "Image upload successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToCalendar()

                        } else {
                            Log.e("UUUUpload2", "Image upload failed: ${response.message()}")
                            Toast.makeText(
                                requireContext(),
                                "Image upload failed: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("UUUUpload", "Image upload failed", t)
                        Toast.makeText(
                            requireContext(),
                            "Image upload failed: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } catch (e: IOException) {
                Log.e("Upload", "Failed to upload image", e)
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                    .show()
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } ?: run {
            Log.e("Upload", "Failed to create temp file")
            Toast.makeText(requireContext(), "Failed to create temp file", Toast.LENGTH_SHORT)
                .show()
        }
    }

}