package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.interactors.LibraryTrackInteractor;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.ui.views.BrowseTrackView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BrowseTrackPresenterImpl implements BrowseTrackPresenter {
  private BrowseTrackView view;
  @Inject private QueueInteractor queueInteractor;
  @Inject private LibraryTrackInteractor trackInteractor;

  @Override public void bind(BrowseTrackView view) {
    this.view = view;
  }

  @Override public void load() {
    trackInteractor.execute().observeOn(AndroidSchedulers.mainThread()).subscribe(tracks -> view.update(tracks), Ln::v);
  }

  @Override public void queue(Track track, @Queue.Action String action) {
    queueInteractor.execute(MetaDataType.TRACK, action, (int) track.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aBoolean -> {
        }, Ln::v);
  }
}
