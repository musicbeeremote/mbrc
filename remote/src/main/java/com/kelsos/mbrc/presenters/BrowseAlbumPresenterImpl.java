package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.interactors.LibraryAlbumInteractor;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.ui.views.BrowseAlbumView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BrowseAlbumPresenterImpl implements BrowseAlbumPresenter {
  @Inject private QueueInteractor queueInteractor;
  @Inject private LibraryAlbumInteractor albumInteractor;
  private BrowseAlbumView view;

  @Override public void bind(BrowseAlbumView view) {
    this.view = view;
  }

  @Override public void queue(Album album, @Queue.Action String action) {
    queueInteractor.execute(MetaDataType.ALBUM, action, (int) album.getId()).subscribe(aBoolean -> {

    }, Ln::v);
  }

  @Override public void load() {
    albumInteractor.execute()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(albums -> view.update(albums), Ln::v);
  }
}
