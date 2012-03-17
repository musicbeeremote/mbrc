package kelsos.mbremote;

public class Communicator {
    private static Communicator ourInstance = new Communicator();

    private UserInterfaceEvents userInterfaceEvents;
    public static Communicator getInstance() {
        return ourInstance;
    }

    private Communicator() {
        userInterfaceEvents=null;
    }
    
    public void setUserInterfaceEventsListener(UserInterfaceEvents listener)
    {
        userInterfaceEvents=listener;
    }
    
    public void onActivityButtonClicked(ClickSource clickSource){
        if(userInterfaceEvents!=null)
            userInterfaceEvents.onActivityButtonClicked(clickSource);
    }

    public void onSeekBarChanged(int volume)
    {
        if(userInterfaceEvents!=null)
            userInterfaceEvents.onSeekBarChanged(volume);
    }
    
    public void onPlayNowRequest(String track)
    {
        if(userInterfaceEvents!=null)
            userInterfaceEvents.onPlayNowRequest(track);
    }
}
