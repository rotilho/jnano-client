package com.rotilho.jnano.client.account;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.NanoAPIException;
import com.rotilho.jnano.client.NanoRequest;
import com.rotilho.jnano.client.block.NanoBlock;
import com.rotilho.jnano.client.block.NanoChangeBlock;
import com.rotilho.jnano.client.block.NanoOpenBlock;
import com.rotilho.jnano.client.block.NanoReceiveBlock;
import com.rotilho.jnano.client.block.NanoSendBlock;
import com.rotilho.jnano.client.block.NanoStateBlock;
import com.rotilho.jnano.client.transaction.NanoTransaction;
import com.rotilho.jnano.commons.NanoAccounts;
import com.rotilho.jnano.commons.NanoAmount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(staticName = "of")
public class NanoAccountOperations {
    @NonNull
    private final NanoAPI api;

    @NonNull
    public Optional<NanoAccountInfo> getInfo(@NonNull String account) {
        try {
            return Optional.of(getInfoOrFail(account));
        } catch (NanoAPIException e) {
            if ("Account not found".equals(e.getError())) {
                return Optional.empty();
            }
            throw e;
        }
    }

    @NonNull
    public NanoAccountInfo getInfoOrFail(@NonNull String account) {
        NanoRequest action = NanoRequest.builder()
                .action("account_info")
                .param("account", account)
                .param("representative", Boolean.TRUE.toString())
                .param("weight", Boolean.TRUE.toString())
                .param("pending", Boolean.TRUE.toString())
                .build();
        return api.execute(action, NanoAccountInfo.class);
    }

    @NonNull
    public String create(@NonNull byte[] publicKey) {
        return NanoAccounts.createAccount(publicKey);
    }

    @NonNull
    public List<NanoTransaction<?>> getHistory(@NonNull String account) {
        return getHistory(account, -1);
    }

    @NonNull
    public List<NanoTransaction<?>> getHistory(@NonNull String account, @NonNull Integer count) {
        NanoRequest action = NanoRequest.builder()
                .action("account_history")
                .param("account", account)
                .param("count", count.toString())
                .param("raw", Boolean.TRUE.toString())
                .build();

        AccountHistory history = api.execute(action, AccountHistory.class);
        return history.getHistory().stream()
                .map(a -> a.toTransaction(account))
                .collect(toList());
    }

    @NonNull
    public byte[] toPublicKey(@NonNull String account) {
        return NanoAccounts.toPublicKey(account);
    }

    @NonNull
    public Map<String, NanoAmount> getPending(@NonNull String account) {
        return getPending(account, NanoAmount.ofRaw(BigDecimal.ONE));
    }

    @NonNull
    public Map<String, NanoAmount> getPending(@NonNull String account, @NonNull NanoAmount threshold) {
        Map<String, Map<String, NanoAmount>> pending = getPending(singletonList(account), threshold);
        pending.values().remove(null);
        String anotherPrefix = account.startsWith("nano_") ? account.replaceFirst("nano_", "xrb_") : account.replaceFirst("xrb_", "nano_");
        if (pending.containsKey(anotherPrefix)) {
            return pending.get(anotherPrefix);
        }
        return pending.getOrDefault(account, emptyMap());
    }

    @NonNull
    public Map<String, Map<String, NanoAmount>> getPending(@NonNull List<String> accounts) {
        return getPending(accounts, NanoAmount.ofRaw(BigDecimal.ONE));
    }

    @NonNull
    public Map<String, Map<String, NanoAmount>> getPending(@NonNull List<String> accounts, @NonNull NanoAmount threshold) {
        NanoRequest action = NanoRequest.builder()
                .action("accounts_pending")
                .param("accounts", accounts)
                .param("threshold", threshold.toString())
                .param("count", Long.MAX_VALUE + "")
                .build();

        AccountPending pending = api.execute(action, AccountPending.class);
        return pending.getBlocks();
    }

    public boolean isValid(@NonNull String account) {
        return NanoAccounts.isValid(account);
    }

    @Value
    private static class AccountHistory {
        private final List<AccountHistoryEntry> history;

        public List<AccountHistoryEntry> getHistory() {
            return history != null ? history : emptyList();
        }
    }

    @Value
    private static class AccountHistoryEntry {
        private final String type;
        private final String previous;
        private final String representative;
        private final String source;
        private final String destination;
        private final String link;
        @JsonSerialize(using = ToStringSerializer.class)
        private final NanoAmount balance;
        private final String signature;
        private final String work;

        /**
         * The account field returned by the node is the sender account not the requested account
         */
        private NanoTransaction<?> toTransaction(String account) {
            NanoBlock block = toBlock(account);
            return NanoTransaction.builder()
                    .block(block)
                    .signature(signature)
                    .work(work)
                    .build();
        }

        private NanoBlock toBlock(String account) {
            switch (type) {
                case "open":
                    return NanoOpenBlock.builder()
                            .source(source)
                            .representative(representative)
                            .account(account)
                            .build();
                case "receive":
                    return NanoReceiveBlock.builder()
                            .previous(previous)
                            .source(source)
                            .build();
                case "send":
                    return NanoSendBlock.builder()
                            .previous(previous)
                            .destination(destination)
                            .balance(balance)
                            .build();
                case "change":
                    return NanoChangeBlock.builder()
                            .previous(previous)
                            .representative(representative)
                            .build();
                default:
                    return NanoStateBlock.builder()
                            .account(account)
                            .previous(previous)
                            .representative(representative)
                            .balance(balance)
                            .link(link)
                            .build();
            }
        }
    }


    @Value
    private static class AccountPending {
        private final Map<String, Map<String, NanoAmount>> blocks;
    }
}
