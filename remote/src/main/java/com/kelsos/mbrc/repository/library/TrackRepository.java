package com.kelsos.mbrc.repository.library;

import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.views.TrackModelView;
import com.kelsos.mbrc.repository.Repository;
import java.util.List;
import rx.Observable;

public interface TrackRepository extends Repository<TrackDao>{
  Observable<List<TrackModelView>> getTracksByAlbumId(long albumId);
  Observable<List<TrackModelView>> getTracks(int offset, int limit);
}
