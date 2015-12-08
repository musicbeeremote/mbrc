package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dto.library.GenreDto;
import java.util.List;

public class GenreMapper {

  public static List<GenreDao> map(List<GenreDto> data) {
    return Stream.of(data).map(GenreMapper::map).collect(Collectors.toList());
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
}
