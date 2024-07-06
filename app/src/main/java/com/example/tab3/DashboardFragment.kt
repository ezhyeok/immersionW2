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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.example.tab3.databinding.FragmentDashboardBinding
import android.app.Activity
import com.example.tab3.ui.dashboard.DashboardViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import android.Manifest
import android.Manifest.permission.CAMERA
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.content.ContentValues

class DashboardFragment : Fragment(){

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var _position:Int=0
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var photoURI: Uri
    private lateinit var imageModel: DashboardViewModel
    private lateinit var imageModel2: DashboardViewModel
    private lateinit var imageAdapter1: ImageAdapter
    private lateinit var imageAdapter2: ImageAdapter

    private val imageLoadLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
        updateImages(uriList)
    }

    private fun getImageUriFromBitmap(context: Context?, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes)
        val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver,bitmap,"File",null)
        return Uri.parse(path.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        imageModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            DashboardViewModel::class.java)
        imageModel2 = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.loadImageButton.setOnClickListener {
            Log.d("DashboardFragment", "Load Image Button clicked")
            checkPermission()
        }
        binding.cameraButton.setOnClickListener(){
            Log.d("CameraPermission", "???")
            checkCameraPermission()
        }

        binding.editButton.setOnClickListener(){
            val x=imageModel.images.value

            if (x!=null) {
                if(hasPermissions()) {
                    binding.dialogPhotoView.apply {
                        Glide.with(requireContext())
                            .load(x[_position])
                            .apply(RequestOptions().centerCrop())
                            .into(this)
                    }

                    toggleVisibility(binding.originLayout)
                    toggleVisibility(binding.imageEditLayout)

                }
                Log.d("imageEdit", "detected ${x[_position]}")
            }
        }

        binding.deleteButton.setOnClickListener{
            Log.d("currentList", "${imageAdapter1.currentList}")
            imageModel.deleteImage(_position)
            imageModel.loadImages()
            val images2 = imageModel.images.value?.map { ImageItems.Image(it) } ?: emptyList()
            val emptyMutableList: MutableList<ImageItems> = mutableListOf()
            Log.d("currentList2", "${images2::class}")
            Log.d("currentList3", "${emptyMutableList::class}")

            if(images2.isEmpty()){
                Log.d("currentList4", "detected")
                imageAdapter1.submitList(emptyMutableList)
                imageAdapter2.submitList(emptyMutableList)
            }else {
                imageAdapter1.submitList(images2)
                imageAdapter2.submitList(images2)
            }
            toggleVisibility(binding.loadImageButton)
            toggleVisibility(binding.cameraButton)
            toggleVisibility(binding.deleteButton)
            toggleVisibility(binding.editButton)
        }

        binding.closeButton.setOnClickListener {
            toggleEditMode()
        }

        initRecyclerViews()

        return root
    }

    private fun initRecyclerViews() {
        // 첫 번째 RecyclerView 초기화
        imageAdapter1 = ImageAdapter(object : ImageAdapter.ItemClickListener {
            override fun onLoadMoreClick() {
                checkPermission()
            }
        }, object : ImageAdapter.ImageClickListener {
            override fun onImageClick(position: Int) {
                toggleVisibility(binding.loadImageButton)
                toggleVisibility(binding.cameraButton)
                toggleVisibility(binding.deleteButton)
                toggleVisibility(binding.editButton)
                _position = position
            }
        })
        Log.d("currentList1", "${imageAdapter1.currentList::class}")

        val images1 = imageModel.images.value?.map { ImageItems.Image(it) } ?: emptyList()
        imageAdapter1.submitList(images1)

        binding.recyclerView1.apply {
            adapter = imageAdapter1
            layoutManager = GridLayoutManager(context, 3)
        }

        // 두 번째 RecyclerView 초기화
        imageAdapter2 = ImageAdapter(object : ImageAdapter.ItemClickListener {
            override fun onLoadMoreClick() {
                // 두 번째 RecyclerView에는 Load More 버튼이 없음
            }
        }, object : ImageAdapter.ImageClickListener {
            override fun onImageClick(position: Int) {
                // 두 번째 RecyclerView에서의 클릭 동작
            }
        })
        Log.d("currentList2", "${imageAdapter2.currentList::class}")

        // 유효한 이미지 URL 목록으로 수정
        val hardcodedImageUrls = mutableListOf(
            "https://via.placeholder.com/150",
            "https://via.placeholder.com/200",
            "https://via.placeholder.com/250"
        )
        val hardcodedImages = hardcodedImageUrls.map { ImageItems.Image(Uri.parse(it)) }
        imageAdapter2.submitList(hardcodedImages)

        binding.recyclerView2.apply {
            adapter = imageAdapter2
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun toggleVisibility(button: View) {
        if (button.visibility == View.VISIBLE) {
            button.visibility = View.GONE
        } else {
            button.visibility = View.VISIBLE
        }
    }

    private fun toggleEditMode() {
        toggleVisibility(binding.originLayout)
        toggleVisibility(binding.imageEditLayout)
        toggleVisibility(binding.loadImageButton)
        toggleVisibility(binding.cameraButton)
        toggleVisibility(binding.deleteButton)
        toggleVisibility(binding.editButton)
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadImage()
            }

            shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) -> {
                showPermissionInfoDialog()
            }

            else -> {
                requestReadMediaImages()
            }
        }
    }

    private fun loadImage() {
        imageLoadLauncher.launch("image/*")
        Log.d("DashboardFragment", "loadImage called")
    }

    private fun showPermissionInfoDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("이미지를 가져오기 위해 외부 저장소 읽기 권한이 필요합니다.")
            setNegativeButton("Cancel", null)
            setPositiveButton("OK") { _, _ ->
                requestReadMediaImages()
            }
        }.show()
    }

    private fun requestReadMediaImages() {
        requestPermissions(
            arrayOf(READ_EXTERNAL_STORAGE),
            REQUEST_READ_MEDIA_IMAGES
        )
    }

    private fun updateImages(uriList: List<Uri>) {
        imageModel.addImages(uriList)
        imageModel.loadImages()
        val images2 = imageModel.images.value?.map { ImageItems.Image(it) } ?: emptyList()
        Log.d("updateImagesXX", "${images2}")
        imageAdapter1.submitList(images2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_READ_MEDIA_IMAGES -> {
                val resultCode = grantResults.firstOrNull() ?: PackageManager.PERMISSION_DENIED
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    loadImage()
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun toFrameActivity() {
        val images = imageAdapter1.currentList.filterIsInstance<ImageItems.Image>().map { it.uri.toString() }.toTypedArray()
        val intent = Intent(requireContext(), FrameActivity::class.java)
            .putExtra("images", images)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
    private fun hasCameraPermission(permissions: Array<out String>, type:Int): Boolean {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (permission in permissions){
                if(ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), permissions, type)
                    return false
                }
            }
        }
        return true
    }
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showCameraPermissionInfoDialog()
            }
            else -> {
                requestCameraPermission()
            }
        }
    }
    private fun showCameraPermissionInfoDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setMessage("카메라 기능을 사용하기 위해 카메라 권한이 필요합니다.")
            setNegativeButton("취소", null)
            setPositiveButton("확인") { _, _ ->
                requestCameraPermission()
            }
        }.show()
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }

    private fun dispatchTakePictureIntent() {
        if(hasCameraPermission(arrayOf(Manifest.permission.CAMERA), 98) && hasCameraPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 99)){
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, 98)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
    // 사진 저장
    fun saveFile(fileName: String, mimeType: String, bitmap: Bitmap): Uri? {
        val contentResolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }
        }

        return uri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                98 -> {
                    if (data?.extras?.get("data") != null) {
                        val img = data.extras?.get("data") as Bitmap
                        val uri = saveFile(RandomFileName(), "image/jpeg", img)
                        if (uri != null)
                            updateImages(listOf(uri))
                    }
                }
                99 -> {
                    val uri = data?.data
                    if (uri != null)
                        updateImages(listOf(uri))
                }
            }
        }
    }

    fun RandomFileName(): String {
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        return fileName
    }

    // 갤러리 취득
    fun GetAlbum() {
        if (hasCameraPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 99)) {
            val itt = Intent(Intent.ACTION_PICK)
            itt.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(itt, 99)
        }
    }

    companion object {
        const val REQUEST_READ_MEDIA_IMAGES = 100
    }
}
