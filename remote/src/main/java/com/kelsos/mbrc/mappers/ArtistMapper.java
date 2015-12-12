package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.dto.library.ArtistDto;
import java.util.List;

public class ArtistMapper {
  public static List<ArtistDao> map(List<ArtistDto> data) {
    return Stream.of(data).map(ArtistMapper::map).collect(Collectors.toList());
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
    return Stream.of(data).map(ArtistMapper::map).collect(Collectors.toList());
  }
}
