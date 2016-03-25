package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.interactors.library.GenreArtistInteractor;
import com.kelsos.mbrc.ui.views.GenreArtistView;
import timber.log.Timber;

public class GenreArtistsPresenterImpl implements GenreArtistsPresenter {
  @Inject private GenreArtistInteractor genreArtistInteractor;
  @Inject private QueueInteractor interactor;

  private GenreArtistView view;

  @Override public void bind(GenreArtistView view) {
    this.view = view;
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {

  }

  @Override public void load(long genreId) {
    genreArtistInteractor.getGenreArtists(genreId).subscribe(artists -> {
      view.update(artists);
    }, t -> {
      Timber.v(t, "Failed");
    });
  }

  @Override public void queue(@Queue.Action String action, Artist artist) {
    interactor.execute(MetaDataType.ARTIST, action, (int) artist.getId()).subscribe(isSuccessful -> {
      if (isSuccessful) {
        view.onQueueSuccess();
      } else {
        view.onQueueFailure();
      }
    }, throwable -> {
      Timber.e(throwable, "Queueing failed");
      view.onQueueFailure();
    });
  }
}
