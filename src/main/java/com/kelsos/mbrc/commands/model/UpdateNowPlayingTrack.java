package com.kelsos.mbrc.commands.model;

import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

import java.util.LinkedHashMap;

public class UpdateNowPlayingTrack implements ICommand {
    @Inject
    MainDataModel model;
    @Override
    public void execute(IEvent e) {
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>)e.getData();
        model.setTrackInfo(map.get("Artist"),map.get("Title"),map.get("Album"),map.get("Year"));
        Log.d("Data","model:" + model.getAlbum());
    }
}
