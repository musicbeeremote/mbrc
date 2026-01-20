package com.kelsos.mbrc.core.data.playlist

import androidx.paging.PagingData
import com.kelsos.mbrc.core.data.Repository
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for playlist data.
 * Interface is in core:data so it can be used by both library and content modules.
 */
interface PlaylistRepository : Repository<Playlist> {
  /**
   * Get all browser items (folders + playlists) at a specific path.
   * Returns a paged flow with folders first, then playlists.
   * @param path The folder path, empty string for root
   */
  fun getBrowserItemsAtPath(path: String): Flow<PagingData<PlaylistBrowserItem>>
}
