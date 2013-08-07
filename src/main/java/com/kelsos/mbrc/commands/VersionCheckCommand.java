package com.kelsos.mbrc.commands;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.squareup.otto.Bus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class VersionCheckCommand implements ICommand {
    private MainDataModel model;
    private ObjectMapper mapper;
    private Context mContext;
    private SettingsManager manager;
    private Bus bus;

    @Inject public VersionCheckCommand(MainDataModel model, ObjectMapper mapper, Context mContext,
                                       SettingsManager manager, Bus bus) {
        this.model = model;
        this.mapper = mapper;
        this.mContext = mContext;
        this.manager = manager;
        this.bus = bus;
    }

    @Override public void execute(IEvent e) {
        new Thread(new VersionChecker()).start();
    }

    private class VersionChecker implements Runnable {
        @Override public void run() {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(manager.getLastUpdated().getTime());
                calendar.add(Calendar.DATE, 2);
                Date nextCheck = new Date(calendar.getTimeInMillis());
                Date now = new Date();

                if (nextCheck.after(now)) {
                    if (BuildConfig.DEBUG) {
                        Log.d("mbrc-log", "waiting for next check: " + Long.toString(nextCheck.getTime()));
                    }
                    return;
                }

                JsonNode jsonNode = mapper.readValue(new URL("http://kelsos.net/musicbeeremote/versions.json"), JsonNode.class);
                String version = RemoteUtils.getVersion(mContext);
                JsonNode vNode = jsonNode.path("versions").path(version);

                String suggestedVersion = vNode.path("plugin").asText();

                if (!suggestedVersion.equals(model.getPluginVersion())) {
                    boolean isOutOfDate = false;

                    String[] currentVersion = model.getPluginVersion().split("\\.");
                    String[] latestVersion = suggestedVersion.split("\\.");

                    int i = 0;
                    while (i<currentVersion.length&&i<latestVersion.length&&currentVersion[i].equals(latestVersion[i])) {
                        i++;
                    }

                    if (i<currentVersion.length&&i<latestVersion.length) {
                        int diff = Integer.valueOf(currentVersion[i]).compareTo(Integer.valueOf(latestVersion[i]));
                        isOutOfDate = diff < 0;
                    }

                    if (isOutOfDate) {
                        bus.post(new MessageEvent(ProtocolEventType.InformClientPluginOutOfDate));
                    }
                }

                manager.setLastUpdated(now);
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "last check on: " + Long.toString(now.getTime()));
                    Log.d("mbrc-log", "plugin reported version: " + model.getPluginVersion());
                    Log.d("mbrc-log", "plugin suggested version: " + suggestedVersion);
                }

            } catch (MalformedURLException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "version check MalformedURLException",e);
                }
            } catch (JsonMappingException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "version check JsonMappingException",e);
                }
            } catch (JsonParseException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "version check parse",e);
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "version check IOException",e);
                }
            } catch (PackageManager.NameNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "version check PackageManager.NameNotFoundException",e);
                }
            } catch (NumberFormatException e) {
                if (BuildConfig.DEBUG) {
                    Log.d("mbrc-log", "version check NumberFormatException",e);
                }
            }
        }
    }

}
