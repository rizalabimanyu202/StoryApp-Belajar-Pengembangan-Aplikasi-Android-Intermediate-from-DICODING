package com.example.storyapp.data.retrofit

import com.example.storyapp.data.response.AddNewStoriesResponse
import com.example.storyapp.data.response.DetailStoriesResponse
import com.example.storyapp.data.response.GetAllStoriesResponse
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.response.LoginResult
import com.example.storyapp.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): GetAllStoriesResponse

    @GET("stories/{id}")
    suspend fun detailStories(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoriesResponse

    @GET("stories")
    suspend fun getStoriesFromLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1
    ): GetAllStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addStoriesWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ) : AddNewStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addStoriesWithoutLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : AddNewStoriesResponse

}