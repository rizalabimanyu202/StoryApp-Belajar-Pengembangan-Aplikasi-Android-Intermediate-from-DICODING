package com.example.storyapp.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.response.ListStoryItem

@Dao
interface StoryDao {
    @Query("SELECT * FROM liststoryitem")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM liststoryitem")
    suspend fun deleteAllStories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<ListStoryItem>)
}