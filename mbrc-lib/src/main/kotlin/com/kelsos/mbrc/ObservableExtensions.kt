package com.kelsos.mbrc

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Helper extension to add Subscribe on IO Scheduler and Observe an
 * Android's main thread Scheduler
 */
fun<T> Observable<T>.task() : Observable<T> {
  return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun<T> Observable<T>.io() : Observable<T> {
  return this.subscribeOn(Schedulers.io())
}

