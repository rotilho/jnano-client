package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;

import java.math.BigInteger;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoSendBlock implements NanoBlock {
    @NonNull
    private final String previous;
    @NonNull
    private final String destination;
    @NonNull
    private final BigInteger balance;

    @Override
    public String getHash() {
        return NanoBlocks.hashSendBlock(previous, destination, balance);
    }
}
