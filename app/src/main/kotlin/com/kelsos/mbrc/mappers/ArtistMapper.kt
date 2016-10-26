package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.ArtistDao
import com.kelsos.mbrc.data.views.GenreArtistView
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.dto.library.ArtistDto

object ArtistMapper {
  fun map(data: List<ArtistDto>): List<ArtistDao> {
    return data.map { map(it) }
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
    return data.map { map(it) }
  }

  fun mapGenreArtists(genreArtistViews: List<GenreArtistView>): List<Artist> {
    return genreArtistViews.map { mapGenreArtist(it) }
  }

  fun mapGenreArtist(genreArtist: GenreArtistView): Artist {
    return Artist(genreArtist.id, genreArtist.name!!)
  }
}
