package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Genre;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.interactors.library.LibraryGenreInteractor;
import com.kelsos.mbrc.ui.views.BrowseGenreView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BrowseGenrePresenterImpl implements BrowseGenrePresenter {
  private BrowseGenreView view;
  @Inject private LibraryGenreInteractor interactor;
  @Inject private QueueInteractor queue;

  @Override public void bind(BrowseGenreView view) {
    this.view = view;
  }

  @Override public void load() {
    interactor.execute().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(genres -> {
      view.update(genres);
    }, Ln::v);
  }

  @Override public void queue(Genre genre, @Queue.Action String action) {
    queue.execute(MetaDataType.GENRE, action, (int) genre.getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(success -> {
          if (success) {
            view.showEnqueueSuccess();
          } else {
            view.showEnqueueFailure();
          }
        }, throwable -> {
          view.showEnqueueFailure();
        });
  }
}
