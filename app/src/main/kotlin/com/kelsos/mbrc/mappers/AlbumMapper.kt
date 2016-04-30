package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.AlbumDao
import com.kelsos.mbrc.dao.ArtistDao
import com.kelsos.mbrc.dao.CoverDao
import com.kelsos.mbrc.dao.views.AlbumModelView
import com.kelsos.mbrc.dao.views.ArtistAlbumView
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.dto.library.AlbumDto
import rx.Observable
import rx.functions.Func1

object AlbumMapper {
    fun map(dao: AlbumModelView, year: String): Album {
        return Album(dao.id, dao.name, dao.artist, dao.cover, year)
    }

    fun map(daoList: List<AlbumModelView>): List<Album> {
        return Observable.from(daoList).map<Album>(Func1{
            albumModelView -> map(albumModelView, "")
        }).toList().toBlocking().first()
    }

    fun mapDtos(albums: List<AlbumDto>, covers: List<CoverDao>, artists: List<ArtistDao>): List<AlbumDao> {
        return Observable.from(albums).map<AlbumDao>(Func1{
            albumDto -> mapDto(albumDto, covers, artists)
        }).toList().toBlocking().first()
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
        return Observable.from(albums).map<Album>(Func1<ArtistAlbumView, Album> { mapArtistAlbum(it) }).toList().toBlocking().first()
    }

    fun mapArtistAlbum(view: ArtistAlbumView): Album {
        return Album(view.id, view.name, view.artist, view.cover, "")
    }
}
