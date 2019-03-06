package com.kelsos.mbrc.utils

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import org.koin.dsl.module.module

val testDispatcherModule = module {
  val trampoline = Schedulers.trampoline()
  val trampolineDispatcher = trampoline.asCoroutineDispatcher()
  single {
    AppRxSchedulers(trampoline, trampoline, trampoline, trampoline)
  }
  single {
    AppCoroutineDispatchers(
      trampolineDispatcher,
      trampolineDispatcher,
      trampolineDispatcher,
      trampolineDispatcher
    )
  }
}