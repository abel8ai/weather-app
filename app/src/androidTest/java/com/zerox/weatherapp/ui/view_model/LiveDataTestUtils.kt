package com.zerox.weatherapp.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun<T> LiveData<T>.getorAwaitValue(): T{
    var data: T? = null
    val latch = CountDownLatch(1)
    latch.await()
    val observer = object: Observer<T>{
        override fun onChanged(t: T) {
            data = t
            this@getorAwaitValue.removeObserver(this)
            latch.countDown()
        }
    }
    this.observeForever(observer)

    try {
        if (!latch.await(2,TimeUnit.SECONDS))
            throw TimeoutException("Live Data never gets it value")
    }
    finally {
        this.removeObserver(observer)
    }
    return data as T
}