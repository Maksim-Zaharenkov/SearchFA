package com.example.searchfa

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.searchfa.data.AudienceData

class MainViewModel: ViewModel() {

    val liveDataSearchList = MutableLiveData<List<AudienceData>>()
    val liveDataBookingsList = MutableLiveData<List<AudienceData>>()
}