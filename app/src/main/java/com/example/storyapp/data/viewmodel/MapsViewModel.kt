package com.example.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.response.AddNewStoriesResponse
import com.example.storyapp.data.response.GetAllStoriesResponse
import com.example.storyapp.data.response.LoginResponse

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesFromLocation(token: String): LiveData<Result<GetAllStoriesResponse>> =
        storyRepository.getStoriesFromLocation(token)

    fun getToken(): LiveData<String> {
        return storyRepository.tokenToken.asLiveData()
    }
}