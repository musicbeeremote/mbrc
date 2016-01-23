package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.Repeat;

import rx.Observable;

public interface RepeatInteractor {
  Observable<String> getRepeat();
  Observable<String> setRepeat(@Repeat.Mode String mode);
}
