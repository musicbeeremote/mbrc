package com.kelsos.mbrc.cache

import android.graphics.Bitmap
import com.google.inject.Singleton
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.domain.TrackRating
import java.util.*

@Singleton class TrackCacheImpl : TrackCache {
    override var trackinfo: TrackInfo = TrackInfo()
    override var lyrics: List<String> = ArrayList()
        get() = lyrics
    override var cover: Bitmap? = null
    override var position: TrackPosition = TrackPosition()
    override var rating: TrackRating = TrackRating()
}
