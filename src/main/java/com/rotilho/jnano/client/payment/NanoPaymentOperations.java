package com.rotilho.jnano.client.payment;

import com.rotilho.jnano.client.NanoAPI;
import com.rotilho.jnano.client.account.NanoAccountOperations;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(staticName = "of")
public class NanoPaymentOperations {
    @NonNull
    private final NanoAPI api;
    @NonNull
    private final NanoAccountOperations accountOperations;
}
