package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.events.UserAction

interface UserActionUseCase {
  fun perform(action: UserAction)
}