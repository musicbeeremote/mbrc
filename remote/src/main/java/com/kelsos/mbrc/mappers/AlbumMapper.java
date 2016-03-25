package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.views.AlbumModelView;
import com.kelsos.mbrc.dao.views.ArtistAlbumView;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.dto.library.AlbumDto;
import java.util.List;
import rx.Observable;

public class AlbumMapper {
  @NonNull public static Album map(AlbumModelView dao, String year) {
    return new Album(dao.getId(), dao.getName(), dao.getArtist(), dao.getCover(), year);
  }

  public static List<Album> map(List<AlbumModelView> daoList) {
    return Observable.from(daoList).map(albumModelView -> map(albumModelView, "")).toList().toBlocking().first();
  }

  public static List<AlbumDao> mapDtos(List<AlbumDto> albums, List<CoverDao> covers, List<ArtistDao> artists) {
    return Observable.from(albums).map(albumDto -> mapDto(albumDto, covers, artists)).toList().toBlocking().first();
  }

  public static AlbumDao mapDto(AlbumDto album, List<CoverDao> covers, List<ArtistDao> artists) {
    AlbumDao dao = new AlbumDao();
    dao.setId(album.getId());
    dao.setDateAdded(album.getDateAdded());
    dao.setDateDeleted(album.getDateDeleted());
    dao.setDateUpdated(album.getDateUpdated());
    dao.setArtist(MapperUtils.getArtistById(album.getArtistId(), artists));
    dao.setCover(MapperUtils.getCoverById(album.getCoverId(), covers));
    dao.setName(album.getName());
    return dao;
  }

  public static List<Album> mapArtistAlbums(List<ArtistAlbumView> albums) {
    return Observable.from(albums).map(AlbumMapper::mapArtistAlbum).toList().toBlocking().first();
  }

  public static Album mapArtistAlbum(ArtistAlbumView view) {
    return new Album(view.getId(), view.getName(), view.getArtist(), view.getCover(), "");
  }
}
