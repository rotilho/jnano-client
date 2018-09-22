package com.rotilho.jnano.client;

import com.rotilho.jnano.client.account.NanoAccountOperations;
import com.rotilho.jnano.client.transaction.NanoTransactionOperations;
import com.rotilho.jnano.client.work.NanoLocalWorkOperations;
import com.rotilho.jnano.client.work.NanoRemoteWorkOperations;
import com.rotilho.jnano.client.work.NanoWorkOperations;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class Nano {
    @NonNull
    private final NanoAPI api;
    private final NanoAPI work;

    public static Nano.NanoBuilder builder() {
        return new NanoBuilder();
    }

    public NanoAccountOperations getAccountOperations() {
        return NanoAccountOperations.of(api);
    }

    public NanoWorkOperations getWorkOperations() {
        if (work == null) {
            return new NanoLocalWorkOperations();
        }
        return NanoRemoteWorkOperations.of(work);
    }

    public NanoTransactionOperations getPaymentOperations() {
        NanoWorkOperations workOperations = getWorkOperations();
        return getPaymentOperations(workOperations);
    }

    public NanoTransactionOperations getPaymentOperations(NanoWorkOperations workOperations) {
        NanoAccountOperations accountOperations = getAccountOperations();
        return NanoTransactionOperations.of(api, accountOperations, workOperations);
    }

    public static class NanoBuilder {
        private String endpoint;
        private Map<String, String> headers = new HashMap<>();

        private String workEndpoint;
        private Map<String, String> workHeaders = new HashMap<>();

        public NanoBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            if (workEndpoint == null) {
                this.workEndpoint = this.endpoint;
            }
            return this;
        }

        public NanoBuilder headers(Map<String, String> headers) {
            this.headers = new HashMap<>(headers);
            if (workHeaders.isEmpty()) {
                this.workHeaders = this.headers;
            }
            return this;
        }

        public NanoBuilder workEndpoint(String workEndpoint) {
            this.workEndpoint = workEndpoint;
            return this;
        }

        public NanoBuilder workHeaders(Map<String, String> workHeaders) {
            this.workHeaders = new HashMap<>(workHeaders);
            return this;
        }

        public Nano build() {
            NanoAPI api = NanoAPI.builder().endpoint(endpoint).headers(headers).build();
            NanoAPI work = NanoAPI.builder().endpoint(workEndpoint).headers(workHeaders).build();
            return new Nano(api, work);
        }

    }

}
