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
import com.example.tab3.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storeadapter: MyAdapter
    private var city:String="전체"
    private var sortby:String="별점 순"

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
                fetchStoreItems()
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
                fetchStoreItems()
                Log.d("MainActivity", "Selected city: $selectedSort")
            }


            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
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
                    val storeResponseItem = StoreResponseItem(
                        place_name = storeItem.name,
                        distance = "",
                        place_url = "",
                        category_name = storeItem.starScore,
                        address_name = "",
                        road_address_name = "",
                        id = storeItem.restaurantId,
                        phone = storeItem.num,
                        category_group_code = "",
                        category_group_name = storeItem.reviewCount,
                        x = "0.0",
                        y = "0.0"
                    )
                    val intent = Intent(activity, StoreDetailActivity::class.java)
                    intent.putExtra(StoreDetailActivity.STORE_ITEM, storeResponseItem)
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
