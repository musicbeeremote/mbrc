package com.kelsos.mbrc.events;

import com.kelsos.mbrc.events.ui.CoverAvailable;
import rx.subjects.PublishSubject;

public class Events {
    public static PublishSubject <MessageEvent> Messages = PublishSubject.create();
    public static PublishSubject<CoverAvailable> CoverAvailableNotification = PublishSubject.create();
}
