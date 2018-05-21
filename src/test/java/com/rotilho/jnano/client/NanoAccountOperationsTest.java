package com.rotilho.jnano.client;

import com.rotilho.jnano.client.NanoAccountOperations.BalanceRequest;
import com.rotilho.jnano.client.NanoAccountOperations.BalanceResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class NanoAccountOperationsTest {
    private static final String ACCOUNT = "nano_3iwi45me3cgo9aza9wx5f7rder37hw11xtc1ek8psqxw5oxb8cujjad6qp9y";

    @Mock
    private NanoAPI api;

    @InjectMocks
    private NanoAccountOperations operations;

    @Test
    public void shouldReturnBalance() {
        // given
        BigInteger expectedBalance = BigInteger.valueOf(100);
        BalanceResponse response = new BalanceResponse(expectedBalance, BigInteger.ZERO);
        given(api.execute(new BalanceRequest(ACCOUNT), BalanceResponse.class)).willReturn(response);

        // when
        BigInteger balance = operations.getBalance(ACCOUNT, false);

        // then
        assertEquals(expectedBalance, balance);
    }

    @Test
    public void shouldReturnTotalBalance() {
        // given
        BigInteger expectedBalance = BigInteger.valueOf(100);
        BigInteger expectedPending = BigInteger.valueOf(100);
        BalanceResponse response = new BalanceResponse(expectedBalance, expectedPending);
        given(api.execute(new BalanceRequest(ACCOUNT), BalanceResponse.class)).willReturn(response);

        // when
        BigInteger totalBalance = operations.getBalance(ACCOUNT, true);

        // then
        assertEquals(expectedBalance.add(expectedPending), totalBalance);
    }

}