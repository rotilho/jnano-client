package com.rotilho.jnano.client.work;

import com.rotilho.jnano.commons.NanoPOWs;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoCachedWorkOperations implements NanoWorkOperations {
    private final NanoWorkOperations operations;
    private final Map<String, String> cache = new HashMap<>();

    @Override
    public String perform(@Nonnull String hash) {
        return cache.computeIfAbsent(hash, k -> operations.perform(hash));
    }

    public void cache(@Nonnull String hash) {
        cache.put(hash, operations.perform(hash));
    }

    public void put(@Nonnull String hash, @NonNull String work) {
        if (!NanoPOWs.isValid(hash, work)) {
            throw new IllegalArgumentException("Work(" + work + ") for the Hash(" + hash + ") is not valid");
        }
        cache.put(hash, work);
    }

    public void remove(@Nonnull String hash) {
        cache.remove(hash);
    }
}
