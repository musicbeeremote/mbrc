package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.annotations.Connection.Status

class ConnectionStatusChangeEvent(@Status val status: Long)
