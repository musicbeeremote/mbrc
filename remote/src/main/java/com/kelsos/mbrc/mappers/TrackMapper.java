package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.views.TrackModelView;
import com.kelsos.mbrc.domain.Track;
import java.util.List;

public class TrackMapper {
  public static Track map(TrackModelView view) {
    return new Track(view.getId(), view.getArtist(), view.getTitle(), view.getCover());
  }

  public static List<Track> map(List<TrackModelView> dao) {
    return Stream.of(dao).map(TrackMapper::map).collect(Collectors.toList());
  }
}
