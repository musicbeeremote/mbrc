package com.kelsos.mbrc.cache

import android.graphics.Bitmap
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.domain.TrackPosition
import com.kelsos.mbrc.domain.TrackRating

interface TrackCache {
  var trackinfo: TrackInfo

  var lyrics: List<String>

  var cover: Bitmap?

  var position: TrackPosition

  var rating: TrackRating
}
