package com.kelsos.mbrc.utilities;

import android.support.v7.widget.RecyclerView;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.events.ui.SearchScrollChanged;
import com.squareup.otto.Bus;
import rx.subjects.PublishSubject;

@Singleton public class ScrollListener extends RecyclerView.OnScrollListener {

  private PublishSubject<Boolean> scrollChange;

  @Inject public ScrollListener(Bus bus) {
    scrollChange = PublishSubject.create();
    scrollChange.distinctUntilChanged().subscribe(scrollingUpwards -> {
      bus.post(new SearchScrollChanged(scrollingUpwards));
    });
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);
    scrollChange.onNext(dy > 0);
  }
}
