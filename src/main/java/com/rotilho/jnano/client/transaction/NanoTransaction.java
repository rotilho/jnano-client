package com.rotilho.jnano.client.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.rotilho.jnano.client.block.NanoBlock;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public final class NanoTransaction<T extends NanoBlock> {
    @JsonUnwrapped
    @NonNull
    private final T block;
    @NonNull
    private final String signature;
    @NonNull
    private final String work;

    @JsonIgnore
    public String getHash() {
        return block.getHash();
    }

    @JsonIgnore
    public Class<? extends NanoBlock> getBlockType() {
        return block.getClass();
    }
}
