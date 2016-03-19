package com.kelsos.mbrc.utilities;

import android.content.Context;
import com.google.inject.Inject;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.domain.DeviceSettings;
import java.io.File;
import java.io.IOException;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import timber.log.Timber;

public class CoverDownloader {
  @Inject private OkHttpClient client;
  @Inject private SettingsManager manager;
  @Inject private Context context;

  private HttpUrl baseUrl;
  private File coverDirectory;

  public void download(List<CoverDao> covers) {
    File filesDir = context.getFilesDir();
    coverDirectory = new File(filesDir, "covers");
    final boolean mkdirs = coverDirectory.mkdirs();
    Timber.v("[Cover] directory created [%s] [%s]", coverDirectory.getAbsolutePath(), mkdirs);

    final DeviceSettings settings = manager.getDefault().toBlocking().first();
    baseUrl = new HttpUrl.Builder().host(settings.getAddress())
        .scheme("http")
        .port(settings.getHttp())
        .addPathSegment("library")
        .addPathSegment("covers")
        .build();

    rx.Observable.from(covers).window(5).
        subscribe(window -> {
          window.subscribe(cover -> {
            File file = new File(coverDirectory, cover.getHash());
            if (file.exists()) {
              return;
            }

            try {
              download(cover, file);
            } catch (IOException e) {
              Timber.e(e, "On file download");
            }
          });
        });
  }

  private void download(CoverDao cover, File file) throws IOException {
    final HttpUrl httpUrl = baseUrl.newBuilder()
        .addPathSegment(String.valueOf(cover.getId()))
        .addPathSegment("raw")
        .build();

    Request request = new Request.Builder().url(httpUrl).get().build();

    final Response response = client.newCall(request).execute();

    if (response.isSuccessful()) {
      BufferedSink sink = Okio.buffer(Okio.sink(file));
      sink.writeAll(response.body().source());
      sink.close();
      Timber.v("[Cover] downloaded [%s]", file.getAbsolutePath());
    }
  }
}
