package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.annotations.PlayerState

class PlayStateChange(@PlayerState.State val state: String)
