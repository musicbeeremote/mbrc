package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.AlbumDao
import com.kelsos.mbrc.dao.ArtistDao
import com.kelsos.mbrc.dao.CoverDao
import com.kelsos.mbrc.dao.GenreDao
import rx.Observable

object MapperUtils {

    internal fun getCoverById(id: Long, data: List<CoverDao>): CoverDao {
        return Observable.from(data).filter {
            it.id == id
        }.toBlocking().firstOrDefault(null)
    }

    internal fun getArtistById(id: Long, data: List<ArtistDao>): ArtistDao {
        return Observable.from(data).filter { it.id == id }.toBlocking().firstOrDefault(null)
    }

    internal fun getAlbumById(id: Long, data: List<AlbumDao>): AlbumDao {
        return Observable.from(data).filter { it.id == id }.toBlocking().firstOrDefault(null)
    }

    internal fun getGenreById(id: Long, data: List<GenreDao>): GenreDao {
        return Observable.from(data).filter { it.id == id }.toBlocking().firstOrDefault(null)
    }
}//no instance
