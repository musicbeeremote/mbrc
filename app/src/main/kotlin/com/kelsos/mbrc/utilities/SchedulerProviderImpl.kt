package com.kelsos.mbrc.utilities

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject

class SchedulerProviderImpl
@Inject
constructor() : SchedulerProvider {

  private val dbExecutor = Executors.newSingleThreadExecutor { Thread(it, "db-operations") }
  private val dbScheduler = Schedulers.from(dbExecutor)

  private val syncExecutor = Executors.newSingleThreadExecutor { Thread(it, "sync") }
  private val syncScheduler = Schedulers.from(syncExecutor)

  override fun io(): Scheduler = Schedulers.io()

  override fun main(): Scheduler = AndroidSchedulers.mainThread()

  override fun computation(): Scheduler = Schedulers.computation()

  override fun db(): Scheduler = dbScheduler

  override fun sync(): Scheduler = syncScheduler
}
