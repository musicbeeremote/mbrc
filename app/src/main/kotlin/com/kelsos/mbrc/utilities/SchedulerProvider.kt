package com.kelsos.mbrc.utilities

import io.reactivex.Scheduler

interface SchedulerProvider {
  fun io(): Scheduler
  fun main(): Scheduler
  fun computation(): Scheduler
  fun db(): Scheduler
  fun sync(): Scheduler
}