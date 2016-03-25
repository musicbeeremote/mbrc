package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractor;
import com.kelsos.mbrc.ui.views.ArtistAlbumsView;
import timber.log.Timber;

public class ArtistAlbumPresenterImpl implements ArtistAlbumPresenter {

  private ArtistAlbumsView view;

  @Inject private ArtistAlbumInteractor artistAlbumInteractor;
  @Inject private QueueInteractor queueInteractor;

  @Override public void load(long artistId) {
    artistAlbumInteractor.getArtistAlbums(artistId).subscribe(model -> {
      view.update(model);
    }, t -> {
      view.showLoadFailed();
    });
  }

  @Override public void bind(ArtistAlbumsView view) {

    this.view = view;
  }

  @Override public void queue(@Queue.Action String action, Album album) {
    queueInteractor.execute(MetaDataType.ALBUM, action, (int) album.getId()).subscribe(success -> {
      if (success) {
        view.queueSuccess();
      }

    }, t -> {
      Timber.v(t, "failed to queue the album");
    });
  }
}
