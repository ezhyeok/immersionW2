package com.example.tab3

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import com.naver.maps.map.overlay.OverlayImage
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.LocationServices
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.jakewharton.rxbinding4.widget.textChanges
import com.example.tab3.databinding.ActivityMapBinding
import com.example.tab3.viewmodel.MapViewModel
import com.example.tab3.MapViewFragment.Companion.STORE_ITEM
import com.example.tab3.MapViewFragment.Companion.currentPosition
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import java.util.concurrent.TimeUnit
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
class MapActivity : BaseActivity<ActivityMapBinding, MapViewModel>(R.layout.activity_map), OnMeterSetListener , OnMapReadyCallback{

    companion object {
        const val TAG = "MapActivity"
        const val ARG_PARAM = "STORE_LIST"
        const val LOCATION_PERMISSION_REQUEST_CODE = 5000
        const val STORE_DETAIL_REQUEST_CODE = 200
    }
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override val viewModel: MapViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mapViewFragment = MapViewFragment()
    private var compositeDisposable = CompositeDisposable()
    private lateinit var recyclerViewAdapter: StoreListAdapter
    private var locationSource: FusedLocationSource?=null
    private lateinit var storeDetailLauncher: ActivityResultLauncher<Intent>


    @SuppressLint("SetTextI18n")
    override fun onMeterSetListener(meter: Double) {
        binding.btnSurroundMeter.text = meter.toString()+"M"
        mapViewFragment.setSurroundMeter(meter)
        mapViewFragment.setZoom(meter)
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

    private fun initMapView() {
        try {
            Log.d(TAG, "Initializing map view")
            val fm = supportFragmentManager
            val mapFragment = fm.findFragmentById(R.id.fragment_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.fragment_map, it).commit()
                }

            mapFragment.getMapAsync(this)
            locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.getSurroundStoreList(
                        it.longitude,
                        it.latitude
                    )
                    mapViewFragment.setLocation(LatLng(it.latitude, it.longitude))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing map view", e)
            Toast.makeText(this, "지도를 초기화하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermission(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        Log.d(TAG, "Map is ready")
        val cameraPosition = CameraPosition(
            LatLng(35.15690579523921, 129.05957113473747),
            14.0
        )
        naverMap.cameraPosition = cameraPosition
        mapViewFragment.naverMap=naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        val marker = Marker()
        marker.icon = OverlayImage.fromResource(R.drawable.ic_current)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d("markerErr", "1")
                marker.position = LatLng(location.latitude, location.longitude)
                marker.map = naverMap
            } else {
                Log.d("markerErr", "2")
                marker.position = LatLng(0.0, 0.0)
                marker.map = naverMap
            }

            viewModel.surroundStoreListLiveData.observe(this) { response ->
                val storeItems: ArrayList<StoreResponseItem> = ArrayList(response.documents)
                val storeList = StoreList(storeItems)
                val bundle = Bundle()
                bundle.putParcelable(ARG_PARAM, storeList)
                mapViewFragment.arguments = bundle
                supportFragmentManager.beginTransaction().add(R.id.fragment_map, mapViewFragment).commit()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (hasPermission()) {
                Log.d(TAG, "Permissions granted")
                initMapView()
            } else {
                Log.d(TAG, "Permissions denied")
                showPermissionExplanationDialog()
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("위치 권한 필요")
            .setMessage("이 앱은 지도 기능을 위해 위치 권한이 필요합니다. 앱 설정에서 권한을 허용해주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create()
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

/*
        viewModel.surroundStoreListLiveData.observe(this) { storeResponse ->
            val storeItems: ArrayList<StoreResponseItem> = ArrayList(storeResponse.documents)
            val storeList = StoreList(storeItems)
            bundle.putParcelable(ARG_PARAM, storeList)
            mapViewFragment.arguments = bundle
            supportFragmentManager.beginTransaction().add(R.id.fragment_map, mapViewFragment).commit()
        }


 */
        storeDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.getParcelableExtra<StoreResponseItem>(STORE_ITEM)?.let {
                    val resultIntent = Intent().apply {
                        putExtra(STORE_ITEM, it)
                    }
                    setResult(AppCompatActivity.RESULT_OK, resultIntent)
                    finish() // MapActivity를 종료하고 NewDiary로 돌아갑니다.
                }
            }
        }





        binding.btnCurrentPosition.setOnClickListener {
            mapViewFragment.setCurrentPosition()
        }

        binding.cardSurroundMeter.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog()
            bottomSheetDialog.show(supportFragmentManager, "bottomSheetDialog")
        }


        /**
         * RecyclerView Adapter 적용
         */
        recyclerViewAdapter = StoreListAdapter {
            Log.d("ItemClick", "ItemClicked")
            val intent = Intent(this, StoreDetailActivity::class.java)
            intent.putExtra(STORE_ITEM, it)
            storeDetailLauncher.launch(intent)
        }

        binding.storeListRecyclerView.apply {
            this.adapter = recyclerViewAdapter
            this.layoutManager = LinearLayoutManager(context)
            this.setHasFixedSize(true)
        }

        binding.searchView.apply {
            this.hint = "검색어를 입력해주세요"

            // EditText 에 포커스가 갔을 때 ClearButton 활성화
            this.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.textClearButton.visibility = View.VISIBLE
                } else {
                    binding.textClearButton.visibility = View.GONE
                }
            }

            /**
             * Rx 방식으로 사용자 입력 쿼리 처리 동작
             * - 사용자 입력이 멈추면 데이터 스트리밍 (입력 완료 상황으로 간주)
             */
            val editTextChangeObservable = binding.searchView.textChanges()
            val searchEditTextSubscription: Disposable =
                // 생성한 Observable 에 Operator 추가
                editTextChangeObservable
                    // 마지막 글자 입력 0.8초 후에 onNext 이벤트로 데이터 스트리밍
                    .debounce(800, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    // 구독을 통해 이벤트 응답 처리
                    .subscribeBy(
                        onNext = {
                            Log.d("Rx", "onNext : $it")
                            // 사용작 쿼리가 비어있지 않다면 API 호출
                            runOnUiThread {
                                if (!it.isNullOrBlank()) {
                                    refreshSearchResultList(it.toString())
                                } else {
                                    binding.storeListRecyclerView.visibility = View.GONE
                                    binding.noResultCard.visibility = View.GONE
                                }
                            }
                        },
                        onComplete = {
                            Log.d("Rx", "onComplete")
                        },
                        onError = {
                            Log.d("Rx", "onError : $it")
                        }
                    )
            // CompositeDisposable 에 추가
           // Log.d("Rx", "search Eddit : $searchEditTextSubscription")
            compositeDisposable.add(searchEditTextSubscription)
        }

        // ClearButton 눌렀을 때 쿼리 Clear
        binding.textClearButton.setOnClickListener {
            binding.searchView.text.clear()
        }
    }


