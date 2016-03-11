package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.domain.Genre;
import com.kelsos.mbrc.dto.library.GenreDto;
import java.util.List;
import rx.Observable;

public class GenreMapper {

  public static List<GenreDao> map(List<GenreDto> data) {
    return Observable.from(data).map(GenreMapper::map).toList().toBlocking().first();
  }

  public static GenreDao map(GenreDto dto) {
    GenreDao dao = new GenreDao();
    dao.setName(dto.getName());
    dao.setId(dto.getId());
    dao.setDateDeleted(dto.getDateDeleted());
    dao.setDateUpdated(dto.getDateUpdated());
    dao.setDateAdded(dto.getDateAdded());
    return dao;
  }

  public static List<Genre> mapToModel(List<GenreDao> genres) {
    return Observable.from(genres).map(GenreMapper::mapToModel).toList().toBlocking().first();
  }

  @NonNull public static Genre mapToModel(GenreDao dao) {
    return new Genre(dao.getId(), dao.getName());
  }
}
