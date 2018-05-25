package com.rotilho.jnano.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rotilho.jnano.client.block.NanoBlock;
import com.rotilho.jnano.client.block.NanoChangeBlock;
import com.rotilho.jnano.client.block.NanoOpenBlock;
import com.rotilho.jnano.client.block.NanoReceiveBlock;
import com.rotilho.jnano.client.block.NanoSendBlock;
import com.rotilho.jnano.client.block.NanoStateBlock;
import com.rotilho.jnano.client.transaction.Transaction;
import com.rotilho.jnano.commons.NanoAccounts;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class NanoAccountOperations {
    @NonNull
    private final NanoAPI api;

    @Nonnull
    public AccountInformation getInfo(@Nonnull String account) {
        AccountInformationAction request = new AccountInformationAction(account);
        return api.execute(request, AccountInformation.class);
    }

    @NonNull
    public String create(@Nonnull byte[] publicKey) {
        return NanoAccounts.createAccount(publicKey);
    }

    public List<Transaction<?>> getHistory(@Nonnull String account) {
        return getHistory(account, -1);
    }

    public List<Transaction<?>> getHistory(@Nonnull String account, @Nonnull Integer count) {
        AccountHistoryAction request = new AccountHistoryAction(account, count);
        AccountHistory history = api.execute(request, AccountHistory.class);
        return history.getHistory().stream().map(AccountHistoryEntry::toTransaction).collect(toList());
    }


    @Value
    static class AccountInformationAction implements NanoAPIAction {
        private final String account;

        public String getAction() {
            return "account_info";
        }

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
    static final class AccountInformation {
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

    @Value
    static class AccountHistoryAction implements NanoAPIAction {
        private final String account;
        @JsonSerialize(using = ToStringSerializer.class)
        private final Integer count;

        public String getAction() {
            return "account_history";
        }

        public String getRaw() {
            return Boolean.TRUE.toString();
        }
    }

    @Value
    static class AccountHistory {
        private final List<AccountHistoryEntry> history;
    }

    @Value
    static class AccountHistoryEntry {
        private final String type;
        private final String previous;
        private final String representative;
        private final String source;
        private final String account;
        private final String destination;
        private final String link;
        @JsonSerialize(using = ToStringSerializer.class)
        private final BigInteger balance;
        private final String signature;
        private final String work;

        Transaction<?> toTransaction() {
            NanoBlock block = toBlock();
            return Transaction.of(block, signature, work);
        }

        NanoBlock toBlock() {
            switch (type) {
                case "open":
                    return NanoOpenBlock.of(source, representative, account);
                case "receive":
                    return NanoReceiveBlock.of(previous, source);
                case "send":
                    return NanoSendBlock.of(previous, destination, balance);
                case "change":
                    return NanoChangeBlock.of(previous, representative);
                default:
                    return NanoStateBlock.of(account, previous, representative, balance, link);
            }
        }


    }


}