    /**
     * 키보드 이외의 영역을 터치했을 때, 키보드를 숨기는 동작
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val view = currentFocus
        if (view != null && (ev!!.action === ACTION_UP || MotionEvent.ACTION_MOVE === ev!!.action) &&
            view is EditText && !view.javaClass.name.startsWith("android.webkit.")
        ) {
            binding.storeListRecyclerView.visibility = View.GONE
            binding.noResultCard.visibility = View.GONE

            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev!!.rawX + view.getLeft() - scrcoords[0]
            val y = ev!!.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }

        return super.dispatchTouchEvent(ev)
    }
/*
    private fun refreshSearchResultList(userQuery: String) {
       // 사용자에 의해 변경된 쿼리(검색어) 기반으로 API 호출
        viewModel.getSearchResultStoreList(userQuery)
        Log.d(TAG, "refresh")
        viewModel.searchResultStoreListLiveData.observe(
            this@MapActivity,
            {
                StoreList(it).storeList.forEach {
                    Log.d(TAG, it.name)
                }
                // API 호출 결과 데이터가 있다면 아이템 갱신 적용
                Log.d("storeList", "$it")
                if (it.size != 0) {

                    YoYo.with(Techniques.SlideInUp)
                        .duration(700)
                        .playOn(binding.storeListRecyclerView)

                    binding.storeListRecyclerView.visibility = View.VISIBLE
                    binding.noResultCard.visibility = View.GONE
                    recyclerViewAdapter.setItem(StoreList(it).storeList)
                } else {
                    // 만약 아이템이 없다면, '아이템 없음'을 사용자에게 알리는 뷰를 띄워줌
                    binding.storeListRecyclerView.visibility = View.GONE
                    binding.noResultCard.visibility = View.VISIBLE
                }
            })
    }

 */
    private fun refreshSearchResultList(userQuery: String) {
        // 사용자에 의해 변경된 쿼리(검색어) 기반으로 API 호출
        val fusedLocationSource = locationSource // 임시 변수에 할당하여 변경 가능성을 제거
        val lastLocation = fusedLocationSource?.lastLocation // fusedLocationSource가 null이면 null 할당
        if (lastLocation != null) {
            viewModel.getSearchResultStoreList(
                userQuery,
                lastLocation.longitude,
                lastLocation.latitude
            )
        }
        Log.d(TAG, "refresh")
        viewModel.searchResultStoreListLiveData.observe(
            this@MapActivity,
            { storeResponse ->
                Log.d(TAG, "storeResponse.items is null? $storeResponse")
                storeResponse?.documents?.let { items ->
                    items.forEach {
                        Log.d(TAG, it.place_name) // StoreResponseItem의 필드에 맞게 수정
                    }
                    // API 호출 결과 데이터가 있다면 아이템 갱신 적용
                    Log.d("storeList", "$storeResponse")
                    if (items.isNotEmpty()) {

                        YoYo.with(Techniques.SlideInUp)
                            .duration(700)
                            .playOn(binding.storeListRecyclerView)

                        binding.storeListRecyclerView.visibility = View.VISIBLE
                        binding.noResultCard.visibility = View.GONE
                        recyclerViewAdapter.setItem(ArrayList(items))
                    } else {
                        // 만약 아이템이 없다면, '아이템 없음'을 사용자에게 알리는 뷰를 띄워줌
                        binding.storeListRecyclerView.visibility = View.GONE
                        binding.noResultCard.visibility = View.VISIBLE
                    }
                } ?: run {
                    // storeResponse.items가 null인 경우 처리
                    Log.d(TAG, "storeResponse.items is null")
                    binding.storeListRecyclerView.visibility = View.GONE
                    binding.noResultCard.visibility = View.VISIBLE
                }
            })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("xqqqqqqqqqq", "requestcode: $resultCode")
        Log.d("xqqqqqqqqqq", "requestcode2: $STORE_DETAIL_REQUEST_CODE")
        Log.d("xqqqqqqqqqq", "requestcode3: ${AppCompatActivity.RESULT_OK}")

        if (requestCode == STORE_DETAIL_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.getParcelableExtra<StoreResponseItem>(StoreDetailActivity.STORE_ITEM)?.let {
                val resultIntent = Intent().apply {
                    putExtra(StoreDetailActivity.STORE_ITEM, it)
                }
                setResult(AppCompatActivity.RESULT_OK, resultIntent)
                finish() // MapActivity를 종료하고 NewDiary로 돌아갑니다.
            }
        }
    }



    override fun onDestroy() {
        // MemoryLeak 방지를 위해 CompositeDisposable 해제
        this.compositeDisposable.clear()
        super.onDestroy()
    }
}
