package com.example.storyapp.data

import com.example.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyQuoteResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "user: $i",
                "description: $i",
                "image:  $i",
                "created story: $i",
                0.0 + i,
                0.0 + i
            )
            items.add(quote)
        }
        return items
    }
}
