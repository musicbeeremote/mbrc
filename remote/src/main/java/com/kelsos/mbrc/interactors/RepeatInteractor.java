package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.RepeatMode;

import rx.Observable;

public interface RepeatInteractor {
  Observable<String> execute(boolean reload);
  Observable<String> execute(@RepeatMode String mode);
}
