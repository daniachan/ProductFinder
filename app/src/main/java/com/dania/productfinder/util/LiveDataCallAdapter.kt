package com.dania.productfinder.util

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean
import com.dania.productfinder.api.APIResponse

class LiveDataCallAdapter<R>(private val responseType: Type) :
        CallAdapter<R, LiveData<APIResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<APIResponse<R>> {
        return object : LiveData<APIResponse<R>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(APIResponse.create(response))
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            postValue(APIResponse.create(throwable))
                        }
                    })
                }
            }
        }
    }
}