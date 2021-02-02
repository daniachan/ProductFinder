package com.dania.productfinder.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import com.dania.productfinder.api.*
import com.dania.productfinder.db.StoreDb
import com.dania.productfinder.vo.SearchResult
import com.dania.productfinder.vo.Resource

class FetchNextSearchPageTask constructor(
    private val force: String,
    private val search: String,
    private val itemsPerPage: Int,
    private val storeService: StoreService,
    private val db: StoreDb
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val current = db.productDao().findSearchResult(search)
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.next
        if (nextPage == null) {
            _liveData.postValue(Resource.success(false))
            return
        }

        val newValue = try {
            val response = storeService.searchPlpCall("true",search,nextPage + 1,10).execute()
            when (val apiResponse = APIResponse.create(response)) {
                is ApiSuccessResponse -> {
                    // we merge all repo ids into 1 list so that it is easier to fetch the
                    // result list.

                    val merged = SearchResult(
                        search,
                        10,
                        nextPage + 1
                    )
                    db.runInTransaction {
                        db.productDao().insert(merged)
                        db.productDao().insertProducts(apiResponse.body.plpResults.records)
                    }
                    Resource.success(apiResponse.nextPage != null)
                }
                is ApiEmptyResponse -> {
                    Resource.success(null)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
