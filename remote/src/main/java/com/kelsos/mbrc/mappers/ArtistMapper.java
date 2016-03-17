package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.views.GenreArtistView;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.dto.library.ArtistDto;
import java.util.List;
import rx.Observable;

public class ArtistMapper {
  public static List<ArtistDao> map(List<ArtistDto> data) {
    return Observable.from(data).map(ArtistMapper::map).toList().toBlocking().first();
  }

  public static ArtistDao map(ArtistDto dto) {
    ArtistDao dao = new ArtistDao();
    dao.setId(dto.getId());
    dao.setName(dto.getName());
    dao.setDateAdded(dto.getDateAdded());
    dao.setDateUpdated(dto.getDateUpdated());
    dao.setDateDeleted(dto.getDateDeleted());
    return dao;
  }

  @NonNull public static Artist map(ArtistDao dao) {
    return new Artist(dao.getId(), dao.getName());
  }

  public static List<Artist> mapData(List<ArtistDao> data) {
    return Observable.from(data).map(ArtistMapper::map).toList().toBlocking().first();
  }

  public static List<Artist> mapGenreArtists(List<GenreArtistView> genreArtistViews) {
    return Observable.from(genreArtistViews).map(ArtistMapper::mapGenreArtist).toList().toBlocking().first();
  }

  @NonNull public static Artist mapGenreArtist(GenreArtistView genreArtist) {
   return new Artist(genreArtist.getId(), genreArtist.getName());
  }
}
