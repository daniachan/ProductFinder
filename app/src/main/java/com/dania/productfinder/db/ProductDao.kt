package com.dania.productfinder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dania.productfinder.vo.SearchResult
import com.dania.productfinder.vo.Product

@Dao
abstract class ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg repos: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProducts(repositories: List<Product>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createProductIfNotExists(repo: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: SearchResult)

    @Query("SELECT * FROM SearchResult WHERE `query` = :query")
    abstract fun search(query: String): LiveData<SearchResult?>

    @Query("SELECT * FROM Product WHERE productID in (:productIDs)")
    abstract fun loadById(productIDs: List<Int>): LiveData<List<Product>>

    @Query("SELECT * FROM Product")
    abstract fun load(): LiveData<List<Product>>

    @Query("SELECT * FROM SearchResult WHERE `query` = :query")
    abstract fun findSearchResult(query: String): SearchResult?

    @Query("DELETE FROM Product")
    abstract fun deleteAll()

    @Query("SELECT * FROM SearchResult")
    abstract fun getAllResults(): LiveData<List<SearchResult>>

    @Query("DELETE FROM SearchResult WHERE `query` = :query ")
    abstract fun deleteItemResult(query: String)

    @Query("DELETE FROM SearchResult ")
    abstract fun deleteAllItems()
}