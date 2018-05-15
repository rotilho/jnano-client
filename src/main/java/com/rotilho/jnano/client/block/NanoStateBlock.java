package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoBlocks;
import com.sun.istack.internal.NotNull;

import java.math.BigInteger;

import lombok.Value;

@Value(staticConstructor = "of")
public class NanoStateBlock implements NanoBlock {
    @NotNull
    private final String account;
    @NotNull
    private final String previous;
    @NotNull
    private final String representative;
    @NotNull
    private final BigInteger balance;
    @NotNull
    private final String link;

    @Override
    public String getHash() {
        return NanoBlocks.hashStateBlock(account, previous, representative, balance, link);
    }
}
