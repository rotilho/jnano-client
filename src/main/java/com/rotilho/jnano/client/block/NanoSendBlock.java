package com.rotilho.jnano.client.block;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
    @JsonSerialize(using = ToStringSerializer.class)
    @NonNull
    private final BigInteger balance;

    @Override
    public String getType() {
        return "send";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashSendBlock(previous, destination, balance);
    }
}
