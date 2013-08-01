package com.kelsos.mbrc.commands;

import android.content.pm.PackageManager;
import android.util.Log;
import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.RemoteUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class VersionCheckCommand implements ICommand {
    private MainDataModel model;
    private ObjectMapper mapper;
    private RemoteUtils rUtils;

    @Inject public VersionCheckCommand(MainDataModel model, ObjectMapper mapper, RemoteUtils rUtils) {
        this.model = model;
        this.mapper = mapper;
        this.rUtils = rUtils;
    }

    @Override public void execute(IEvent e) {
        new Thread(new VersionChecker()).start();
    }

    private class VersionChecker implements Runnable {
        @Override public void run() {
            try {
                JsonNode jsonNode = mapper.readValue(new URL("http://kelsos.net/musicbeeremote/versions.json"), JsonNode.class);
                String version = rUtils.getVersion();
                JsonNode vNode = jsonNode.path(version);
                String pluginVersion = vNode.path("plugin_version").asText();
                if (!pluginVersion.equals(model.getPluginVersion())) {
                    Log.d("mbrc-log", "doesn't match");
                }

                Log.d("mbrc-log", pluginVersion);
            } catch (MalformedURLException e) {
                Log.d("mbrc-log", "url",e);
            } catch (JsonMappingException e) {
                Log.d("mbrc-log", "mapping",e);
            } catch (JsonParseException e) {
                Log.d("mbrc-log", "parse",e);
            } catch (IOException e) {
                Log.d("mbrc-log", "io",e);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("mbrc-log", "pack",e);
            }
        }
    }

}
