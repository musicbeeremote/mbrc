package com.kelsos.mbrc.providers;

import com.google.inject.Provider;
import org.codehaus.jackson.map.ObjectMapper;

public class ObjectMapperProvider implements Provider<ObjectMapper> {
    @Override
    public ObjectMapper get() {
        return new ObjectMapper();
    }
}
