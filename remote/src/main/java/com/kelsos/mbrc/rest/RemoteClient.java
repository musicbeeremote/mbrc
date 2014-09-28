package com.kelsos.mbrc.rest;

import com.kelsos.mbrc.converter.JacksonConverter;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import org.codehaus.jackson.map.ObjectMapper;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class RemoteClient {
    private static final Logger logger = LoggerManager.getLogger();
    public static final String API_URL = "http://192.168.100.223:8189";

    public void main(){
        JacksonConverter converter = new JacksonConverter(new ObjectMapper());

        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept", "application/json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setConverter(converter)
                .setRequestInterceptor(interceptor)
                .build();

        RemoteApi remoteApi = restAdapter.create(RemoteApi.class);
    }
}
