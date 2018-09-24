package com.rotilho.jnano.client.work;

import com.rotilho.jnano.commons.NanoWorks;

import lombok.NonNull;


public class NanoLocalWorkOperations implements NanoWorkOperations {
    @Override
    public String perform(@NonNull String hash) {
        return NanoWorks.perform(hash);
    }
}
