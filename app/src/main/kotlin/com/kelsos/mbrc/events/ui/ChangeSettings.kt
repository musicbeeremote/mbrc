package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.enums.SettingsAction

class ChangeSettings(val action: SettingsAction, val settings: ConnectionSettings)
