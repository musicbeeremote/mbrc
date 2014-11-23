package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.Logger;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.events.actions.ButtonPressedEvent.Button;

@Singleton
public class ActionController {
    @Inject
    public ActionController(RemoteApi api){

        Events.ButtonPressedNotification.subscribeOn(Schedulers.io())
                .filter(event -> event.getType().equals(Button.STOP))
                .flatMap(event -> api.playbackStop())
                .subscribe(r -> Ln.d(r.isSuccess()), Logger::ProcessThrowable);
    }
}
