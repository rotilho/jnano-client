package com.rotilho.jnano.client.transaction;

import com.rotilho.jnano.client.JSON;
import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.NanoRequest;
import com.rotilho.jnano.client.account.NanoAccountInfo;
import com.rotilho.jnano.client.account.NanoAccountOperations;
import com.rotilho.jnano.client.block.NanoStateBlock;
import com.rotilho.jnano.client.work.NanoWorkOperations;
import com.rotilho.jnano.commons.NanoAccountType;
import com.rotilho.jnano.commons.NanoAccounts;
import com.rotilho.jnano.commons.NanoAmount;
import com.rotilho.jnano.commons.NanoHelper;
import com.rotilho.jnano.commons.NanoKeys;
import com.rotilho.jnano.commons.NanoSignatures;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import static java.util.stream.Collectors.toList;

@Builder
@RequiredArgsConstructor(staticName = "of")
public class NanoTransactionOperations {
    @NonNull
    private final NanoAccountType accountType;
    @NonNull
    private final NanoAPI api;
    @NonNull
    private final NanoAccountOperations accountOperations;
    @NonNull
    private final NanoWorkOperations workOperations;

    public Optional<NanoTransaction<NanoStateBlock>> open(@NonNull byte[] privateKey, @NonNull String representative) {
        byte[] publicKey = NanoKeys.createPublicKey(privateKey);
        String account = NanoAccounts.createAccount(publicKey);

        Set<Map.Entry<String, NanoAmount>> pending = accountOperations.getPending(account).entrySet();
        if (pending.isEmpty()) {
            return Optional.empty();
        }

        String work = workOperations.perform(NanoHelper.toHex(publicKey));

        Map.Entry<String, NanoAmount> firstPending = pending.iterator().next();
        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(accountType)
                .account(account)
                .previous("0000000000000000000000000000000000000000000000000000000000000000")
                .representative(representative)
                .balance(firstPending.getValue())
                .link(firstPending.getKey())
                .build();
        return Optional.of(process(privateKey, block, work));
    }

    public List<NanoTransaction<NanoStateBlock>> receive(@NonNull byte[] privateKey) {
        return receive(privateKey, NanoAmount.ofRaw(BigDecimal.ONE));
    }

    public List<NanoTransaction<NanoStateBlock>> receive(@NonNull byte[] privateKey, NanoAmount threshold) {
        String account = createAccount(privateKey);
        Map<String, NanoAmount> pending = accountOperations.getPending(account, threshold);
        return pending.entrySet().stream()
                .map(entry -> receive(privateKey, account, entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    private NanoTransaction<NanoStateBlock> receive(@NonNull byte[] privateKey, @NonNull String account, @NonNull String hash, @NonNull NanoAmount amount) {
        NanoAccountInfo info = accountOperations.getInfoOrFail(account);

        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(accountType)
                .account(account)
                .previous(info.getFrontier())
                .representative(info.getRepresentative())
                .balance(info.getBalance().add(amount))
                .link(hash)
                .build();

        return process(privateKey, block);
    }

    public NanoTransaction<NanoStateBlock> send(@NonNull byte[] privateKey, @NonNull String previous, @NonNull String targetAccount, @NonNull NanoAmount amount) {
        String sourceAccount = createAccount(privateKey);
        NanoAccountInfo info = accountOperations.getInfoOrFail(sourceAccount);

        if (!info.getFrontier().equals(previous)) {
            throw new IllegalArgumentException("Previous hash (" + previous + ")  is different from account frontier (" + info.getFrontier() + ")");
        }

        NanoAmount balance = info.getBalance().subtract(amount);

        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(accountType)
                .account(sourceAccount)
                .previous(previous)
                .representative(info.getRepresentative())
                .balance(balance)
                .link(targetAccount)
                .build();
        return process(privateKey, block);
    }

    public NanoTransaction<NanoStateBlock> change(@NonNull byte[] privateKey, @NonNull String representative) {
        String account = createAccount(privateKey);
        NanoAccountInfo info = accountOperations.getInfoOrFail(account);

        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(accountType)
                .account(account)
                .previous(info.getFrontier())
                .representative(representative)
                .balance(info.getBalance())
                .link("0000000000000000000000000000000000000000000000000000000000000000")
                .build();
        return process(privateKey, block);
    }

    public NanoTransaction<NanoStateBlock> process(@NonNull byte[] privateKey, @NonNull NanoStateBlock block) {
        String work = workOperations.perform(block.getPrevious());
        return process(privateKey, block, work);
    }

    public NanoTransaction<NanoStateBlock> process(@NonNull byte[] privateKey, @NonNull NanoStateBlock block, @NonNull String work) {
        String signature = NanoSignatures.sign(privateKey, block.getHash());
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
            throw new UncheckedIOException(new IOException(transaction + " processing failed"));
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
