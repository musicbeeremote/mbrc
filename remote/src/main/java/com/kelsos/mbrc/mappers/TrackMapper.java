package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.dao.views.TrackModelView;
import com.kelsos.mbrc.domain.Track;
import java.util.List;
import rx.Observable;

public class TrackMapper {
  public static Track map(TrackModelView view) {
    return new Track(view.getId(), view.getArtist(), view.getTitle(), view.getCover());
  }

  public static List<Track> map(List<TrackModelView> dao) {
    return Observable.from(dao).map(TrackMapper::map).toList().toBlocking().first();
  }
}
