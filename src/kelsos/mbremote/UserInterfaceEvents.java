package kelsos.mbremote;

public interface UserInterfaceEvents {
    public abstract void onActivityButtonClicked(ClickSource clickSource);
    public abstract void onSeekBarChanged(int volume);
    public abstract void onPlayNowRequest(String track);
}

