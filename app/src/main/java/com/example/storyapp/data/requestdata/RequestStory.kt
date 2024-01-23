package com.example.storyapp.data.requestdata

import java.io.File

data class RequestNewStory(
    val imageStoryFile: File,
    val description: String,
    val lat: Double? = null,
    val lon: Double? = null
)