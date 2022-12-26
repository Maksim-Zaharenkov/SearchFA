package com.example.searchfa.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.searchfa.MainViewModel
import com.example.searchfa.adapters.RecyclerViewAdapter
import com.example.searchfa.data.AudienceData
import com.example.searchfa.databinding.FragmentBookingsBinding
import com.example.searchfa.sqlite.UserDatabase
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingsFragment : Fragment() {
    private lateinit var binding: FragmentBookingsBinding
    private val db = Firebase.firestore
    private lateinit var adapter: RecyclerViewAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        var userCode: String? = null

        Thread{
            val sqliteDb = UserDatabase.getDatabase(requireContext())
            userCode = sqliteDb.getDao().getUser()[0].code
            getListOfBookingAudiences(userCode.toString())
        }.start()

        viewModel.liveDataBookingsList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    fun init() = with(binding){
        rvBookings.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerViewAdapter()
        rvBookings.adapter = adapter
    }

    @SuppressLint("SimpleDateFormat")
    private fun getListOfBookingAudiences(userCode: String) {
        val dateTime = GregorianCalendar.getInstance()
        val formatterTime = SimpleDateFormat("H:mm")
        val formatterDate = SimpleDateFormat("d.MM.yyyy")
        val currentTime = formatterTime.format(dateTime.time)
        val currentDate = formatterDate.format(dateTime.time)

        val listBookingAudiences = ArrayList<AudienceData>()
        db.collection("audiences")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("audiences")
                        .document(document.id)
                        .collection("busy-info")
                        .get()
                        .addOnSuccessListener { resultTwo ->
                            for (documentTwo in resultTwo){
                                if(userCode == documentTwo.data["user"]) {
                                    if ((formatterDate.parse(currentDate)!! < formatterDate.parse(documentTwo.data["date"] as String))
                                        || (formatterDate.parse(currentDate)!! == formatterDate.parse(documentTwo.data["date"] as String) &&
                                                formatterTime.parse(currentTime)!! < formatterTime.parse((documentTwo.data["time"] as String).substringBefore(" ")))) {

                                        val item = AudienceData(
                                            document.data["type"] as String,
                                            document.data["number"] as String,
                                            documentTwo.data["date"] as String,
                                            documentTwo.data["time"] as String,
                                            "booking",
                                            documentTwo.id,
                                            ""
                                        )
                                        listBookingAudiences.add(item)
                                        viewModel.liveDataBookingsList.value = listBookingAudiences
                                    }
                                }
                            }
                        }
                }
                Log.d("max", listBookingAudiences.size.toString())
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

    }

    companion object {

        @JvmStatic
        fun newInstance() = BookingsFragment()
    }
}