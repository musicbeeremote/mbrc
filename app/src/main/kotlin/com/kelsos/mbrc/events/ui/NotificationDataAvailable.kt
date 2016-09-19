package com.kelsos.mbrc.events.ui

import android.graphics.Bitmap
import com.kelsos.mbrc.annotations.PlayerState.State

class NotificationDataAvailable(val artist: String, val title: String, val album: String, val cover: Bitmap,
                                @State @State
                                val state: String)
