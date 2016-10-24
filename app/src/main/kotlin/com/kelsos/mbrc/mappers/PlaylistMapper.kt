package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.PlaylistDao
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.dto.playlist.PlaylistDto

object PlaylistMapper {

  fun map(playlistDto: PlaylistDto): Playlist {
    val playlist = Playlist()
    playlist.id = playlistDto.id
    playlist.name = playlistDto.name
    playlist.path = playlistDto.path
    playlist.isReadOnly = playlistDto.readOnly
    playlist.tracks = playlistDto.tracks
    return playlist
  }

  fun map(collection: List<PlaylistDto>): List<Playlist> {
    return collection.map { map(it) }.toList()
  }

  fun mapData(collection: List<PlaylistDao>): List<Playlist> {
    return collection.map { mapData(it) }.toList()
  }

  private fun mapData(playlistDao: PlaylistDao): Playlist {
    val playlist = Playlist()
    playlist.tracks = playlistDao.tracks
    playlist.isReadOnly = playlistDao.readOnly
    playlist.path = playlistDao.path
    playlist.name = playlistDao.name
    playlist.id = playlistDao.id
    return playlist
  }

  private fun mapDto(playlist: PlaylistDto): PlaylistDao {
    val data = PlaylistDao()
    data.id = playlist.id
    data.name = playlist.name
    data.path = playlist.path
    data.readOnly = playlist.readOnly
    data.dateAdded = playlist.dateAdded
    data.dateDeleted = playlist.dateDeleted
    data.dateUpdated = playlist.dateUpdated
    return data
  }

  fun mapDto(objects: List<PlaylistDto>): List<PlaylistDao> {
    return objects.map { mapDto(it) }.toList()
  }

}
