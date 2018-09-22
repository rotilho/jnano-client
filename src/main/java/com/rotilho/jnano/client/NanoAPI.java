package com.rotilho.jnano.client;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NanoAPI {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final String endpoint;
    private final OkHttpClient client;
    private final Map<String, String> headers;

    @Builder
    public NanoAPI(@NonNull String endpoint, Integer connectTimeoutMillis, Integer readTimeoutMillis, @NonNull @Singular Map<String, String> headers) {
        this.endpoint = endpoint;
        this.headers = headers;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(connectTimeoutMillis != null ? connectTimeoutMillis : 10_000, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMillis != null ? readTimeoutMillis : 10_000, TimeUnit.MILLISECONDS)
                .build();
    }

    @NonNull
    public <T> T execute(@NonNull NanoRequest action, @NonNull Class<T> clazz) {
        RequestBody body = RequestBody.create(MEDIA_TYPE, JSON.stringify(action));
        Request request = new Request.Builder()
                .headers(Headers.of(headers))
                .url(endpoint)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            checkSuccess(action, response);
            String json = response.body().string();
            checkError(action, json);
            return JSON.parse(json, clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void checkSuccess(NanoRequest action, Response response) {
        if (!response.isSuccessful()) {
            throw new UncheckedIOException(new IOException("Request to " + endpoint + " failed because " + response.message() + "(" + response.code() + "). Action " + action));
        }
    }

    private void checkError(NanoRequest action, String json) throws IOException {
        RPCError error = JSON.parse(json, RPCError.class);
        if (error.getError() != null) {
            throw new UncheckedIOException(new IOException("Request to " + endpoint + " failed because '" + error.getError() + "'. Action " + action));
        }
    }

    @Value
    private static class RPCError {
        private String error;
    }


}
