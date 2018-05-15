package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoOpenBlock implements NanoBlock {
    @NonNull
    private final String source;
    @NonNull
    private final String representative;
    @NonNull
    private final String account;

    @Override
    public String getHash() {
        return NanoBlocks.hashOpenBlock(source, representative, account);
    }
}
