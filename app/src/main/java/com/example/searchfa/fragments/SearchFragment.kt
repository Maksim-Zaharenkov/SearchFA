package com.example.searchfa.fragments

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
import com.example.searchfa.R
import com.example.searchfa.adapters.RecyclerViewAdapter
import com.example.searchfa.data.AudienceData
import com.example.searchfa.databinding.FragmentSearchBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
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

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        viewModel.liveDataSearchList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    fun init() = with(binding){
        rvSearch.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerViewAdapter()
        rvSearch.adapter = adapter
    }

    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}