package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoReceiveBlock implements NanoBlock {
    @NonNull
    private String previous;
    @NonNull
    private String source;

    @Override
    public String getHash() {
        return NanoBlocks.hashReceiveBlock(previous, source);
    }
}
