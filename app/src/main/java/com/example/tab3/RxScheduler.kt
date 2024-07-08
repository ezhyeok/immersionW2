package com.example.tab3

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T : Any> Single<T>.applySchedulers() = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
