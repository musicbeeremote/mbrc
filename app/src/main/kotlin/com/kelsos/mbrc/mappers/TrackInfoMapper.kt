package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.dto.track.TrackInfoResponse

class TrackInfoMapper {
  companion object {
    fun map(response: TrackInfoResponse): TrackInfo {
      return TrackInfo(response.album, response.artist, response.path, response.title, response.year)
    }
  }
}
