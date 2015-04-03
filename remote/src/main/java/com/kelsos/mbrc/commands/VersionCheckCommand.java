package com.kelsos.mbrc.commands;

import android.content.Context;
import android.content.pm.PackageManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import roboguice.util.Ln;

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

    public static final String CHECK_URL = "http://kelsos.net/musicbeeremote/versions.json";

    @Override public void run() {
      try {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(manager.getLastUpdated().getTime());
        calendar.add(Calendar.DATE, 2);
        Date nextCheck = new Date(calendar.getTimeInMillis());
        Date now = new Date();

        if (nextCheck.after(now)) {
          if (BuildConfig.DEBUG) {
            Ln.d(String.format("waiting for next check: %s", Long.toString(nextCheck.getTime())));
          }
          return;
        }

        JsonNode jsonNode = mapper.readValue(new URL(CHECK_URL), JsonNode.class);
        String version = RemoteUtils.getVersion(mContext);
        JsonNode vNode = jsonNode.path(Const.VERSIONS).path(version);

        String suggestedVersion = vNode.path(Const.PLUGIN).asText();

        if (!suggestedVersion.equals(model.getPluginVersion())) {
          boolean isOutOfDate = false;

          String[] currentVersion = model.getPluginVersion().split("\\.");
          String[] latestVersion = suggestedVersion.split("\\.");

          int i = 0;
          while (i < currentVersion.length && i < latestVersion.length && currentVersion[i].equals(
              latestVersion[i])) {
            i++;
          }

          if (i < currentVersion.length && i < latestVersion.length) {
            int diff =
                Integer.valueOf(currentVersion[i]).compareTo(Integer.valueOf(latestVersion[i]));
            isOutOfDate = diff < 0;
          }

          if (isOutOfDate) {
            bus.post(new MessageEvent(ProtocolEventType.InformClientPluginOutOfDate));
          }
        }

        manager.setLastUpdated(now);
        if (BuildConfig.DEBUG) {
          Ln.d(String.format("last check on: %s", Long.toString(now.getTime())));
          Ln.d(String.format("plugin reported version: %s", model.getPluginVersion()));
          Ln.d(String.format("plugin suggested version: %s", suggestedVersion));
        }
      } catch (MalformedURLException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "version check MalformedURLException");
        }
      } catch (JsonMappingException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "version check JsonMappingException");
        }
      } catch (JsonParseException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "version check parse");
        }
      } catch (IOException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "version check IOException");
        }
      } catch (PackageManager.NameNotFoundException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "version check PackageManager.NameNotFoundException");
        }
      } catch (NumberFormatException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "version check NumberFormatException");
        }
      }
    }
  }
}
