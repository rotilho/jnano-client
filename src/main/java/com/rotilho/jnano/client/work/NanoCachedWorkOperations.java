package com.rotilho.jnano.client.work;

import com.rotilho.jnano.commons.NanoWorks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.NonNull;
import lombok.Value;

import static java.util.Collections.unmodifiableMap;

@Value(staticConstructor = "of")
public class NanoCachedWorkOperations implements NanoWorkOperations {
    private final NanoWorkOperations operations;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public Map<String, String> getCache() {
        return unmodifiableMap(cache);
    }

    @Override
    public String perform(@NonNull String hash) {
        return cache.computeIfAbsent(hash, k -> operations.perform(hash));
    }

    public void cache(@NonNull String hash) {
        cache.put(hash, operations.perform(hash));
    }

    public void put(@NonNull String hash, @NonNull String work) {
        if (!NanoWorks.isValid(hash, work)) {
            throw new IllegalArgumentException("Work(" + work + ") for the Hash(" + hash + ") is not valid");
        }
        cache.put(hash, work);
    }

    public void remove(@NonNull String hash) {
        cache.remove(hash);
    }
}
