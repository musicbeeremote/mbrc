package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.views.TrackModelView;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.dto.library.TrackDto;
import java.util.List;
import rx.Observable;

public class TrackMapper {
  public static Track map(TrackModelView view) {
    return new Track(view.getId(), view.getArtist(), view.getTitle(), view.getCover());
  }

  public static List<Track> map(List<TrackModelView> dao) {
    return Observable.from(dao).map(TrackMapper::map).toList().toBlocking().first();
  }

  public static List<TrackDao> mapDtos(List<TrackDto> trackDto,
      List<ArtistDao> artists,
      List<GenreDao> genres,
      List<AlbumDao> albums) {
    return Observable.from(trackDto).map(dto -> mapDto(dto, artists, genres, albums)).toList().toBlocking().first();
  }

  public static TrackDao mapDto(TrackDto trackDto,
      List<ArtistDao> artists,
      List<GenreDao> genres,
      List<AlbumDao> albums) {
    TrackDao dao = new TrackDao();
    dao.setId(trackDto.getId());
    dao.setPath(trackDto.getPath());
    dao.setPosition(trackDto.getPosition());
    dao.setTitle(trackDto.getTitle());
    dao.setDisc(trackDto.getDisc());
    dao.setYear(trackDto.getYear());
    dao.setDateAdded(trackDto.getDateAdded());
    dao.setDateDeleted(trackDto.getDateDeleted());
    dao.setDateUpdated(trackDto.getDateUpdated());
    dao.setGenre(MapperUtils.getGenreById(trackDto.getGenreId(), genres));
    dao.setAlbumArtist(MapperUtils.getArtistById(trackDto.getAlbumArtistId(), artists));
    dao.setArtist(MapperUtils.getArtistById(trackDto.getArtistId(), artists));
    dao.setAlbum(MapperUtils.getAlbumById(trackDto.getAlbumId(), albums));
    return dao;
  }
}
