package com.rotilho.jnano.client.block;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rotilho.jnano.commons.NanoBlocks;

import java.math.BigInteger;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class NanoStateBlock implements NanoBlock {
    @NonNull
    private final String account;
    @NonNull
    private final String previous;
    @NonNull
    private final String representative;
    @JsonSerialize(using = ToStringSerializer.class)
    @NonNull
    private final BigInteger balance;
    @NonNull
    private final String link;

    @Override
    public String getType() {
        return "state";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashStateBlock(account, previous, representative, balance, link);
    }
}
