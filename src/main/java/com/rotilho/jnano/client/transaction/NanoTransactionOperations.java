package com.rotilho.jnano.client.transaction;

import com.rotilho.jnano.client.JSON;
import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.NanoRequest;
import com.rotilho.jnano.client.account.NanoAccountInfo;
import com.rotilho.jnano.client.account.NanoAccountOperations;
import com.rotilho.jnano.client.amount.NanoAmount;
import com.rotilho.jnano.client.block.NanoStateBlock;
import com.rotilho.jnano.client.work.NanoWorkOperations;
import com.rotilho.jnano.commons.NanoAccounts;
import com.rotilho.jnano.commons.NanoKeys;
import com.rotilho.jnano.commons.NanoSignatures;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.util.stream.Collectors.toList;

@Builder
@RequiredArgsConstructor(staticName = "of")
public class NanoTransactionOperations {

    @NonNull
    private final NanoAPI api;
    @NonNull
    private final NanoAccountOperations accountOperations;
    @NonNull
    private final NanoWorkOperations workOperations;

    public List<NanoTransaction<NanoStateBlock>> receive(@Nonnull byte[] privateKey) {
        String account = createAccount(privateKey);
        Map<String, NanoAmount> pending = accountOperations.getPending(account);
        return pending.entrySet().stream()
                .map(entry -> receive(privateKey, account, entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    private NanoTransaction<NanoStateBlock> receive(@Nonnull byte[] privateKey, @Nonnull String account, @Nonnull String hash, @Nonnull NanoAmount amount) {
        NanoAccountInfo info = accountOperations.getInfo(account);

        NanoStateBlock block = NanoStateBlock.builder()
                .account(account)
                .previous(info.getFrontier())
                .representative(info.getRepresentative())
                .balance(info.getBalance().add(amount))
                .link(hash)
                .build();

        return process(privateKey, block);
    }

    public NanoTransaction<NanoStateBlock> send(@Nonnull byte[] privateKey, @Nonnull String previous, @Nonnull String targetAccount, @Nonnull NanoAmount amount) {
        String sourceAccount = createAccount(privateKey);
        NanoAccountInfo info = accountOperations.getInfo(sourceAccount);

        if (!info.getFrontier().equals(previous)) {
            throw new IllegalArgumentException("Previous hash (" + previous + ")  is different from account frontier (" + info.getFrontier() + ")");
        }

        NanoAmount balance = info.getBalance().subtract(amount);

        NanoStateBlock block = NanoStateBlock.builder()
                .account(sourceAccount)
                .previous(previous)
                .representative(info.getRepresentative())
                .balance(balance)
                .link(targetAccount)
                .build();
        return process(privateKey, block);
    }

    public NanoTransaction<NanoStateBlock> process(@Nonnull byte[] privateKey, @Nonnull NanoStateBlock block) {
        String signature = NanoSignatures.sign(privateKey, block.getHash());
        String work = workOperations.perform(block.getPrevious());
        NanoTransaction<NanoStateBlock> transaction = NanoTransaction.<NanoStateBlock>builder()
                .block(block)
                .signature(signature)
                .work(work)
                .build();
        NanoRequest request = NanoRequest.builder()
                .action("process")
                .param("block", JSON.stringify(transaction)) //Unfortunately the node accepts just string in this field but not nested object
                .build();
        BlockHash blockHash = api.execute(request, BlockHash.class);
        if (!block.getHash().equals(blockHash.getHash())) {
            throw new UncheckedIOException(new IOException("Block " + block.getHash() + " processing failed"));
        }
        return transaction;
    }

    private String createAccount(byte[] privateKey) {
        byte[] publicKey = NanoKeys.createPublicKey(privateKey);
        return NanoAccounts.createAccount(publicKey);
    }

    @Value
    static class BlockHash {
        private final String hash;
    }
}
