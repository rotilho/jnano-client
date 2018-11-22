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
public class NanoStateBlock implements NanoBlock {
    @NonNull
    @JsonIgnore
    private NanoAccountType accountType;
    @NonNull
    private final String account;
    @NonNull
    private final String previous;
    @NonNull
    private final String representative;
    @NonNull
    private final NanoAmount balance;
    @NonNull
    private final String link;

    @Override
    public String getType() {
        return "state";
    }

    @Override
    public String getHash() {
        return NanoBlocks.hashStateBlock(accountType, account, previous, representative, balance, link);
    }
}
