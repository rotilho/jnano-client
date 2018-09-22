package com.rotilho.jnano.client.work;

import com.rotilho.jnano.commons.NanoPOWs;

import javax.annotation.Nonnull;

public class NanoLocalWorkOperations implements NanoWorkOperations {
    @Override
    public String perform(@Nonnull String hash) {
        return NanoPOWs.perform(hash);
    }
}
