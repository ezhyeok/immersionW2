package com.example.tab3

import android.content.Intent
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tab3.databinding.DiaryCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.example.tab3.ui.notifications.NotificationsViewModel
import com.naver.maps.map.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class DiaryFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var binding: DiaryCalendarBinding
    var db: AppDatabase? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener("NewDiaryClosed", this) { _, _ ->
            Log.d("qssssssssss", "Received result")
            val today = CalendarDay.today()
            showView(today.year, today.month, today.day)
            calendarView.removeDecorators()
            addDecorators()
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DiaryCalendarBinding.inflate(inflater, container, false)
        calendarView = binding.calendar as MaterialCalendarView
        val bottomSheetLayout: LinearLayout = binding.layout.bottomSheetLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetLayout?.visibility = View.VISIBLE
        Log.d("When Create", "onCreateView: ${bottomSheetBehavior?.state}")
        Log.d("When Create", "Layout visibility: ${bottomSheetLayout?.visibility}")




        /*
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }


         */

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getInstance(requireContext())
        Log.d("ViewCreated", "onCreateView: ${bottomSheetBehavior?.state}")
        /*
        view?.post {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }

         */


        //calendar setup
        val today = CalendarDay.today()
        calendarView.setOnDateChangedListener { _, date, _ ->
            showView(date.year, date.month, date.day)
            binding.layout.diaryimg.setOnClickListener {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                navigateToNewDiary(date.year, date.month, date.day)
                Log.d("diary refresh", "${bottomSheetBehavior?.state}")
                showView(date.year, date.month, date.day)
                //showView(today.year, today.month, today.day)
                calendarView.removeDecorators()
                addDecorators()
            //bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            val monthFormatted = String.format(Locale.getDefault(), "%02d", date.month)
            val dayFormatted = String.format(Locale.getDefault(), "%02d", date.day)
            binding.layout.todayDate.text = "${date.year}/$monthFormatted/$dayFormatted"
        }

        //show initial view for today's date
        showView(today.year, today.month, today.day)
        binding.layout.todayDate.text = "${today.year}/${String.format(Locale.getDefault(), "%02d", today.month)}/${String.format(Locale.getDefault(), "%02d", today.day)}"

        binding.layout.diaryimg.setOnClickListener{
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            navigateToNewDiary(today.year, today.month, today.day)
            Log.d("diary refresh", "${bottomSheetBehavior?.state}")
            val today = CalendarDay.today()
            showView(today.year, today.month, today.day)
            calendarView.removeDecorators()
            addDecorators()

        }

        // add decorators (dots) for dates with entries
        addDecorators()


        bottomSheetBehavior.apply {

        }

        /*val todayCalendar = Calendar.getInstance()
        val todayYear = todayCalendar.get(Calendar.YEAR)
        val todayMonth = todayCalendar.get(Calendar.MONTH) + 1
        val todayDay = todayCalendar.get(Calendar.DAY_OF_MONTH)
        showView(todayYear, todayMonth, todayDay)

        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val today = dateFormat.format(todayCalendar.time)
        binding.layout.todayDate.text = today

        binding.layout.diaryimg.setOnClickListener {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            navigateToNewDiary(todayYear, todayMonth, todayDay)
        }

        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            showView(year, month+1, dayOfMonth)
            binding.layout.diaryimg.setOnClickListener {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                navigateToNewDiary(year, month+1, dayOfMonth) // Workout data 전체 보내기
            }

            val monthFormatted = String.format(Locale.getDefault(), "%02d", month+1)
            val dayFormatted = String.format(Locale.getDefault(), "%02d", dayOfMonth)
            binding.layout.todayDate.text = "$year/$monthFormatted/$dayFormatted"
        } */

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN){
                //val today = CalendarDay.today()
                //showView(today.year, today.month, today.day)
                calendarView.removeDecorators()
                addDecorators()

                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun showView(year:Int, month:Int, day:Int){
        CoroutineScope(Dispatchers.IO).launch{
            val dateWorkout = db?.diaryDao()?.getWorkoutByDate(year, month, day)
            var selectedWorkout: Diary? = dateWorkout?.firstOrNull()  //선택한 날짜의 diary 데이터
            Log.d("check", "$selectedWorkout")
            val transformation = MultiTransformation(
                CenterCrop(),
                RoundedCorners(50)
            )

            withContext(Dispatchers.Main) {
                if (selectedWorkout != null) {
                    // changing View to uploaded images
                    // **changing 일기 이미지
                    if(selectedWorkout.place_name!=null){
                        binding.layout.storeName.text=selectedWorkout.place_name
                    }else{
                        binding.layout.storeName.text=""
                    }
                    if(selectedWorkout.address_name!=null){
                        binding.layout.storeAddress.text=selectedWorkout.address_name
                    }else{
                        binding.layout.storeAddress.text=""
                    }
                    if (selectedWorkout.diaryImg != null) {
                        Glide.with(requireContext())
                            .load(selectedWorkout.diaryImg)
                            .apply(RequestOptions.bitmapTransform(transformation))
                            .into(binding.layout.diaryimg)
                        val xx=selectedWorkout.diaryContent
                        if(xx!=null && xx.isNotEmpty())
                            binding.layout.diaryContent.text = xx
                        else
                            binding.layout.diaryContent.text = resources.getString(R.string.nodiary)
                    } else {
                        if(selectedWorkout.place_name!=null){
                            binding.layout.storeName.text=selectedWorkout.place_name
                        }else{
                            binding.layout.storeName.text=""
                        }
                        if(selectedWorkout.address_name!=null){
                            binding.layout.storeAddress.text=selectedWorkout.address_name
                        }else{
                            binding.layout.storeAddress.text=""
                        }
                        Glide.with(requireContext())
                            .load(R.drawable.blankimg)
                            .apply(RequestOptions.bitmapTransform(transformation))
                            .into(binding.layout.diaryimg)

                        binding.layout.diaryContent.text = selectedWorkout.diaryContent
                    }

                    // Set click listener for the delete diary image to delete diary entry
                    binding.layout.deletediary.setOnClickListener {
                        deleteDiary(year, month, day)
                    }

                    // changing View finished
                } else {    // 선택된 날짜에 해당하는 데이터가 없는 경우
                    // db에 해당 날짜의 데이터를 추가
                    binding.layout.storeName.text=""
                    binding.layout.storeAddress.text=""
                    Glide.with(requireContext())
                        .load(R.drawable.blankimg)
                        .apply(RequestOptions.bitmapTransform(transformation))
                        .into(binding.layout.diaryimg)
                    binding.layout.diaryContent.text = resources.getString(R.string.nodiary)

                    // 일기 내역 없으면 리스너 제거
                    binding.layout.deletediary.setOnClickListener(null)
                }
            }
        }
    }

    private fun navigateToNewDiary(year: Int, month: Int, day: Int) {
        val newDiary = NewDiary.newInstance(year, month, day)
        childFragmentManager.beginTransaction()
            .replace(R.id.calendarFragment, newDiary)
            .addToBackStack(null)
            .commit()


    }

    private fun deleteDiary(year: Int, month: Int, day: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val dateWorkout = db?.diaryDao()?.getWorkoutByDate(year, month, day)
            val selectedWorkout: Diary? = dateWorkout?.firstOrNull()

            selectedWorkout?.let {
                db?.diaryDao()?.delete(it)
                withContext(Dispatchers.Main) {
                    Glide.with(requireContext())
                        .load(R.drawable.blankimg)
                        .apply(
                            RequestOptions.bitmapTransform(
                                MultiTransformation(
                                    CenterCrop(),
                                    RoundedCorners(50)
                                )
                            )
                        )
                        .into(binding.layout.diaryimg)
                    binding.layout.diaryContent.text = resources.getString(R.string.nodiary)
                    binding.layout.deletediary.setOnClickListener(null)
                    binding.layout.storeName.text=null
                    binding.layout.storeAddress.text=null

                    // Update decorators after deletion
                    calendarView.removeDecorators()
                    addDecorators()
                }
            }
        }
    }

    fun addDecorators() {
        CoroutineScope(Dispatchers.IO).launch {
            val datesWithEntries = db?.diaryDao()?.getAll() ?: emptyList()
            val decorator = EventDecorator(
                this@DiaryFragment,
                requireContext().getColor(R.color.gray),
                datesWithEntries.map { CalendarDay.from(it.year, it.month, it.date) }.toHashSet()
            )
            withContext(Dispatchers.Main) {
                calendarView.addDecorator(decorator)
            }
        }
        calendarView.addDecorator(SundayDecorator())
        calendarView.addDecorator(SaturdayDecorator())
    }

    inner class SundayDecorator() : DayViewDecorator {
        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            if (day == null) {
                return false
            }

            //캘린더 셋업
            calendar.set(day.year, day.month-1, day.day)

            //일주일을 받아옴
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            //그중 일요일을 리턴
            return weekDay == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade?) {
            //하루(일요일)의 전체 텍스트에 범위의 색 추가
            val orange = ContextCompat.getColor(requireContext(), R.color.orange)
            view?.addSpan(object : ForegroundColorSpan(orange){})
        }

    }

    inner class SaturdayDecorator() : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            if (day == null) {
                return false
            }

            //캘린더 셋업
            calendar.set(day.year, day.month-1, day.day)

            //일주일을 받아옴
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            //그중 토요일을 리턴
            return weekDay == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade?) {
            val orange = ContextCompat.getColor(requireContext(), R.color.orange)
            view?.addSpan(object : ForegroundColorSpan(orange){})
        }

    }

    override fun onResume() {
        super.onResume()
        //val today = CalendarDay.today()
        //showView(today.year, today.month, today.day)
        calendarView.removeDecorators()
        addDecorators()
        calendarView.addDecorator(SundayDecorator())
        calendarView.addDecorator(SaturdayDecorator())
    }

    class EventDecorator(private val fragment: DiaryFragment, private val color: Int, private val dates: HashSet<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5f, color))
            /*
            if (fragment.bottomSheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
                val today = CalendarDay.today()
                fragment.showView(today.year, today.month, today.day)
                fragment.calendarView.removeDecorators()
                fragment.addDecorators()

                fragment.bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }

             */

        }


    }
}