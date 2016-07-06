package com.kelsos.mbrc.commands;

import android.content.Context;
import android.content.pm.PackageManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.kelsos.mbrc.utilities.SettingsManager;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import timber.log.Timber;

public class VersionCheckCommand implements ICommand {
  private static final String CHECK_URL = "http://kelsos.net/musicbeeremote/versions.json";

  private MainDataModel model;
  private ObjectMapper mapper;

  private Context context;
  private SettingsManager manager;
  private RxBus bus;

  @Inject public VersionCheckCommand(MainDataModel model, ObjectMapper mapper,
      Provider<Context> context, SettingsManager manager, RxBus bus) {
    this.model = model;
    this.mapper = mapper;
    this.context = context.get();
    this.manager = manager;
    this.bus = bus;
  }

  @Override public void execute(IEvent e) {

    if (!manager.isPluginUpdateCheckEnabled()) {
      return;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(manager.getLastUpdated().getTime());
    calendar.add(Calendar.DATE, 2);
    Date nextCheck = new Date(calendar.getTimeInMillis());
    Date now = new Date();

    if (nextCheck.after(now)) {
      Timber.d("waiting for next check: %s", Long.toString(nextCheck.getTime()));
      return;
    }

    JsonNode jsonNode;
    try {
      jsonNode = mapper.readValue(new URL(CHECK_URL), JsonNode.class);
    } catch (IOException e1) {
      Timber.d(e1, "While reading json node");
      return;
    }
    String version = null;
    try {
      version = RemoteUtils.getVersion(context);
    } catch (PackageManager.NameNotFoundException e1) {
      Timber.d(e1, "While reading the current version");
    }
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
    Timber.d("last check on: %s", Long.toString(now.getTime()));
    Timber.d("plugin reported version: %s", model.getPluginVersion());
    Timber.d("plugin suggested version: %s", suggestedVersion);
  }
}
