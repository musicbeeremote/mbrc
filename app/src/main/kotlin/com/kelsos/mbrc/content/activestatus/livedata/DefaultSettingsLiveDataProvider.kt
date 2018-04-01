package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.util.WeakHashMap
import javax.inject.Inject

interface DefaultSettingsLiveDataProvider {

  fun observe(obj: Any, onSettings: OnDefaultConnectionChanged)

  fun update(connectionSettings: ConnectionSettingsEntity)

  fun removeObservers(obj: Any)
}

typealias OnDefaultConnectionChanged = (ConnectionSettingsEntity) -> Unit

class DefaultSettingsLiveDataProviderImpl
@Inject
constructor(private val dispatchers: AppDispatchers) : DefaultSettingsLiveDataProvider {
  private val settingsRelay: MutableSharedFlow<ConnectionSettingsEntity> = MutableSharedFlow(0, 5)
  private val map: WeakHashMap<Any, Job> = WeakHashMap()
  private val scope = CoroutineScope(dispatchers.io)

  override fun observe(obj: Any, onSettings: OnDefaultConnectionChanged) {
    map[obj] = settingsRelay.onEach { withContext(dispatchers.main) { onSettings(it) } }
      .distinctUntilChanged()
      .launchIn(scope)
  }

  override fun update(connectionSettings: ConnectionSettingsEntity) {
    settingsRelay.tryEmit(connectionSettings)
  }

  override fun removeObservers(obj: Any) {
    map.remove(obj)
  }
}
