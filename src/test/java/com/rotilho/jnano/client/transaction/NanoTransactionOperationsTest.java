package com.rotilho.jnano.client.transaction;

import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.account.NanoAccountInfo;
import com.rotilho.jnano.client.account.NanoAccountOperations;
import com.rotilho.jnano.client.block.NanoStateBlock;
import com.rotilho.jnano.client.transaction.NanoTransactionOperations.BlockHash;
import com.rotilho.jnano.client.work.NanoWorkOperations;
import com.rotilho.jnano.commons.NanoAccounts;
import com.rotilho.jnano.commons.NanoKeys;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.rotilho.jnano.commons.NanoHelper.toByteArray;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class NanoTransactionOperationsTest {
    private final static byte[] PRIVATE_KEY = toByteArray("9F0E444C69F77A49BD0BE89DB92C38FE713E0963165CCA12FAF5712D7657120F");
    private final static byte[] PUBLIC_KEY = NanoKeys.createPublicKey(PRIVATE_KEY);
    private final static String ACCOUNT = NanoAccounts.createAccount(PUBLIC_KEY);
    private final static String TARGET_ACCOUNT = "xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3";
    private final static String TARGET_HASH = "991CF190094C00F0B68E2E5F75F6BEE95A2E0BD93CEAA4A6734DB9F19B728948";
    private final static BigInteger BALANCE = new BigInteger("100");
    private final static String REPRESENTATIVE = "xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3";
    private final static String FRONTIER = "FF84533A571D953A596EA401FD41743AC85D04F406E76FDE4408EAED50B473C5";

    @Mock
    private NanoAPI api;
    @Mock
    private NanoAccountOperations accountOperations;
    @Mock
    private NanoWorkOperations workOperations;

    @InjectMocks
    private NanoTransactionOperations operations;

    @Test
    public void shouldSend() {
        // given
        BigInteger amount = new BigInteger("70");

        mockAccount();
        mockWork();

        NanoTransaction<NanoStateBlock> expectedTransaction = createSendTransaction(amount);

        given(api.execute(any(), eq(BlockHash.class))).willReturn(new BlockHash(expectedTransaction.getHash()));

        // when
        NanoTransaction<NanoStateBlock> transaction = operations.send(
                PRIVATE_KEY,
                FRONTIER,
                TARGET_ACCOUNT,
                amount
        );

        // then
        assertEquals(expectedTransaction, transaction);
    }

    @Test(expected = UncheckedIOException.class)
    public void shouldThrowExceptionWhenBlockCantBeBroadcasted() {
        // given
        String wrongHash = TARGET_HASH;

        mockAccount();
        mockWork();

        given(api.execute(any(), eq(BlockHash.class))).willReturn(new BlockHash(wrongHash));

        // when
        operations.send(
                PRIVATE_KEY,
                FRONTIER,
                TARGET_ACCOUNT,
                new BigInteger("70")
        );
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDoubleSpending() {
        // given
        String wrongHash = TARGET_HASH;

        mockAccount();

        // when
        operations.send(
                PRIVATE_KEY,
                wrongHash,
                TARGET_ACCOUNT,
                new BigInteger("70")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNotEnoughBalance() {
        // given
        String wrongHash = TARGET_HASH;

        mockAccount();

        // when
        operations.send(
                PRIVATE_KEY,
                wrongHash,
                TARGET_ACCOUNT,
                BALANCE.multiply(BigInteger.valueOf(2))
        );
    }

    @Test
    public void shouldReceive() {
        // given
        BigInteger amount = new BigInteger("50");

        Map<String, BigInteger> pending = of(TARGET_HASH, amount);
        given(accountOperations.getPending(ACCOUNT)).willReturn(pending);

        mockAccount();
        mockWork();

        NanoTransaction<NanoStateBlock> expectedTransaction = createReceiveTransaction(amount);

        given(api.execute(any(), eq(BlockHash.class))).willReturn(new BlockHash(expectedTransaction.getHash()));

        // when
        List<NanoTransaction<NanoStateBlock>> transactions = operations.receive(PRIVATE_KEY);

        // then
        assertEquals(singletonList(expectedTransaction), transactions);

    }

    private NanoTransaction<NanoStateBlock> createSendTransaction(BigInteger amount) {
        NanoStateBlock block = NanoStateBlock.builder()
                .account(ACCOUNT)
                .previous(FRONTIER)
                .representative(REPRESENTATIVE)
                .balance(BALANCE.subtract(amount))
                .link(TARGET_ACCOUNT)
                .build();
        return NanoTransaction.<NanoStateBlock>builder()
                .block(block)
                .signature("94D9D49BE57F9B5FE00DA00AEFE9B4497772E479089B54341D4E976CAB0EFD2178353D727ADDD9AFF07A84C4A6071593ED0C9F048A877DFE03492656B623F005")
                .work("572a137c36b8ff90")
                .build();
    }


    private NanoTransaction<NanoStateBlock> createReceiveTransaction(BigInteger amount) {
        NanoStateBlock block = NanoStateBlock.builder()
                .account(ACCOUNT)
                .previous(FRONTIER)
                .representative(REPRESENTATIVE)
                .balance(BALANCE.add(amount))
                .link(TARGET_HASH)
                .build();
        return NanoTransaction.<NanoStateBlock>builder()
                .block(block)
                .signature("3ECA349CFED73A61357592DBBD2D409306964F36DFEFB29AFB79705B34FA31A73B30AF44AF8FC95CFA824AA37626CB6B0CF559CF8E13509FC4921881C9065F0D")
                .work("572a137c36b8ff90")
                .build();
    }


    private void mockAccount() {
        NanoAccountInfo info = NanoAccountInfo.builder()
                .frontier(FRONTIER)
                .representative(REPRESENTATIVE)
                .balance(BALANCE)
                .build();
        given(accountOperations.getInfo(ACCOUNT)).willReturn(info);
    }

    private void mockWork() {
        given(workOperations.perform(FRONTIER)).willReturn("572a137c36b8ff90");
    }

}