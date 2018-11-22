package com.rotilho.jnano.client;

import com.rotilho.jnano.commons.NanoAccountType;

public class NanoTestAccountType implements NanoAccountType {
    @Override
    public String extractEncodedPublicKey(String account) {
        return account.substring(5, 57);
    }

    @Override
    public String regex() {
        return "^(test_)[13456789abcdefghijkmnopqrstuwxyz]{60}$";
    }

    @Override
    public String prefix() {
        return "test_";
    }
}
