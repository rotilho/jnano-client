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

    public BigInteger getBalance(@Nonnull String account, boolean includePending) {
        BalanceRequest request = new BalanceRequest(account);
        BalanceResponse response = api.execute(request, BalanceResponse.class);
        if (includePending) {
            return response.getTotal();
        }
        return response.getBalance();
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
    static final class BalanceResponse {
        private final BigInteger balance;
        private final BigInteger pending;

        public BigInteger getTotal() {
            return balance.add(pending);
        }
    }
}
