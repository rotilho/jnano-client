package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;
import com.sun.istack.internal.NotNull;

import lombok.Value;

@Value(staticConstructor = "of")
public class NanoChangeBlock implements NanoBlock {
    @NotNull
    private String previous;
    @NotNull
    private String representative;

    @Override
    public String getHash() {
        return NanoBlocks.hashChangeBlock(previous, representative);
    }
}
