package com.kelsos.mbrc.common.utilities

import io.reactivex.Scheduler

data class AppRxSchedulers(
  val main: Scheduler,
  val disk: Scheduler,
  val database: Scheduler,
  val network: Scheduler
)
