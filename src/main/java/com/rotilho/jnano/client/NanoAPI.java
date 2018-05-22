package com.rotilho.jnano.client;

import java.io.IOException;
import java.io.UncheckedIOException;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiredArgsConstructor
public class NanoAPI {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient CLIENT = new OkHttpClient();

    private final String endpoint;

    public <T> T execute(NanoAPIAction action, Class<T> clazz) {
        try {
            RequestBody body = RequestBody.create(MEDIA_TYPE, JSON.stringify(action));
            Request request = new Request.Builder().url(endpoint).post(body).build();
            Response response = CLIENT.newCall(request).execute();
            checkSuccess(action, response);
            return JSON.parse(response.body().string(), clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void checkSuccess(NanoAPIAction action, Response response) {
        if (!response.isSuccessful()) {
            throw new UncheckedIOException(new IOException("Request to " + endpoint + " failed because " + response.message() + "(" + response.code() + "). Action " + action));
        }
    }


}
