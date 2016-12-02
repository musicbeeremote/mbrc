package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.annotations.Connection.Status

data class ConnectionStatusChangeEvent(@Status val status: Int)
