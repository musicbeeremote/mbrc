package com.kelsos.mbrc.core.data.library.album

/**
 * Room query projection for album cover data.
 * Used by [AlbumDao.getCovers] and [AlbumDao.updateCovers].
 */
data class AlbumCover(val artist: String?, val album: String?, val hash: String?)

data class CachedAlbumCover(val id: Long, val cover: String?)
