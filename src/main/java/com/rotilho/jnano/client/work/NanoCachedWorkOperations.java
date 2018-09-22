package com.rotilho.jnano.client.work;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
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
}
