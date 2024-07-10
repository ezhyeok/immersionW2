package com.example.tab3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.naver.maps.map.NaverMap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tab3.MapViewFragment2.Companion.currentPosition
import com.example.tab3.databinding.ActivityStoreRecyclerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreDetailActivity2 : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityStoreRecyclerBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationSource: FusedLocationSource? = null
    lateinit var naverMap: NaverMap
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val TAG = "StoreDetailActivity"

    private lateinit var storeItem: StoreItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Checked")
        setContentView(R.layout.activity_store_recycler)

        // Fragment 초기화
        if (savedInstanceState == null) {
            val mapFragment = MapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.storeMap, mapFragment)
                .commit()
            mapFragment.getMapAsync(this)
        }

        // DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_store_recycler)

        // 전달받은 Store Item 정보를 얻음
        storeItem = intent.getParcelableExtra<StoreItem>(STORE_ITEM)!!
        Log.d("storeItemInfo", "$storeItem")
        var items: List<ReviewRecycle> = listOf()
        binding.recyclerView.layoutManager = LinearLayoutManager(this@StoreDetailActivity2)
        val storeAdapter = StoreAdapter( mutableListOf())
        binding.recyclerView.adapter = storeAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val apiService = RetrofitClient.apiService
            val call1 = apiService.getAllReview(storeItem.restaurantId)
            call1.enqueue(object : Callback<List<ReviewRecycle>> {
                override fun onResponse(call: Call<List<ReviewRecycle>>, response: Response<List<ReviewRecycle>>) {
                    if (response.isSuccessful) {

                        storeAdapter.items.clear()

                        storeAdapter.items.addAll(response.body()?: listOf())
                        storeAdapter.notifyDataSetChanged()
                        // 성공적인 응답 처리
                        /*
                        추가 코드 필요한부분
                        followers[position]을 제외한 부분을 다시 adapter에 표시하고 싶음
                         */
                    } else {
                        // 실패한 응답 처리
                        Log.e("MainActivity", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<ReviewRecycle>>, t: Throwable) {
                    // 네트워크 실패 처리
                    Log.e("MainActivity", "Failure: ${t.message}")
                }
            })

        }
        Log.d("storeItemInfoitems", "${items}")

        binding.storeName.text = storeItem.name
        binding.storeType.text = storeItem.category_name

        // 장소 위치 정보를 기반으로 GoogleMap Intent
        binding.storeAddress.text = storeItem.address_name
        binding.storeLocationCard.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${storeItem.address_name}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        // 전화번호 정보를 기반으로 Call Intent
        binding.storePhoneNumber.text = storeItem.num
        binding.phoneNumberCard.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_VIEW)
            callIntent.data = Uri.parse("tel:" + storeItem.num)
            startActivity(callIntent)
        }

        binding.addressSelect.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(STORE_ITEM, storeItem)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (!hasPermission()) {
            Log.d(TAG, "Requesting permissions")
            ActivityCompat.requestPermissions(this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            Log.d(TAG, "Permissions already granted")
            initMapView()
        }
    }

    private fun hasPermission(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initMapView() {
        try {
            Log.d(TAG, "Initializing map view")
            val fm = supportFragmentManager
            val mapFragment = fm.findFragmentById(R.id.storeMap) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.storeMap, it).commit()
                }

            mapFragment.getMapAsync(this)
            locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentPosition = LatLng(it.latitude, it.longitude)
                    if (::naverMap.isInitialized) {
                        updateMapView()
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing map view", e)
            Toast.makeText(this, "지도를 초기화하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = currentPosition
        if (currentPosition != null) {
            updateMapView()
        }
    }

    private fun updateMapView() {
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = currentPosition

        val storePosition = LatLng(storeItem.y!!.toDouble(), storeItem.x!!.toDouble())
        Log.d(TAG, "Store Position onMapReady: $storePosition") // Debug log 추가
        val storeMarker = Marker().apply {
            icon = OverlayImage.fromResource(R.drawable.ic_basic)
            position = storePosition
            map = naverMap
        }

        val bounds = LatLngBounds.Builder()
            .include(currentPosition)
            .include(storePosition)
            .build()

        val cameraUpdate = CameraUpdate.fitBounds(bounds, 100)
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        const val STORE_ITEM = "STORE_ITEM"
        const val LOCATION_PERMISSION_REQUEST_CODE = 5000
    }
}
