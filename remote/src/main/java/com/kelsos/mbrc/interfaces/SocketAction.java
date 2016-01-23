package com.kelsos.mbrc.interfaces;

import rx.Subscription;

public interface SocketAction {
  Subscription call();
}
