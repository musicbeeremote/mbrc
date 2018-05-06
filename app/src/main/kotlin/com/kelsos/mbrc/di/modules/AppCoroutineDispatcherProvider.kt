package com.kelsos.mbrc.di.modules

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Provider

class AppCoroutineDispatcherProvider
@Inject
constructor() : Provider<AppCoroutineDispatchers> {
  override fun get(): AppCoroutineDispatchers {
    return AppCoroutineDispatchers(
      Dispatchers.Main,
      Dispatchers.IO,
      Dispatchers.IO,
      Dispatchers.IO
    )
  }
}
