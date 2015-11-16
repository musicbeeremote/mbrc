package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.dto.NowPlayingTrack;

import java.util.List;

public class QueueTrackMapper {
  private QueueTrackMapper() {
    //no instance
  }
  public static QueueTrack map(final NowPlayingTrack track) {
    QueueTrack queueTrack = new QueueTrack();
    queueTrack.setPosition(track.getPosition());
    queueTrack.setPath(track.getPath());
    queueTrack.setArtist(track.getArtist());
    queueTrack.setTitle(track.getTitle());
    return queueTrack;
  }

  public static List<QueueTrack> map(List<NowPlayingTrack> list) {
    return Stream.of(list).map(QueueTrackMapper::map).collect(Collectors.toList());
  }
}
