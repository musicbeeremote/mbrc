package kelsos.mbremote.Messaging;

import kelsos.mbremote.Messaging.ClickSource;

public interface UserInterfaceEvent {
    public abstract void onActivityButtonClicked(ClickSource clickSource);
    public abstract void onSeekBarChanged(int volume);
    public abstract void onPlayNowRequest(String track);
}

