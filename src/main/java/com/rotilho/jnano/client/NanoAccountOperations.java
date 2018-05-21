package com.rotilho.jnano.client;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class NanoAccountOperations {
    @NonNull
    private final NanoAPI api;

    public BalanceInformation getBalance(@Nonnull String account, boolean includePending) {
        BalanceRequest request = new BalanceRequest(account);
        return api.execute(request, BalanceInformation.class);
    }


    @Value
    static final class BalanceRequest implements NanoAPIAction {
        private final String account;

        @Override
        public String getAction() {
            return "account_balance";
        }
    }

    @Value
    static final class BalanceInformation {
        private final BigInteger balance;
        private final BigInteger pending;

        public BigInteger getTotal() {
            return balance.add(pending);
        }
    }
}
