package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.Album;
import java.util.List;
import rx.Observable;

public interface LibraryAlbumInteractor {
  Observable<List<Album>> execute();
}
