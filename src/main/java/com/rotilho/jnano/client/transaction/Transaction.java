package com.rotilho.jnano.client.transaction;

import com.rotilho.jnano.client.block.NanoBlock;

import lombok.NonNull;
import lombok.Value;

@Value
public final class Transaction {
    @NonNull
    private final NanoBlock block;
    @NonNull
    private final String signature;
    @NonNull
    private final String work;

    public String getHash() {
        return block.getHash();
    }
}
