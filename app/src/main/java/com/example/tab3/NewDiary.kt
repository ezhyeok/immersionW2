package com.example.tab3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import com.example.tab3.MyAdapter.NumberClick
import com.example.tab3.databinding.FragmentDiaryAddBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity


class NewDiary: Fragment() {
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
    companion object {
        fun newInstance(year: Int, month: Int, day: Int): NewDiary {
            val PICK_IMAGE_REQUEST = 1
            val fragment = NewDiary()
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


        binding.diarySave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                createWorkoutAndNavigateBack(inputimage)
                navigateToCalendar()
                CheckDataBase()
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
            val newDiary = Diary(
                year = addYear,
                month = addMonth,
                date = addDay,
                diaryImg = img,
                diaryContent = diarytext,
                place_name=place_name,
                category_name=category_name,
                address_name=address_name,
                id=id,
                phone=phone,
                x=x,
                y=y
            )
            place_name=null
            category_name=null
            address_name=null
            id=null
            phone=null
            x=null
            y=null
            db?.diaryDao()?.insertOrUpdate(newDiary)
            DiaryData.add(newDiary)
            Log.d("newLog", newDiary.toString())
        }else{

        }

        //update decorators when a new diary entry is added
        CoroutineScope(Dispatchers.Main).launch {
            (requireParentFragment() as DiaryFragment).addDecorators()
            parentFragmentManager.setFragmentResult("NewDiaryClosed", Bundle())
        }
    }

    private fun navigateToCalendar() {
        parentFragmentManager.popBackStack()
    }

    private fun CheckDataBase(){
        val savedDiary = db?.diaryDao()?.getAll()?: emptyList()
        Log.d("savedDiary", savedDiary.toString())
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
        }
    }


}