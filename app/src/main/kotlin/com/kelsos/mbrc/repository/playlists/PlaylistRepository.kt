package com.kelsos.mbrc.repository.playlists

import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.repository.Repository
import rx.Single

interface PlaylistRepository : Repository<Playlist> {
  fun play(path: String): Single<BaseResponse>
}
