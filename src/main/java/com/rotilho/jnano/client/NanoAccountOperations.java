package com.rotilho.jnano.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rotilho.jnano.commons.NanoAccounts;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class NanoAccountOperations {
    @NonNull
    private final NanoAPI api;

    @Nonnull
    public AccountInformation getInfo(@Nonnull String account) {
        AccountAction request = new AccountAction("account_info", account);
        return api.execute(request, AccountInformation.class);
    }

    public String create(@Nonnull byte[] publicKey) {
        return NanoAccounts.createAccount(publicKey);
    }

    @Value
    static final class AccountAction implements NanoAPIAction {
        private final String action;
        private final String account;

        public String getRepresentative() {
            return Boolean.TRUE.toString();
        }

        public String getWeight() {
            return Boolean.TRUE.toString();
        }

        public String getPending() {
            return Boolean.TRUE.toString();
        }
    }

    @Value
    public static final class AccountBalance {
        @JsonSerialize(using = ToStringSerializer.class)
        private final BigInteger balance;
        @JsonSerialize(using = ToStringSerializer.class)
        private final BigInteger pending;

        public BigInteger getTotal() {
            return balance.add(pending);
        }
    }

    @Value
    public static final class AccountInformation {
        private final String frontier;
        @JsonProperty("open_block")
        private final String openBlock;
        @JsonProperty("representative_block")
        private final String representativeBlock;
        @JsonSerialize(using = ToStringSerializer.class)
        private final BigInteger balance;
        @JsonSerialize(using = ToStringSerializer.class)
        @JsonProperty("modified_timestamp")
        private final Long modifiedTimestamp;
        @JsonSerialize(using = ToStringSerializer.class)
        @JsonProperty("block_count")
        private final Long blockCount;
        private final String representative;
        @JsonSerialize(using = ToStringSerializer.class)
        private final BigInteger weight;
        @JsonSerialize(using = ToStringSerializer.class)
        private final BigInteger pending;
    }

}
