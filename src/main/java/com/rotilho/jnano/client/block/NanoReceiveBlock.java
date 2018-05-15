package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;
import com.sun.istack.internal.NotNull;

import lombok.Value;

@Value(staticConstructor = "of")
public class NanoReceiveBlock implements NanoBlock {
    @NotNull
    private String previous;
    @NotNull
    private String source;

    @Override
    public String getHash() {
        return NanoBlocks.hashReceiveBlock(previous, source);
    }
}
