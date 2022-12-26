package com.example.searchfa.fragments

import android.R
import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.searchfa.MainActivity
import com.example.searchfa.MainViewModel
import com.example.searchfa.data.AudienceData
import com.example.searchfa.databinding.ActivityMainBinding
import com.example.searchfa.databinding.FragmentMainBinding
import com.example.searchfa.sqlite.UserDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val db = Firebase.firestore
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.calendarView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setDate()

        setTime()
    }

    fun init() = with(binding) {
        btnCalendar.setOnClickListener {
            calendarView.visibility = View.VISIBLE
        }
        btnSearch.setOnClickListener {
            calendarView.visibility = View.GONE

            var userCode: String? = null

            Thread{
                val sqliteDb = UserDatabase.getDatabase(requireContext())
                userCode = sqliteDb.getDao().getUser()[0].code

                val searchParams = AudienceData(
                    spinnerTypeAudience.selectedItem.toString(),
                    "",
                    btnCalendar.text.toString(),
                    spinnerWorkingHours.selectedItem.toString(),
                    "search",
                    "",
                    userCode.toString()
                )
                getListOfFreeAudiences(searchParams)

            }.start()

            val tag = "SearchFragment"
            parentFragmentManager
                .beginTransaction()
                .replace(com.example.searchfa.R.id.placeHolder_2, SearchFragment.newInstance(), tag)
                .commit()

            activity?.let {
                (it as MainActivity).goToSearchRecyclerView()
            }
        }

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            btnCalendar.text = "$dayOfMonth.${month + 1}.$year"
            setTime()
            binding.calendarView.visibility = View.GONE
        }
    }

    private fun getData() {

    }

    @SuppressLint("SimpleDateFormat")
    private fun setDate() = with(binding) {

        val time = GregorianCalendar.getInstance()
        val formatterTime = SimpleDateFormat("H:mm")
        val currentTime = formatterTime.format(time.time)

        val date = GregorianCalendar.getInstance()
        val formatterDate = SimpleDateFormat("d.MM.yyyy")

        val maxDate = GregorianCalendar.getInstance()

        if (formatterTime.parse(currentTime)!! <= formatterTime.parse("20:05")) {
            maxDate.add(GregorianCalendar.DAY_OF_MONTH, 5)
        }
        else {
            date.add(GregorianCalendar.DAY_OF_MONTH, 1)
            maxDate.add(GregorianCalendar.DAY_OF_MONTH, 6)
        }

        btnCalendar.text = formatterDate.format(date.time)
        calendarView.maxDate = maxDate.timeInMillis
        calendarView.minDate = date.timeInMillis
    }

    @SuppressLint("SimpleDateFormat")
    private fun setTime() = with(binding) {
        val time = GregorianCalendar.getInstance()
        val formatterTime = SimpleDateFormat("H:mm")
        val date = GregorianCalendar.getInstance()
        val formatterDate = SimpleDateFormat("d.MM.yyyy")
        val currentDate = formatterDate.format(date.time)

        val arrayWorkingHours = arrayListOf(
            "9:00 - 10:20",
            "10:35 - 11:55",
            "12:25 - 13:45",
            "14:00 - 15:20",
            "15:50 - 17:10",
            "17:25 - 18:45",
            "19:00 - 20:20",
            "20:35 - 21:55"
        )
        var arrayCurrentTime = emptyArray<String>()

        val currentTime = formatterTime.format(time.time)


        if (formatterDate.parse(binding.btnCalendar.text.toString()) == formatterDate.parse(currentDate)) {
            when {
                formatterTime.parse(currentTime)!! <= formatterTime.parse("8:30")
                -> for (i in 0 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("8:30")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("10:05")
                -> for (i in 1 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("10:05")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("11:55")
                -> for (i in 2 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("11:55")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("13:30")
                -> for (i in 3 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("13:30")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("15:20")
                -> for (i in 4 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("15:20")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("16:55")
                -> for (i in 5 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("16:55")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("18:30")
                -> for (i in 6 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("18:30")
                        && formatterTime.parse(currentTime)!! <= formatterTime.parse("20:05")
                -> for (i in 7 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]

                formatterTime.parse(currentTime)!! > formatterTime.parse("20:05")
                -> for (i in 0 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]
            }
        }
        else {
            for (i in 0 until arrayWorkingHours.size) arrayCurrentTime += arrayWorkingHours[i]
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrayCurrentTime)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        spinnerWorkingHours.adapter = adapter
    }

    private fun getListOfFreeAudiences(searchParams: AudienceData){
        val listOfFreeAudience = ArrayList<AudienceData>()
        var check = false

        db.collection("audiences")
            .whereEqualTo("type", searchParams.type)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("audiences")
                        .document(document.id)
                        .collection("busy-info")
                        .get()
                        .addOnSuccessListener { resultTwo ->
                            for (documentTwo in resultTwo){
                                check = !(searchParams.date == documentTwo.data["date"]
                                        && searchParams.time == documentTwo.data["time"])
                            }
                            if(check) {
                                val item = AudienceData(
                                    searchParams.type,
                                    document.data["number"] as String,
                                    searchParams.date,
                                    searchParams.time,
                                    searchParams.fragment,
                                    "",
                                    searchParams.user
                                )
                                listOfFreeAudience.add(item)
                                viewModel.liveDataSearchList.value = listOfFreeAudience
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}