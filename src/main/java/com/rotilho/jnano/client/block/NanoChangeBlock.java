package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoChangeBlock implements NanoBlock {
    @NonNull
    private String previous;
    @NonNull
    private String representative;

    @Override
    public String getHash() {
        return NanoBlocks.hashChangeBlock(previous, representative);
    }
}
