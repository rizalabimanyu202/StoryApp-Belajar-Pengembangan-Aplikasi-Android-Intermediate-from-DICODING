package com.example.storyapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.Result
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.requestdata.RequestUser
import com.example.storyapp.data.response.RegisterResponse

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel()  {
    fun getRegister(requestUser: RequestUser): LiveData<Result<RegisterResponse>> =
        storyRepository.getRegister(requestUser)
}