package com.kelsos.mbrc.interceptors;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import roboguice.util.Ln;

public class LoggingInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    long t1 = System.nanoTime();
    Ln.i("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers());

    Response response = chain.proceed(request);

    long t2 = System.nanoTime();
    Ln.i("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d,
        response.headers());

    return response;
  }
}
