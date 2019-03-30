package com.kelsos.mbrc.utils

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import org.koin.dsl.module.module

object TestDispatchers {
  private val trampoline = Schedulers.trampoline()
  private val trampolineDispatcher = trampoline.asCoroutineDispatcher()

  val schedulers = AppRxSchedulers(trampoline, trampoline, trampoline, trampoline)
  val dispatchers = AppCoroutineDispatchers(
    trampolineDispatcher,
    trampolineDispatcher,
    trampolineDispatcher,
    trampolineDispatcher
  )
}

val testDispatcherModule = module {
  single { TestDispatchers.schedulers }
  single { TestDispatchers.dispatchers }
}