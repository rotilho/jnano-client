package com.rotilho.jnano.client.block;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NanoChangeBlockTest {

    @Test
    public void shouldHashChangeBlock() {
        //given
        String previous = "F958305C0FF0551421D4ABEDCCF302079D020A0A3833E33F185E2B0415D4567A";
        String representative = "xrb_18gmu6engqhgtjnppqam181o5nfhj4sdtgyhy36dan3jr9spt84rzwmktafc";

        // when
        NanoChangeBlock block = NanoChangeBlock.builder()
                .previous(previous)
                .representative(representative)
                .build();

        // then
        assertEquals("654FA425CEBFC9E7726089E4EDE7A105462D93DBC915FFB70B50909920A7D286", block.getHash());
    }

}