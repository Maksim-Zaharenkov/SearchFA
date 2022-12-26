package com.example.searchfa.adapters

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.searchfa.MainActivity
import com.example.searchfa.R
import com.example.searchfa.data.AudienceData
import com.example.searchfa.databinding.RecyclerViewItemBinding
import com.example.searchfa.fragments.SearchFragment
import com.example.searchfa.sqlite.UserDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecyclerViewAdapter: ListAdapter<AudienceData, RecyclerViewAdapter.Holder>(Comparator()) {

    class Holder(view: View, context: Context): RecyclerView.ViewHolder(view){
        val binding = RecyclerViewItemBinding.bind(view)
        private val db = Firebase.firestore
        val contextH = context

        fun bind(item: AudienceData) = with(binding){
            val typeNum = item.type + " №" + item.number
            val date = "Дата: " + item.date
            val time = "Время: " + item.time
            tvTypeNum.text = typeNum
            tvDate.text = date
            tvTime.text = time

            when(item.fragment){
                "search" -> btnCancelOrBooking.text = "Забронировать"
                "booking" -> btnCancelOrBooking.text = "Отменить бронь"
            }

            btnCancelOrBooking.setOnClickListener {

                when(item.fragment){
                    "search" -> {
                        writeBooking(item)

                        contextH.let {
                            (it as MainActivity).updateSearchFragment()
                        }
                    }
                    "booking" ->{
                        cancelBooking(item)

                        contextH.let {
                            (it as MainActivity).updateBookingsFragment()
                        }
                    }
                }
            }
        }

        private fun cancelBooking(item: AudienceData) {

            db.collection("audiences")
                .document(item.number)
                .collection("busy-info")
                .document(item.idBusyInfo)
                .delete()
                .addOnSuccessListener { documentReference ->
                    Log.d("deleteLog", "Document deleted")
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error deleting documents.", exception)
                }
        }

        private fun writeBooking(item: AudienceData) {

            val busyInfo = hashMapOf(
                "date" to item.date,
                "time" to item.time,
                "user" to item.user
            )

            db.collection("audiences")
                .document(item.number)
                .collection("busy-info")
                .add(busyInfo)
                .addOnSuccessListener { documentReference ->
                    Log.d("addLog", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error adding documents.", exception)
                }
        }
    }

    class Comparator: DiffUtil.ItemCallback<AudienceData>() {

        override fun areItemsTheSame(
            oldItem: AudienceData,
            newItem: AudienceData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AudienceData,
            newItem: AudienceData
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return Holder(view, parent.context)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}