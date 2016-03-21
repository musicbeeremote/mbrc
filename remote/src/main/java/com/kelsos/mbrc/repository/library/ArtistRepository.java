package com.kelsos.mbrc.repository.library;

import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.views.GenreArtistView;
import com.kelsos.mbrc.repository.Repository;
import java.util.List;
import rx.Observable;

public interface ArtistRepository extends Repository<ArtistDao> {
  Observable<List<GenreArtistView>> getArtistsByGenreId(long id);
}
