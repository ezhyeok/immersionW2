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
import android.view.WindowInsetsAnimation

import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var storeadapter: MyAdapter

    private var _position:Int=0

    var inputimage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView

        initRecyclerView()

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initRecyclerView(){
        storeadapter=MyAdapter(
            object:MyAdapter.ItemClickListener{
                override fun onItemClick(position:Int){
                    _position=position
                }
            }
        )
        binding.recyclerV.apply{
            adapter=storeadapter
            layoutManager=LinearLayoutManager(context)
        }
        fetchStoreItems()
    }

    private fun fetchStoreItems(){
        val apiService=RetrofitClient.apiService
        val call=apiService.getStore()

        call.enqueue(object: Callback<List<StoreItem>> {
            override fun onResponse(call: Call<List<StoreItem>>, response: Response<List<StoreItem>>){
                if(response.isSuccessful){
                    val storeItems=response.body()
                    Log.d("FetchStore","Store")
                    if(storeItems!=null){
                        storeadapter.submitList(storeItems)
                    }
                }else{
                    Log.e("NO,,","ee")
                }
            }
            override fun onFailure(call:Call<List<StoreItem>>,t:Throwable){
                Log.e("fetch failed","e")
            }
        })
    }

}
