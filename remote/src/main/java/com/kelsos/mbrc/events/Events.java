package com.kelsos.mbrc.events;

import rx.subjects.PublishSubject;

public class Events {
    public static PublishSubject <MessageEvent> Messages = PublishSubject.create();
}
