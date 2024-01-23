package com.example.storyapp.data.dl

import android.content.Context
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.dspref.UserPreferences
import com.example.storyapp.data.dspref.dataStore
import com.example.storyapp.data.retrofit.ApiConfig
import com.example.storyapp.data.room.StoryDatabase

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val userPreferences = UserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, userPreferences, storyDatabase)
    }
}