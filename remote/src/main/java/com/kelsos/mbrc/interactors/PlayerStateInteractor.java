package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.player.PlayState;

import rx.Observable;

public interface PlayerStateInteractor {
  Observable<PlayState> execute(boolean reload);
}
