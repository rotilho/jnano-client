package com.rotilho.jnano.client.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rotilho.jnano.commons.NanoAccountType;
import com.rotilho.jnano.commons.NanoBlocks;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(exclude = "accountType")
public class NanoChangeBlock implements NanoBlock {
    @NonNull
    @JsonIgnore
    private NanoAccountType accountType;
    @NonNull
    private final String previous;
    @NonNull
    private final String representative;

    @Override
    public String getType() {
        return "change";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashChangeBlock(accountType, previous, representative);
    }
}
