package com.dania.productfinder.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dania.productfinder.vo.*

/**
 * Main database description.
 */
@Database(
    entities = [
        Product::class,
        SearchResult::class],
    version = 4,
    exportSchema = false
)
abstract class StoreDb : RoomDatabase() {
    abstract fun productDao(): ProductDao
}