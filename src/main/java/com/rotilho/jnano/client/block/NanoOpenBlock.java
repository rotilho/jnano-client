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
public class NanoOpenBlock implements NanoBlock {
    @NonNull
    @JsonIgnore
    private NanoAccountType accountType;
    @NonNull
    private final String source;
    @NonNull
    private final String representative;
    @NonNull
    private final String account;

    @Override
    public String getType() {
        return "open";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashOpenBlock(accountType, source, representative, account);
    }
}
