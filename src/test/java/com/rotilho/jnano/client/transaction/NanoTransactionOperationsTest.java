package com.rotilho.jnano.client.transaction;

import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.account.NanoAccountInfo;
import com.rotilho.jnano.client.account.NanoAccountOperations;
import com.rotilho.jnano.client.block.NanoStateBlock;
import com.rotilho.jnano.client.transaction.NanoTransactionOperations.BlockHash;
import com.rotilho.jnano.client.work.NanoWorkOperations;
import com.rotilho.jnano.commons.NanoAccounts;
import com.rotilho.jnano.commons.NanoAmount;
import com.rotilho.jnano.commons.NanoBaseAccountType;
import com.rotilho.jnano.commons.NanoHelper;
import com.rotilho.jnano.commons.NanoKeys;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.of;
import static com.rotilho.jnano.commons.NanoHelper.toByteArray;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
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
    private final static NanoAmount BALANCE = NanoAmount.ofRaw("100");
    private final static String REPRESENTATIVE = "xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3";
    private final static String FRONTIER = "FF84533A571D953A596EA401FD41743AC85D04F406E76FDE4408EAED50B473C5";

    @Mock
    private NanoAPI api;
    @Mock
    private NanoAccountOperations accountOperations;
    @Mock
    private NanoWorkOperations workOperations;

    private NanoTransactionOperations operations;

    @Before
    public void setUp() {
        operations = NanoTransactionOperations.of(NanoBaseAccountType.NANO, api, accountOperations, workOperations);
    }

    @Test
    public void shouldOpen() {
        // given
        Map<String, NanoAmount> pending = of(TARGET_HASH, BALANCE);
        given(accountOperations.getPending(ACCOUNT)).willReturn(pending);

        NanoTransaction<NanoStateBlock> expectedTransaction = createOpenTransaction();

        mockWork(NanoHelper.toHex(PUBLIC_KEY), expectedTransaction.getWork());

        given(api.execute(any(), eq(BlockHash.class))).willReturn(new BlockHash(expectedTransaction.getHash()));

        // when
        NanoTransaction<NanoStateBlock> transaction = operations.open(PRIVATE_KEY, REPRESENTATIVE).get();

        // then
        assertEquals(expectedTransaction, transaction);
    }

    @Test
    public void shouldReturnEmptyWhenOpeningAccountWithEmptyPendingTransactions() {
        // given
        given(accountOperations.getPending(ACCOUNT)).willReturn(emptyMap());

        // when
        Optional<NanoTransaction<NanoStateBlock>> transactions = operations.open(PRIVATE_KEY, REPRESENTATIVE);

        // then
        assertFalse(transactions.isPresent());
    }


    @Test
    public void shouldReceive() {
        // given
        NanoAmount amount = NanoAmount.ofRaw("50");

        Map<String, NanoAmount> pending = of(TARGET_HASH, amount);
        given(accountOperations.getPending(ACCOUNT, NanoAmount.ofRaw(BigDecimal.ONE))).willReturn(pending);

        mockAccount();
        mockWork();

        NanoTransaction<NanoStateBlock> expectedTransaction = createReceiveTransaction(amount);

        given(api.execute(any(), eq(BlockHash.class))).willReturn(new BlockHash(expectedTransaction.getHash()));

        // when
        List<NanoTransaction<NanoStateBlock>> transactions = operations.receive(PRIVATE_KEY);

        // then
        assertEquals(singletonList(expectedTransaction), transactions);
    }

    @Test
    public void shouldSend() {
        // given
        NanoAmount amount = NanoAmount.ofRaw("70");

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
                NanoAmount.ofRaw("70")
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
                NanoAmount.ofRaw("70")
        );
    }


    @Test
    public void shouldChange() {
        // given
        String representative = "xrb_1brainb3zz81wmhxndsbrjb94hx3fhr1fyydmg6iresyk76f3k7y7jiazoji";

        mockAccount();
        mockWork();

        NanoTransaction<NanoStateBlock> expectedTransaction = createChangeTransaction(representative);

        given(api.execute(any(), eq(BlockHash.class))).willReturn(new BlockHash(expectedTransaction.getHash()));

        // when
        NanoTransaction<NanoStateBlock> transactions = operations.change(PRIVATE_KEY, representative);

        // then
        assertEquals(expectedTransaction, transactions);
    }


    private NanoTransaction<NanoStateBlock> createOpenTransaction() {
        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(NanoBaseAccountType.NANO)
                .account(ACCOUNT)
                .previous("0000000000000000000000000000000000000000000000000000000000000000")
                .representative(REPRESENTATIVE)
                .balance(BALANCE)
                .link(TARGET_HASH)
                .build();
        return NanoTransaction.<NanoStateBlock>builder()
                .block(block)
                .signature("7B937B422CC41981237F0D78EF1BC9B633698809BA3ECC7F9075CCA6630431983EBB1C9A5C628F2584ACA45BDE4DA8AAED23FE29CAE2535E9BA4BB47BD79C103")
                .work("fa514e88cca1589f")
                .build();
    }


    private NanoTransaction<NanoStateBlock> createReceiveTransaction(NanoAmount amount) {
        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(NanoBaseAccountType.NANO)
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

    private NanoTransaction<NanoStateBlock> createSendTransaction(NanoAmount amount) {
        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(NanoBaseAccountType.NANO)
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


    private NanoTransaction<NanoStateBlock> createChangeTransaction(String representative) {
        NanoStateBlock block = NanoStateBlock.builder()
                .accountType(NanoBaseAccountType.NANO)
                .account(ACCOUNT)
                .previous(FRONTIER)
                .representative(representative)
                .balance(BALANCE)
                .link("0000000000000000000000000000000000000000000000000000000000000000")
                .build();
        return NanoTransaction.<NanoStateBlock>builder()
                .block(block)
                .signature("AE7E648868996B0EB625697F342D88A5F1B98FBB7BC613AA23B5372C9E25B5FC8EE0E4D3608F7DC7B56B13120EA504238AD1D77D5FEC0D742EAD4FFD5DCCF30C")
                .work("572a137c36b8ff90")
                .build();
    }


    private void mockAccount() {
        NanoAccountInfo info = NanoAccountInfo.builder()
                .frontier(FRONTIER)
                .representative(REPRESENTATIVE)
                .balance(BALANCE)
                .build();
        given(accountOperations.getInfoOrFail(ACCOUNT)).willReturn(info);
    }

    private void mockWork() {
        mockWork(FRONTIER, "572a137c36b8ff90");
    }

    private void mockWork(String hash, String work) {
        given(workOperations.perform(hash)).willReturn(work);
    }
}