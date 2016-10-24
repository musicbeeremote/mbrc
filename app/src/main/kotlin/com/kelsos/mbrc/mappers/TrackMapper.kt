package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.AlbumDao
import com.kelsos.mbrc.data.dao.ArtistDao
import com.kelsos.mbrc.data.dao.GenreDao
import com.kelsos.mbrc.data.dao.TrackDao
import com.kelsos.mbrc.data.views.TrackModelView
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.dto.library.TrackDto

object TrackMapper {
    fun map(view: TrackModelView): Track {
        return Track(view.id, view.artist, view.title, view.cover)
    }

    fun map(dao: List<TrackModelView>): List<Track> {
        return dao.map { map(it) }.toList()
    }

    fun mapDtos(trackDto: List<TrackDto>,
                artists: List<ArtistDao>,
                genres: List<GenreDao>,
                albums: List<AlbumDao>): List<TrackDao> {
        return trackDto.map { dto -> mapDto(dto, artists, genres, albums) }.toList()
    }

    fun mapDto(trackDto: TrackDto,
               artists: List<ArtistDao>,
               genres: List<GenreDao>,
               albums: List<AlbumDao>): TrackDao {
        val dao = TrackDao()
        dao.id = trackDto.id
        dao.path = trackDto.path
        dao.position = trackDto.position
        dao.title = trackDto.title
        dao.disc = trackDto.disc
        dao.year = trackDto.year
        dao.dateAdded = trackDto.dateAdded
        dao.dateDeleted = trackDto.dateDeleted
        dao.dateUpdated = trackDto.dateUpdated
        dao.genre = MapperUtils.getGenreById(trackDto.genreId, genres)
        dao.albumArtist = MapperUtils.getArtistById(trackDto.albumArtistId, artists)
        dao.artist = MapperUtils.getArtistById(trackDto.artistId, artists)
        dao.album = MapperUtils.getAlbumById(trackDto.albumId, albums)
        return dao
    }
}
