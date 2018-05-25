package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoReceiveBlock implements NanoBlock {
    @NonNull
    private final String previous;
    @NonNull
    private final String source;

    @Override
    public String getType() {
        return "receive";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashReceiveBlock(previous, source);
    }
}
