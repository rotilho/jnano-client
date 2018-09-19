package com.rotilho.jnano.client.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.NanoRequest;
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
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(staticName = "of")
public class NanoAccountOperations {
    @NonNull
    private final NanoAPI api;

    @Nonnull
    public AccountInformation getInfo(@Nonnull String account) {
        NanoRequest action = NanoRequest.builder()
                .action("account_info")
                .param("account", account)
                .param("representative", Boolean.TRUE.toString())
                .param("weight", Boolean.TRUE.toString())
                .param("pending", Boolean.TRUE.toString())
                .build();

        return api.execute(action, AccountInformation.class);
    }

    @NonNull
    public String create(@Nonnull byte[] publicKey) {
        return NanoAccounts.createAccount(publicKey);
    }

    @NonNull
    public List<Transaction<?>> getHistory(@Nonnull String account) {
        return getHistory(account, -1);
    }

    @NonNull
    public List<Transaction<?>> getHistory(@Nonnull String account, @Nonnull Integer count) {
        NanoRequest action = NanoRequest.builder()
                .action("account_history")
                .param("account", account)
                .param("count", count.toString())
                .param("raw", Boolean.TRUE.toString())
                .build();

        AccountHistory history = api.execute(action, AccountHistory.class);
        return history.getHistory().stream().map(AccountHistoryEntry::toTransaction).collect(toList());
    }

    @NonNull
    public byte[] toPublicKey(@Nonnull String account) {
        return NanoAccounts.toPublicKey(account);
    }

    @NonNull
    public Map<String, BigInteger> getPending(@Nonnull String account) {
        return getPending(account, BigInteger.ONE);
    }

    @NonNull
    public Map<String, BigInteger> getPending(@Nonnull String account, @Nonnull BigInteger threshold) {
        return getPending(singletonList(account), threshold).getOrDefault(account, emptyMap());
    }

    @NonNull
    public Map<String, Map<String, BigInteger>> getPending(@Nonnull List<String> accounts) {
        return getPending(accounts, BigInteger.ONE);
    }

    @NonNull
    public Map<String, Map<String, BigInteger>> getPending(@Nonnull List<String> accounts, @Nonnull BigInteger threshold) {
        NanoRequest action = NanoRequest.builder()
                .action("accounts_pending")
                .param("accounts", accounts)
                .param("threshold", threshold.toString())
                .param("count", Long.MAX_VALUE + "")
                .build();

        AccountPending pending = api.execute(action, AccountPending.class);
        return pending.getBlocks();
    }

    public boolean isValid(@Nonnull String account) {
        return NanoAccounts.isValid(account);
    }

    @Value
    private static class AccountHistory {
        private final List<AccountHistoryEntry> history;
    }

    @Value
    private static class AccountHistoryEntry {
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

        private Transaction<?> toTransaction() {
            NanoBlock block = toBlock();
            return Transaction.of(block, signature, work);
        }

        private NanoBlock toBlock() {
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


    @Value
    private static class AccountPending {
        private final Map<String, Map<String, BigInteger>> blocks;
    }
}
