package com.kelsos.mbrc.events.ui

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.empty

class NotificationDataAvailable(val artist: String = String.empty,
                                 val title: String = String.empty,
                                 val album: String = String.empty,
                                 val cover: Bitmap? = null,
                                 @PlayerState.State val state: String = PlayerState.UNDEFINED)
