package com.example.tab3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import ted.gun0912.clustering.naver.TedNaverClustering
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MapViewFragment : Fragment(), OnMapReadyCallback{
    companion object {
        val STORE_ITEM = "STORE_ITEM"
        val ARG_PARAM = "STORE_LIST"
        val storeType = MapTypeConstant
        var currentPosition = LatLng(37.576227432762906, 126.89254733575699)
        var cameraPosition = CameraPosition(currentPosition, 15.0)
    }

    private var storeList: StoreList? = null
    private val circleOverlay = CircleOverlay()
    lateinit var naverMap: NaverMap
    private lateinit var storeDetailLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fragment_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fragment_map, it).commit()
            }
        mapFragment.getMapAsync(this)
        storeDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.getParcelableExtra<StoreResponseItem>(STORE_ITEM)?.let {
                    val resultIntent = Intent().apply {
                        putExtra(STORE_ITEM, it)
                    }
                    activity?.setResult(AppCompatActivity.RESULT_OK, resultIntent)
                    activity?.finish() // MapActivity를 종료하고 NewDiary로 돌아갑니다.
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            storeList = it.getParcelable(ARG_PARAM)
        }
        Log.d("StttoireList", "$storeList")

        return inflater.inflate(R.layout.activity_map, container, false)
    }

    @SuppressLint("ResourceAsColor")
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        val cameraPosition = CameraPosition(currentPosition, 15.0)
        naverMap.cameraPosition = cameraPosition

        circleOverlay.center = currentPosition
        circleOverlay.color = R.color.white_indigo
        circleOverlay.radius = 300.0
        circleOverlay.map = naverMap

        val marker = Marker()

        marker.icon = OverlayImage.fromResource(R.drawable.ic_current)
        marker.position = currentPosition
        marker.map = naverMap

        naverMap.uiSettings.isCompassEnabled = false
        naverMap.uiSettings.isZoomControlEnabled = false

        storeList?.storeList?.let { it ->
            var storeMarker = Marker()
            TedNaverClustering.with<StoreResponseItem>(requireActivity(), naverMap)
                .customMarker { clusterItem ->
                    Marker(LatLng(clusterItem.getTedLatLng().latitude, clusterItem.getTedLatLng().longitude)).apply {
                        when (clusterItem.category_group_code) {
                            storeType.BAKERY -> icon = OverlayImage.fromResource(R.drawable.ic_bakery)
                            storeType.CHINESE_FOOD -> icon = OverlayImage.fromResource(R.drawable.ic_chinese_food)
                            storeType.FAST_FOOD -> icon = OverlayImage.fromResource(R.drawable.ic_fast_food)
                            storeType.JAPANESE_FOOD -> icon = OverlayImage.fromResource(R.drawable.ic_japanese_food)
                            storeType.KOREAN_FOOD -> icon = OverlayImage.fromResource(R.drawable.ic_korean_food)
                            storeType.WESTERN_FOOD -> icon = OverlayImage.fromResource(R.drawable.ic_western_food)
                            else -> icon = OverlayImage.fromResource(R.drawable.ic_basic)
                        }
                        storeMarker = this
                    }
                }
                .markerClickListener {
                    val intent = Intent(requireContext(), StoreDetailActivity::class.java)
                    intent.putExtra(STORE_ITEM, it)
                    storeDetailLauncher.launch(intent)
                }
                .clusterText { it.toString() }
                .clusterBackground { ContextCompat.getColor(requireContext(), R.color.indigo) }
                .items(it)
                .make()
        }
    }

    fun setLocation(location: LatLng) {
        currentPosition = location
        updateMapPosition()
    }

    private fun updateMapPosition() {
        currentPosition?.let { location ->
            if (::naverMap.isInitialized) {
                val cameraPosition = CameraPosition(location, 15.0)
                naverMap.cameraPosition = cameraPosition

                val marker = Marker()
                marker.icon = OverlayImage.fromResource(R.drawable.ic_current)
                marker.position = location
                marker.map = naverMap
            }
        }
    }
    fun setCurrentPosition() {
        cameraPosition = CameraPosition(currentPosition, 15.0)
        naverMap.cameraPosition = cameraPosition
    }

    fun setSurroundMeter(meter: Double) {
        if (naverMap != null) {
            circleOverlay.radius = meter
            circleOverlay.map = naverMap
        }
    }

    fun setZoom(meter: Double) {
        if (naverMap != null) {
            when (meter) {
                300.0 -> cameraPosition = CameraPosition(currentPosition, 15.0)
                400.0 -> cameraPosition = CameraPosition(currentPosition, 14.5)
                500.0 -> cameraPosition = CameraPosition(currentPosition, 14.0)
                600.0 -> cameraPosition = CameraPosition(currentPosition, 13.5)
            }
            naverMap.cameraPosition = cameraPosition
        }
    }
}