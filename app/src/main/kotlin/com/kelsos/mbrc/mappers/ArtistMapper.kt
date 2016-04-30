package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.ArtistDao
import com.kelsos.mbrc.dao.views.GenreArtistView
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.dto.library.ArtistDto
import rx.Observable
import rx.functions.Func1

object ArtistMapper {
    fun map(data: List<ArtistDto>): List<ArtistDao> {
        return Observable.from(data).map<ArtistDao>(Func1<ArtistDto, ArtistDao> { map(it) }).toList().toBlocking().first()
    }

    fun map(dto: ArtistDto): ArtistDao {
        val dao = ArtistDao()
        dao.id = dto.id
        dao.name = dto.name
        dao.dateAdded = dto.dateAdded
        dao.dateUpdated = dto.dateUpdated
        dao.dateDeleted = dto.dateDeleted
        return dao
    }

    fun map(dao: ArtistDao): Artist {
        return Artist(dao.id, dao.name)
    }

    fun mapData(data: List<ArtistDao>): List<Artist> {
        return Observable.from(data).map<Artist>(Func1<ArtistDao, Artist> { map(it) }).toList().toBlocking().first()
    }

    fun mapGenreArtists(genreArtistViews: List<GenreArtistView>): List<Artist> {
        return Observable.from(genreArtistViews).map<Artist>(Func1<GenreArtistView, Artist> { mapGenreArtist(it) }).toList().toBlocking().first()
    }

    fun mapGenreArtist(genreArtist: GenreArtistView): Artist {
        return Artist(genreArtist.id, genreArtist.name)
    }
}
