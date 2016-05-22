package com.kelsos.mbrc.services;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class OnSubscribeReader extends SyncOnSubscribe<BufferedReader, String> {
  private final BufferedReader reader;

  public OnSubscribeReader(BufferedReader reader) {
    this.reader = reader;
  }

  @Override protected BufferedReader generateState() {
    return this.reader;
  }

  @Override protected BufferedReader next(BufferedReader state, Observer<? super String> observer) {
    try {
      String line = reader.readLine();
      if (TextUtils.isEmpty(line)) {
        observer.onCompleted();
      } else {
        observer.onNext(line);
      }
    } catch (IOException e) {
      observer.onError(e);
    } return state;
  }
}
