package com.rotilho.jnano.client.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rotilho.jnano.commons.NanoAccountType;
import com.rotilho.jnano.commons.NanoAmount;
import com.rotilho.jnano.commons.NanoBlocks;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(exclude = "accountType")
public class NanoSendBlock implements NanoBlock {
    @NonNull
    @JsonIgnore
    private NanoAccountType accountType;
    @NonNull
    private final String previous;
    @NonNull
    private final String destination;
    @NonNull
    private final NanoAmount balance;

    @Override
    public String getType() {
        return "send";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashSendBlock(accountType, previous, destination, balance);
    }
}
