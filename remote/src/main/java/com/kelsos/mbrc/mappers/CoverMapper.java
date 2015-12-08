package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dto.library.CoverDto;
import java.util.List;

public class CoverMapper {
  public static List<CoverDao> map(List<CoverDto> objects) {
    return Stream.of(objects).map(CoverMapper::map).collect(Collectors.toList());
  }

  public static CoverDao map(CoverDto object) {
    CoverDao dao = new CoverDao();
    dao.setId(object.getId());
    dao.setHash(object.getHash());
    dao.setDateUpdated(object.getDateUpdated());
    dao.setDateDeleted(object.getDateDeleted());
    dao.setDateAdded(object.getDateAdded());
    return dao;
  }
}
