package com.kelsos.mbrc.rx;
import android.support.annotation.NonNull;

import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxUtils {
  /**
   * {@link rx.Observable.Transformer} that transforms the source observable to subscribe in the
   * io thread and observe on the Android's UI thread.
   */
  private static Observable.Transformer ioMainTransformer;
  private static Completable.CompletableTransformer uiCompletableScheduler;

  static {
    ioMainTransformer = createIoToMain();
    uiCompletableScheduler = createUiCompletableScheduler();
  }

  @NonNull
  private static Completable.CompletableTransformer createUiCompletableScheduler() {
    return tObservable -> tObservable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * Get {@link rx.Observable.Transformer} that transforms the source observable to subscribe in
   * the io thread and observe on the Android's UI thread.
   *
   * Because it doesn't interact with the emitted items it's safe ignore the unchecked casts.
   *
   * @return {@link rx.Observable.Transformer}
   */
  @NonNull
  @SuppressWarnings("unchecked")
  private static <T> Observable.Transformer<T, T> createIoToMain() {
    return tObservable -> tObservable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @SuppressWarnings("unchecked")
  public static <T> Observable.Transformer<T, T> ioToMain() {
    return ioMainTransformer;
  }

  public static Completable.CompletableTransformer uiTask() {
    return uiCompletableScheduler;
  }
}
