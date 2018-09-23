package com.rotilho.jnano.client.block;

import com.rotilho.jnano.client.amount.NanoAmount;
import com.rotilho.jnano.commons.NanoBlocks;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class NanoSendBlock implements NanoBlock {
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
        return NanoBlocks.hashSendBlock(previous, destination, balance.toRaw().toBigInteger());
    }
}
