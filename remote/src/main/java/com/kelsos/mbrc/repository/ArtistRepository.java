package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.views.GenreArtistView;
import java.util.List;
import rx.Observable;

public interface ArtistRepository extends Repository<ArtistDao>{
  Observable<List<GenreArtistView>> getArtistsByGenreId(long id);
}
