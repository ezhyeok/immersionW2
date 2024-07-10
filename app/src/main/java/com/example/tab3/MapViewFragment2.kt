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
import com.naver.maps.geometry.LatLngBounds
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
import com.naver.maps.map.CameraUpdate
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MapViewFragment2 : Fragment(), OnMapReadyCallback {
    companion object {
        const val STORE_ITEM = "STORE_ITEM"
        const val ARG_PARAM = "STORE_LIST"
        val storeType = MapTypeConstant
        var currentPosition = LatLng(37.576227432762906, 126.89254733575699)
        var cameraPosition = CameraPosition(currentPosition, 15.0)
    }

    private var storeList: StoreList2? = null
    private val circleOverlay = CircleOverlay()
    lateinit var naverMap: NaverMap
    private lateinit var storeDetailLauncher: ActivityResultLauncher<Intent>
    private val boundsBuilder = LatLngBounds.Builder() // 경계 범위를 설정하는 빌더 객체 생성
    private var hasMarkers = false

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

        naverMap.locationOverlay.isVisible = true
        naverMap.locationOverlay.position = currentPosition

        naverMap.uiSettings.isCompassEnabled = false
        naverMap.uiSettings.isZoomControlEnabled = false
        Log.d("SSStoreList mapview", "$storeList")
        hasMarkers = false // 마커가 추가되었는지 여부를 확인하는 변수

        storeList?.storeList?.let { it ->
            var storeMarker = Marker()
            it.forEach { storeItem ->
                if(storeItem.getTedLatLng().latitude!=0.0 && storeItem.getTedLatLng().longitude!=0.0){
                    val position = LatLng(storeItem.getTedLatLng().latitude, storeItem.getTedLatLng().longitude)
                    Marker(position).apply {
                        Log.d("Mappp", "Marker included: $position")
                        if(storeItem.starScore.toDouble()<0.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic)
                        }else if(storeItem.starScore.toDouble()<1.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_01)
                        }else if(storeItem.starScore.toDouble()<2.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_02)
                        }else if(storeItem.starScore.toDouble()<3.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_03)
                        }else if(storeItem.starScore.toDouble()<4.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_04)
                        }else{
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_05)
                        }

                        map = naverMap
                        boundsBuilder.include(position) // 각 마커의 위치를 경계 범위에 추가
                        hasMarkers = true // 마커가 추가되었음을 표시
                    }
                }
            }

            if (hasMarkers) {
                val bounds = boundsBuilder.build()
                val cameraUpdate = CameraUpdate.fitBounds(bounds, 100)
                naverMap.moveCamera(cameraUpdate)
            }

            TedNaverClustering.with<StoreItem>(requireActivity(), naverMap)
                .customMarker { clusterItem ->
                    Marker(LatLng(clusterItem.getTedLatLng().latitude, clusterItem.getTedLatLng().longitude)).apply {

                        if(clusterItem.starScore.toDouble()<0.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic)
                        }else if(clusterItem.starScore.toDouble()<1.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_01)
                        }else if(clusterItem.starScore.toDouble()<2.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_02)
                        }else if(clusterItem.starScore.toDouble()<3.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_03)
                        }else if(clusterItem.starScore.toDouble()<4.5){
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_04)
                        }else{
                            icon = OverlayImage.fromResource(R.drawable.ic_basic_05)
                        }

                    }
                }
                .markerClickListener {
                    val intent = Intent(requireContext(), StoreDetailActivity2::class.java)
                    intent.putExtra(STORE_ITEM, it)
                    storeDetailLauncher.launch(intent)
                }
                .clusterText { it.toString() }
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

                if (hasMarkers) {
                    val bounds = boundsBuilder.build()
                    val cameraUpdate = CameraUpdate.fitBounds(bounds, 100)
                    naverMap.moveCamera(cameraUpdate)
                }
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
