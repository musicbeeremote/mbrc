package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.Repeat;

import rx.Single;

/**
 * Created by kelsos on 10/14/2015.
 */
public interface RepeatInteractor {
  Single<Repeat> execute();
}
