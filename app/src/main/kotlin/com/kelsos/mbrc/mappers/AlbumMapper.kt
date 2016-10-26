package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.AlbumDao
import com.kelsos.mbrc.data.dao.ArtistDao
import com.kelsos.mbrc.data.dao.CoverDao
import com.kelsos.mbrc.data.views.AlbumModelView
import com.kelsos.mbrc.data.views.ArtistAlbumView
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.dto.library.AlbumDto

object AlbumMapper {
  fun map(dao: AlbumModelView, year: String): Album {
    return Album(dao.id, dao.name!!, dao.artist!!, dao.cover!!, year)
  }

  fun map(daoList: List<AlbumModelView>): List<Album> {
    return daoList.map { map(it, "") }.toList()
  }

  fun mapDtos(albums: List<AlbumDto>,
              covers: List<CoverDao>,
              artists: List<ArtistDao>): List<AlbumDao> {
    return albums.map { mapDto(it, covers, artists) }
  }

  fun mapDto(album: AlbumDto, covers: List<CoverDao>, artists: List<ArtistDao>): AlbumDao {
    val dao = AlbumDao()
    dao.id = album.id
    dao.dateAdded = album.dateAdded
    dao.dateDeleted = album.dateDeleted
    dao.dateUpdated = album.dateUpdated
    dao.artist = MapperUtils.getArtistById(album.artistId, artists)
    dao.cover = MapperUtils.getCoverById(album.coverId, covers)
    dao.name = album.name
    return dao
  }

  fun mapArtistAlbums(albums: List<ArtistAlbumView>): List<Album> {
    return albums.map { mapArtistAlbum(it) }
  }

  fun mapArtistAlbum(view: ArtistAlbumView): Album {
    return Album(view.id, view.name!!, view.artist!!, view.cover!!, "")
  }
}
