package com.rotilho.jnano.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

import lombok.NonNull;

public final class JSON {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JSON() {
    }

    @NonNull
    public static <T> T parse(@NonNull String json, @NonNull Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    @NonNull
    public static <T> String stringify(@NonNull T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

}
