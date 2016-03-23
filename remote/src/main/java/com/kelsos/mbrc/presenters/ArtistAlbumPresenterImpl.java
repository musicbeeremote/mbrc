package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.interactors.ArtistInteractor;
import com.kelsos.mbrc.interactors.library.ArtistAlbumInteractor;
import com.kelsos.mbrc.models.ArtistAlbumModel;
import com.kelsos.mbrc.ui.views.ArtistAlbumsView;
import java.util.List;
import rx.Observable;

public class ArtistAlbumPresenterImpl implements ArtistAlbumPresenter {

  private ArtistAlbumsView view;

  @Inject private ArtistAlbumInteractor artistAlbumInteractor;
  @Inject private ArtistInteractor artistInteractor;

  @Override public void load(long artistId) {
    Observable<List<Album>> albumObservable = artistAlbumInteractor.getArtistAlbums(artistId);
    Observable<Artist> artistObservable = artistInteractor.getArtist(artistId);

    Observable.zip(albumObservable, artistObservable, ArtistAlbumModel::new).subscribe(model -> {
      view.update(model.getAlbums());
      view.updateArtistInfo(model.getArtist());
    }, t -> {
      view.showLoadFailed();
    });

  }

  @Override public void bind(ArtistAlbumsView view) {

    this.view = view;
  }
}
