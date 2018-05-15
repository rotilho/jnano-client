package com.rotilho.jnano.client.block;

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
    @NonNull
    private final BigInteger balance;
    @NonNull
    private final String link;

    @Override
    public String getHash() {
        return NanoBlocks.hashStateBlock(account, previous, representative, balance, link);
    }
}
