package com.kelsos.mbrc.di.modules

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.rx2.asCoroutineDispatcher
import javax.inject.Inject
import javax.inject.Provider

class AppCoroutineDispatcherProvider
@Inject
constructor(private val appRxSchedulers: AppRxSchedulers) : Provider<AppCoroutineDispatchers> {
  override fun get(): AppCoroutineDispatchers {
    return AppCoroutineDispatchers(
      UI,
      appRxSchedulers.disk.asCoroutineDispatcher(),
      appRxSchedulers.network.asCoroutineDispatcher(),
      appRxSchedulers.database.asCoroutineDispatcher()
    )
  }

}
