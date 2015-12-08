package com.kelsos.mbrc.utilities;

import android.content.Context;
import com.annimon.stream.Stream;
import com.google.inject.Inject;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;
import okio.BufferedSink;
import okio.Okio;
import roboguice.util.Ln;

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
    Ln.v("[Cover] directory created [%s] [%s]", coverDirectory.getAbsolutePath(), mkdirs);

    final ConnectionSettings settings = manager.getDefault();
    baseUrl = new HttpUrl.Builder().host(settings.getAddress())
        .scheme("http")
        .port(settings.getHttp())
        .addPathSegment("library")
        .addPathSegment("covers")
        .build();

    Stream.of(covers).forEach(value -> {
      File file = new File(coverDirectory, value.getHash());
      if (file.exists()) {
        return;
      }

      try {

        download(value, file);
      } catch (IOException e) {
        e.printStackTrace();
      }
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
      Ln.v("[Cover] downloaded [%s]", file.getAbsolutePath());
    }
  }
}
