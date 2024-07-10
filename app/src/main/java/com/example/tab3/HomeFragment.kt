package com.example.tab3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tab3.NewDiary.Companion.REQUEST_CODE
import com.example.tab3.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storeadapter: MyAdapter
    private var latestStores= emptyList<StoreItem>()
    private var city:String="전체"
    private var sortby:String="별점 순"

    private var showFollowersOnly: Boolean = false

    private var _position: Int = 0

    var inputimage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Spinner and Button using binding
        binding.citySpinner.apply {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.cities_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
        binding.sortSpinner.apply {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position).toString()
                city=selectedCity
                if(showFollowersOnly==true){
                    fetchStoreItemsByF()
                }
                else{
                    fetchStoreItems()
                }
                Log.d("MainActivity", "Selected city: $selectedCity")
            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSort = parent.getItemAtPosition(position).toString()
                sortby=selectedSort
                if(showFollowersOnly==true){
                    fetchStoreItemsByF()
                }
                else{
                    fetchStoreItems()
                }
                Log.d("MainActivity", "Selected city: $selectedSort")
            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        binding.switchSync.setOnCheckedChangeListener { _, isChecked ->
            showFollowersOnly = isChecked
            if(showFollowersOnly){
                fetchStoreItemsByF()
            }else {
                fetchStoreItems()
            }

        }
        binding.mapButton.setOnClickListener{
            val intent = MapShowActivity.newInstance(requireContext(), latestStores)
            startActivityForResult(intent, REQUEST_CODE)
        }
        initRecyclerView()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        storeadapter = MyAdapter(
            object : MyAdapter.ItemClickListener {
                override fun onItemClick(position: Int) {
                    _position = position
                    val storeItem = storeadapter.currentList[position]

                    val intent = Intent(activity, StoreDetailActivity2::class.java)
                    intent.putExtra(StoreDetailActivity2.STORE_ITEM, storeItem)
                    startActivity(intent)
                }
            }
        )
        binding.recyclerV.apply {
            adapter = storeadapter
            layoutManager = LinearLayoutManager(context)
        }
        fetchStoreItems()
    }

    private fun fetchStoreItems() {
        val apiService = RetrofitClient.apiService
        val call = apiService.getStore(city,sortby)

        call.enqueue(object : Callback<List<StoreItem>> {
            override fun onResponse(
                call: Call<List<StoreItem>>,
                response: Response<List<StoreItem>>
            ) {
                if (response.isSuccessful) {
                    val storeItems = response.body()
                    Log.d("FetchStore", "Store")
                    if (storeItems != null) {
                        storeadapter.submitList(storeItems)
                        latestStores=storeItems
                    }
                } else {
                    Log.e("NO,,", "ee")
                }
            }
            override fun onFailure(call: Call<List<StoreItem>>, t: Throwable) {
                Log.e("fetch failed", "e")
            }
        })
    }

    private fun fetchStoreItemsByF() {
        val apiService = RetrofitClient.apiService
        val call = apiService.getStorefollowers(city,sortby, ClientData.uniqueId.toString())

        call.enqueue(object : Callback<List<StoreItem>> {
            override fun onResponse(
                call: Call<List<StoreItem>>,
                response: Response<List<StoreItem>>
            ) {
                if (response.isSuccessful) {
                    val storeItems = response.body()
                    Log.d("FetchStore11", "Store")
                    if (storeItems != null) {
                        storeadapter.submitList(storeItems)
                        latestStores=storeItems
                    }
                } else {
                    Log.e("NO,,", "ee")
                }
            }
            override fun onFailure(call: Call<List<StoreItem>>, t: Throwable) {
                Log.e("fetch failed", "e")
            }
        })
    }
}

