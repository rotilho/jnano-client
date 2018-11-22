package com.rotilho.jnano.client.block;

import com.rotilho.jnano.client.NanoTestAccountType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NanoOpenBlockTest {

    @Test
    public void shouldHashOpenBlock() {
        //given
        String source = "E89208DD038FBB269987689621D52292AE9C35941A7484756ECCED92A65093BA";
        String representative = "test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3";
        String account = "test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3";

        // when
        NanoOpenBlock block = NanoOpenBlock.builder()
                .accountType(new NanoTestAccountType())
                .source(source)
                .representative(representative)
                .account(account)
                .build();

        // then
        assertEquals("991CF190094C00F0B68E2E5F75F6BEE95A2E0BD93CEAA4A6734DB9F19B728948", block.getHash());
    }

}