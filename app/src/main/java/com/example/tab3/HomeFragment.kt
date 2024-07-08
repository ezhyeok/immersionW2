package com.example.tab3

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tab3.databinding.FragmentHomeBinding
import android.util.Log
import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.provider.ContactsContract
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: MyAdapter
    private var latestText:String? = null
    private var latestItem:MyItem = MyItem(profile = -1, name = "1", number = "1", isFavorite = false)
    private var latestSelect:Int?=null
    lateinit var getImage: ActivityResultLauncher<String>
    lateinit var addProfile: ActivityResultLauncher<String>
    lateinit var dialogView: View



    var inputimage: Uri? = null

    override fun onResume() {
        super.onResume()
        // 프래그먼트가 화면에 다시 나타날 때마다 권한 요청을 수행

        if (binding.switchSync.isChecked){
            requestPermissions()
        }


        binding.searchCont.setQuery("", false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(
            HomeViewModel::class.java)
        //homeViewModel.deleteAllContacts()
        //val switchSync = binding.switchSync
        getImage=
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    Log.d("ImageClickqqqqq2", "$it")
                    latestItem.image=it.toString()
                    Log.d("ImageClickqqqqq3", "$latestItem")
                    homeViewModel.updateContact(latestItem)
                }
            }
        addProfile=
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    if(hasPermissions()) {
                        Glide.with(requireContext())
                            .load(uri)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestItem.image=it.toString()
                    }
                }
            }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView
        val switchSync = binding.switchSync
        switchSync.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 동기화 ON
                requestPermissions()
            } else {
                // 동기화 OFF
                // 필요한 동작 추가
            }
        }

        adapter = MyAdapter(mutableListOf())
        binding.recyclerV.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerV.adapter = adapter
        binding.searchCont.setQuery("", false)
        // Observe contacts LiveData
        homeViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            val sortedContacts = contacts.sortedWith(compareByDescending<MyItem> { it.isFavorite }.thenBy { it.name })
            Log.d("Maaaaaaaaaa", "$sortedContacts")
            adapter.items.clear()
            adapter.items.addAll(sortedContacts)
            (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter("")
            adapter.notifyDataSetChanged()

            // Update visibility of RecyclerViews

        }

        //val decoration = MyAdapter.AddressAdapterDecoration()
        //binding.recyclerV.addItemDecoration(decoration)

        adapter.numberClick = object : MyAdapter.NumberClick {
            override fun onNumberClick(view: View, position: Int) {
                val item = adapter.filteredItems[position]
                val number: String = item.number
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                startActivity(intent)
            }
        }

        adapter.imageClick = object : MyAdapter.ImageClick {
            override fun onImageClick(view: View, position: Int) {
                /*
                val item = adapter.filteredItems[position]
                latestItem=item
                checkImagePermission()
                 */
                val item = adapter.filteredItems[position]
                dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_contact, null)
                val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
                val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
                val editImage = dialogView.findViewById<ImageView>(R.id.editImage)
                val resourceId = when (item.selcted) {
                    1 -> R.drawable.ic_person_01
                    2 -> R.drawable.ic_person_02
                    3 -> R.drawable.ic_person_03
                    4 -> R.drawable.ic_person_04
                    5 -> R.drawable.ic_person_05
                    6 -> R.drawable.ic_person_06
                    7 -> R.drawable.ic_person_07
                    8 -> R.drawable.ic_person_08
                    else -> R.drawable.ic_person // 기본 이미지
                }
                if(item.image!=null){
                    //profile.setImageResource(R.drawable.star_filled)
                    Glide.with(requireContext())
                        .load(Uri.parse(item.image))
                        .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                        .into(dialogView.findViewById<ImageView>(R.id.editImage))
                    //profile.setImageURI(Uri.parse(item.image))
                }else{
                    Glide.with(requireContext())
                        .load(resourceId)
                        .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                        .into(dialogView.findViewById<ImageView>(R.id.editImage))
                }

                editImage.setOnClickListener {
                    val profileView = LayoutInflater.from(context).inflate(R.layout.profile_select, null)
                    val profile1 = profileView.findViewById<ImageView>(R.id.profile1)
                    val profile2 = profileView.findViewById<ImageView>(R.id.profile2)
                    val profile3 = profileView.findViewById<ImageView>(R.id.profile3)
                    val profile4 = profileView.findViewById<ImageView>(R.id.profile4)
                    val profile5 = profileView.findViewById<ImageView>(R.id.profile5)
                    val profile6 = profileView.findViewById<ImageView>(R.id.profile6)
                    val profile7 = profileView.findViewById<ImageView>(R.id.profile7)
                    val profile8 = profileView.findViewById<ImageView>(R.id.profile8)
                    val profilePlus = profileView.findViewById<ImageView>(R.id.profilePlus)
                    val alertDialog = AlertDialog.Builder(context)
                        .setTitle("Select Profile")
                        .setView(profileView)
                        .setNegativeButton("Cancel", null)
                        .create()
                    profile1.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_01)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=1
                        alertDialog.dismiss()
                    }
                    profile2.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_02)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=2
                        alertDialog.dismiss()
                    }
                    profile3.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_03)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=3
                        alertDialog.dismiss()
                    }
                    profile4.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_04)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=4
                        alertDialog.dismiss()
                    }
                    profile5.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_05)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=5
                        alertDialog.dismiss()
                    }
                    profile6.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_06)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=6
                        alertDialog.dismiss()
                    }

                    profile7.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_07)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=7
                        alertDialog.dismiss()
                    }
                    profile8.setOnClickListener{
                        Glide.with(requireContext())
                            .load(R.drawable.ic_person_08)
                            .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                            .into(dialogView.findViewById<ImageView>(R.id.editImage))
                        latestSelect=8
                        alertDialog.dismiss()
                    }
                    profilePlus.setOnClickListener{
                        checkProfilePermission()
                        alertDialog.dismiss()
                    }
                    alertDialog.show()
                }

                AlertDialog.Builder(context)
                    .setTitle("Change Contact")
                    .setView(dialogView)
                    .setPositiveButton("Add") { _, _ ->

                        val name = editTextName.text.toString()
                        val number = editTextNumber.text.toString()

                        if (name.isNotBlank() && number.isNotBlank()) {
                            val newContact = MyItem(profile = item.profile, name = name, number = number, isFavorite = item.isFavorite, image = latestItem.image, selcted = latestSelect)
                            homeViewModel.updateContact(newContact)
                            Toast.makeText(requireContext(), "Contact changed", Toast.LENGTH_SHORT).show()
                            latestItem.image=null
                            latestSelect=null
                        } else {
                            Toast.makeText(requireContext(), "Please enter both name and phone number", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }



        adapter.favoriteClick = object : MyAdapter.FavoriteClick {
            override fun onFavoriteClick(view: View, position: Int) {
                val item = adapter.filteredItems[position]
                Log.d("Maaaaaaaaaa", "$position $item")
                item.isFavorite = !item.isFavorite
                homeViewModel.updateContact(item)
                //adapter.onCreateViewHolder()
                //adapter.notifyItemChanged(position)
                (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(latestText)
                adapter.notifyDataSetChanged()
            }
        }
        adapter.deleteClick = object : MyAdapter.DeleteClick {
            override fun onDeleteClick(view: View, position: Int) {
                val item = adapter.filteredItems[position]
                Log.d("Maaaaaaaaaa", "$position $item")

                homeViewModel.deleteContact(item.profile)
                //adapter.filteredItems.removeAt(position)
                (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(latestText)
                adapter.notifyDataSetChanged()
                //adapter.onCreateViewHolder()
                //adapter.notifyItemRemoved(position)
            }
        }

        binding.searchCont.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return onQueryTextChange(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // 검색어가 비어있을 때
                    binding.recyclerV.visibility = View.VISIBLE
                    //(binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(newText)
                    (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(newText)
                    latestText=newText
                } else {
                    // 검색어가 입력됐을 때
                    binding.recyclerV.visibility = View.VISIBLE
                    (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(newText)
                    latestText=newText
                }
                return true
            }
        })

        binding.searchCont.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // 포커스가 없어졌을 때
                if (binding.searchCont.query.isNullOrEmpty()) {
                    binding.recyclerV.visibility = View.VISIBLE
                    (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(latestText)
                }
            } else {
                // 포커스가 있을 때
                binding.recyclerV.visibility = View.VISIBLE
                (binding.recyclerV.adapter as? MyAdapter)?.filter?.filter(latestText)
            }
        }

        binding.buttonAddContact.setOnClickListener {
            showAddContactDialog()
        }

        return root
    }
    private fun showAddContactDialog() {
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_contact, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)
        var pUri:Uri?=null
        val editImage = dialogView.findViewById<ImageView>(R.id.editImage)

        editImage.setOnClickListener {
            val profileView = LayoutInflater.from(context).inflate(R.layout.profile_select, null)
            val profile1 = profileView.findViewById<ImageView>(R.id.profile1)
            val profile2 = profileView.findViewById<ImageView>(R.id.profile2)
            val profile3 = profileView.findViewById<ImageView>(R.id.profile3)
            val profile4 = profileView.findViewById<ImageView>(R.id.profile4)
            val profile5 = profileView.findViewById<ImageView>(R.id.profile5)
            val profile6 = profileView.findViewById<ImageView>(R.id.profile6)
            val profile7 = profileView.findViewById<ImageView>(R.id.profile7)
            val profile8 = profileView.findViewById<ImageView>(R.id.profile8)
            val profilePlus = profileView.findViewById<ImageView>(R.id.profilePlus)
            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Select Profile")
                .setView(profileView)
                .setNegativeButton("Cancel", null)
                .create()
            profile1.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_01)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=1
                alertDialog.dismiss()
            }
            profile2.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_02)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=2
                alertDialog.dismiss()
            }
            profile3.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_03)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=3
                alertDialog.dismiss()
            }
            profile4.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_04)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=4
                alertDialog.dismiss()
            }
            profile5.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_05)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=5
                alertDialog.dismiss()
            }
            profile6.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_06)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=6
                alertDialog.dismiss()
            }
            profile7.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_07)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=7
                alertDialog.dismiss()
            }
            profile8.setOnClickListener{
                Glide.with(requireContext())
                    .load(R.drawable.ic_person_08)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(dialogView.findViewById<ImageView>(R.id.editImage))
                latestSelect=8
                alertDialog.dismiss()
            }
            profilePlus.setOnClickListener{
                checkProfilePermission()
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        AlertDialog.Builder(context)
            .setTitle("Add Contact")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->

                val name = editTextName.text.toString()
                val number = editTextNumber.text.toString()

                if (name.isNotBlank() && number.isNotBlank()) {
                    val newContact = MyItem(profile = homeViewModel.readMaxId(), name = name, number = number, isFavorite = false, image = latestItem.image, selcted = latestSelect)
                    homeViewModel.addContact(newContact)
                    Toast.makeText(requireContext(), "Contact added", Toast.LENGTH_SHORT).show()
                    homeViewModel.addMaxId()
                    latestItem.image=null
                    latestSelect=null
                } else {
                    Toast.makeText(requireContext(), "Please enter both name and phone number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
            if (permissionsToRequest.isEmpty()) {
                Toast.makeText(requireContext(), "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                showConsentDialog()
            } else {
                Toast.makeText(requireContext(), "일부 권한이 거부되었습니다. 권한을 허용해야 앱이 정상 동작합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }

            if (deniedPermissions.isEmpty()) {
                Toast.makeText(requireContext(), "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                showConsentDialog()
            } else {
                Toast.makeText(requireContext(), "일부 권한이 거부되었습니다. 권한을 허용해야 앱이 정상 동작합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadContacts() {
        val contactsList = mutableListOf<MyItem>()

        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val number = cursor.getString(numberIndex)
                contactsList.add(MyItem(profile = homeViewModel.readMaxId(), name = name, number = number, isFavorite = false))
                homeViewModel.addMaxId()
            }
        }

        addContacts(contactsList)
        //showConsentDialog(contactsList)
    }

    private fun showConsentDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("연락처 추가")
            .setMessage("연락처를 추가하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                loadContacts()
            }
            .setNegativeButton("아니요", null)
            .show()
    }

    private fun addContacts(contactsList: List<MyItem>) {
        // 연락처 추가
        for (contact in contactsList) {
            homeViewModel.addContact(contact)
        }
        // Update adapter
        adapter.items.clear()
        adapter.items.addAll(homeViewModel.contacts.value ?: emptyList())
        adapter.notifyDataSetChanged()
    }

    private fun checkImagePermission() {
        Log.d("DashboardFragment", "Image Clicked")
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
    private fun checkProfilePermission() {
        Log.d("DashboardFragment", "Image Clicked")
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadProfile()
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
        Log.d("DashboardFragment", "loadImage called")
        return getImage.launch("image/*")

    }
    private fun loadProfile() {
        Log.d("DashboardFragment", "loadImage called")
        return addProfile.launch("image/*")

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
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_READ_MEDIA_IMAGES = 1001

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
