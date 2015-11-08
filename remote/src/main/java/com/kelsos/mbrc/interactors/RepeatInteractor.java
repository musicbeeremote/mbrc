package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.Repeat;

import rx.Observable;

public interface RepeatInteractor {
  Observable<String> execute(boolean reload);
  Observable<String> execute(@Repeat.Mode String mode);
}
