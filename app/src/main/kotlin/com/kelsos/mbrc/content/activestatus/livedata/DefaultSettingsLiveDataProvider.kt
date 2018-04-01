package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import javax.inject.Inject

interface DefaultSettingsLiveDataProvider : LiveDataProvider<ConnectionSettingsEntity>

class DefaultSettingsLiveDataProviderImpl
@Inject
constructor() : DefaultSettingsLiveDataProvider, BaseLiveDataProvider<ConnectionSettingsEntity>()