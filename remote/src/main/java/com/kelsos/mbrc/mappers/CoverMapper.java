package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dto.library.CoverDto;
import java.util.List;
import rx.Observable;

public class CoverMapper {
  public static List<CoverDao> map(List<CoverDto> objects) {
    return Observable.from(objects).map(CoverMapper::map).toList().toBlocking().first();
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
