package com.kelsos.mbrc.utilities;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kelsos.mbrc.events.MessageEvent;

@Singleton
public class MessagePool extends ObjectPool<MessageEvent> {
    @Inject private Injector injector;

    @Override
    protected MessageEvent create() {
        return injector.getInstance(MessageEvent.class);
    }

    @Override
    public boolean validate(MessageEvent o) {
        return false;
    }

    @Override
    public void expire(MessageEvent o) {

    }
}
