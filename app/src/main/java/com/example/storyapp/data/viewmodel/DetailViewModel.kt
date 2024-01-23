package com.example.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.response.DetailStoriesResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun detailStories(token: String, id: String): LiveData<Result<DetailStoriesResponse>> =
        storyRepository.detailStories(token, id)

    fun getToken(): LiveData<String> {
        return storyRepository.tokenToken.asLiveData()
    }

    fun saveToken(token: String){
        viewModelScope.launch {
            storyRepository.saveToken(token)
        }
    }

    fun saveName(token: String){
        viewModelScope.launch {
            storyRepository.saveName(token)
        }
    }
}