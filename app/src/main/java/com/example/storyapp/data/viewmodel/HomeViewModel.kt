package com.example.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel()  {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)

    fun getToken(): LiveData<String> {
        return storyRepository.tokenToken.asLiveData()
    }

    fun getName(): LiveData<String> {
        return storyRepository.nameName.asLiveData()
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