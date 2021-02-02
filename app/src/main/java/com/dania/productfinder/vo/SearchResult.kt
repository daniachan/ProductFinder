package com.dania.productfinder.vo

import androidx.room.Entity
import androidx.room.TypeConverters
import com.dania.productfinder.db.StoreTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(StoreTypeConverters::class)
data class SearchResult(
    val query: String,
    val totalCount: Int,
    val next: Int?
)