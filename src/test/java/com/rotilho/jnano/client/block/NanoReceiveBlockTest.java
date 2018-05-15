package com.rotilho.jnano.client.block;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NanoReceiveBlockTest {

    @Test
    public void shouldHashReceiveBlock() {
        //given
        String previous = "67022ABE190D5C1E42F32F14EEBD3E25A36CA26B00BF1D038AE86055CCD105F5";
        String source = "ED9554BD4D0F3BDCC8A7BDED5DEF09C503F4E94EF3698047441EFBBCC0D241F7";

        // when
        NanoReceiveBlock block = NanoReceiveBlock.of(previous, source);

        // then
        assertEquals("49876A7B00159C2F6EEB33BEAEE07FC637F4A29DD9631DFFD3DA015DE2165FE6", block.getHash());
    }

}