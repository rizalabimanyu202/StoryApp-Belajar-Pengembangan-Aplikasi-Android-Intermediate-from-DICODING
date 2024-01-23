package com.example.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getLogin(email: String, password:String): LiveData<Result<LoginResponse>> =
        storyRepository.getLogin(email, password)

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