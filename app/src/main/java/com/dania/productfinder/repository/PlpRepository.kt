package com.dania.productfinder.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.dania.productfinder.AppExecutors
import com.dania.productfinder.api.ApiSuccessResponse
import com.dania.productfinder.api.StoreService
import com.dania.productfinder.api.SearchResponse
import com.dania.productfinder.db.StoreDb
import com.dania.productfinder.db.ProductDao
import com.dania.productfinder.util.RateLimiter
import com.dania.productfinder.vo.SearchResult
import com.dania.productfinder.vo.Product
import com.dania.productfinder.vo.Resource

@Singleton
//@OpenForTesting
class PlpRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: StoreDb,
    private val productDao: ProductDao,
    private val storeService: StoreService
) {

    private val repoListRateLimit = RateLimiter<String>(15, TimeUnit.MINUTES)

    fun getAllSugges() : LiveData<List<SearchResult>> {
        val allSuggest = db.productDao().getAllResults()
        return allSuggest
    }

    fun deleteItem(value: String) {
        db.productDao().deleteItemResult(value)
    }

    fun deleteAllItems() {
        db.productDao().deleteAllItems()
    }

    fun searchNextPage(search: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(
                force = "true",
                search = search,
                itemsPerPage = 15,
                storeService = storeService,
                db = db
        )
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(search: String): LiveData<Resource<List<Product>>> {

        return object : NetworkBoundResource<List<Product>, SearchResponse>(appExecutors) {

            override fun saveCallResult(item: SearchResponse) {
                val repoIds = item.plpResults.records.map { it.productID }
                val searchResult = SearchResult(
                        query = search,
                        totalCount = item.plpResults.plpState.totalNumRecs,
                        next = item.plpResults.plpState.firstRecNum
                )
                db.runInTransaction {
                    productDao.insertProducts(item.plpResults.records)
                    productDao.insert(searchResult)
                }
            }

            override fun shouldFetch(data: List<Product>?) = true

            override fun loadFromDb(): LiveData<List<Product>> {
                return productDao.load()
            }

            override fun cleanFromDb() {
                runBlocking(Dispatchers.Default) {
                    productDao.deleteAll()
                }
            }


            override fun createCall() = storeService.searchPlp("true",search,1,10 )

            override fun processResponse(response: ApiSuccessResponse<SearchResponse>)
                    : SearchResponse {
                val body = response.body
                body.nextPage = response.nextPage
                return body
            }
        }.asLiveData()
    }
}
