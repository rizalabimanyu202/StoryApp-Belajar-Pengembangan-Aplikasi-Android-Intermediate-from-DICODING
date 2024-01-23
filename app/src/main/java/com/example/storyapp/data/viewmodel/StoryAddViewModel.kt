package com.example.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.response.AddNewStoriesResponse
import com.example.storyapp.data.requestdata.RequestNewStory
import kotlinx.coroutines.launch

class StoryAddViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private var _latitute: MutableLiveData<Double> = MutableLiveData()
    val latitute: LiveData<Double> get() = _latitute

    private var _longitude: MutableLiveData<Double> = MutableLiveData()
    val longitude: LiveData<Double> get() = _longitude

    fun addStoriesWithLocation(token: String, rns: RequestNewStory): LiveData<Result<AddNewStoriesResponse>> =
        storyRepository.addStoriesWithLocation(token, rns);

    fun addStoriesWithoutLocation(token: String, rns: RequestNewStory): LiveData<Result<AddNewStoriesResponse>> =
        storyRepository.addStoriesWithoutLocation(token, rns);

    fun getToken(): LiveData<String> {
        return storyRepository.tokenToken.asLiveData()
    }

    fun setLocation(lat: Double, lon: Double){
        _latitute.postValue(lat)
        _longitude.postValue(lon)
    }
}