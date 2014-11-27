package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.kelsos.mbrc.util.Logger;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.events.actions.ButtonPressedEvent.Button;

@Singleton
public class ActionController {
    @Inject
    public ActionController(RemoteApi api){
        SubscribeToButtonEvent(Button.PREVIOUS, api.playPrevious());
        SubscribeToButtonEvent(Button.NEXT, api.playNext());
        SubscribeToButtonEvent(Button.STOP, api.playbackStop());
        SubscribeToButtonEvent(Button.PLAYPAUSE, api.playPause());
    }

    private void SubscribeToButtonEvent(Button button, Observable<SuccessResponse> apiRequest){
        Events.ButtonPressedNotification.subscribeOn(Schedulers.io())
                .filter(event -> event.getType().equals(button))
                .flatMap(event -> apiRequest)
                .subscribe(r -> Ln.d(r.isSuccess()), Logger::ProcessThrowable);
    }
}
