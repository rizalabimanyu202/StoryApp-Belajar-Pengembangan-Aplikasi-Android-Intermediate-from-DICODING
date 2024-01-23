package com.example.storyapp.data.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.dspref.UserPreferences
import com.example.storyapp.data.response.AddNewStoriesResponse
import com.example.storyapp.data.response.DetailStoriesResponse
import com.example.storyapp.data.response.ErrorResponse
import com.example.storyapp.data.response.GetAllStoriesResponse
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.data.retrofit.ApiService
import com.example.storyapp.data.requestdata.RequestNewStory
import com.example.storyapp.data.requestdata.RequestUser
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.room.StoryDatabase
import com.example.storyapp.ui.adapter.StoryPagingSource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPrefrences: UserPreferences,
    private val storyDatabase: StoryDatabase
    ) {

    val tokenToken: Flow<String> = userPrefrences.getToken()

    val nameName: Flow<String> = userPrefrences.getName()

    suspend fun saveToken(token: String){ userPrefrences.saveToken(token) }

    suspend fun saveName(name: String){ userPrefrences.saveName(name) }

    fun getRegister(requestUser: RequestUser): LiveData<Result<RegisterResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(requestUser.name, requestUser.email, requestUser.password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun getLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun detailStories(token: String, id: String): LiveData<Result<DetailStoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.detailStories("Bearer $token", id)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun addStoriesWithLocation(token: String, dataUpload: RequestNewStory): LiveData<Result<AddNewStoriesResponse>> = liveData {
        emit(Result.Loading)
        val requestDescription = dataUpload.description.toRequestBody("text/plain".toMediaType())
        val requestImageStoryFile = dataUpload.imageStoryFile.asRequestBody("image/jpeg".toMediaType())
        val requestLatitute = dataUpload.lat.toString().toRequestBody("text/plain".toMediaType())
        val requestLongitute = dataUpload.lon.toString().toRequestBody("text/plain".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            dataUpload.imageStoryFile.name,
            requestImageStoryFile
        )
        try {
            val response = apiService.addStoriesWithLocation("Bearer $token", multipartBody, requestDescription, requestLatitute, requestLongitute)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun addStoriesWithoutLocation(token: String, dataUpload: RequestNewStory): LiveData<Result<AddNewStoriesResponse>> = liveData {
        emit(Result.Loading)
        val requestDescription = dataUpload.description.toRequestBody("text/plain".toMediaType())
        val requestImageStoryFile = dataUpload.imageStoryFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            dataUpload.imageStoryFile.name,
            requestImageStoryFile
        )
        try {
            val response = apiService.addStoriesWithoutLocation("Bearer $token", multipartBody, requestDescription)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    fun getStoriesFromLocation(token: String): LiveData<Result<GetAllStoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesFromLocation("Bearer $token")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBodyResponse = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessageResponse = errorBodyResponse.message
            emit(Result.Error(errorMessageResponse.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, userPrefrences: UserPreferences, storyDatabase: StoryDatabase): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPrefrences, storyDatabase)
            }.also { instance = it }
    }
}