package com.kelsos.mbrc.events.ui

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState.State

data class NotificationDataAvailable(val artist: String,
                                val title: String,
                                val album: String,
                                val cover: Bitmap?,
                                @State val state: String)
