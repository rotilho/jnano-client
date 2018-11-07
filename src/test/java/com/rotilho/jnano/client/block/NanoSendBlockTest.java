package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoAmount;
import com.rotilho.jnano.commons.NanoBaseAccountType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NanoSendBlockTest {

    @Test
    public void shouldHashSendBlock() {
        //given
        String previous = "991CF190094C00F0B68E2E5F75F6BEE95A2E0BD93CEAA4A6734DB9F19B728948";
        String destination = "xrb_13ezf4od79h1tgj9aiu4djzcmmguendtjfuhwfukhuucboua8cpoihmh8byo";
        NanoAmount balance = NanoAmount.ofRaw("337010421085160209006996005437231978653");

        // when
        NanoSendBlock block = NanoSendBlock.builder()
                .accountType(NanoBaseAccountType.NANO)
                .previous(previous)
                .destination(destination)
                .balance(balance)
                .build();

        // then
        assertEquals("A170D51B94E00371ACE76E35AC81DC9405D5D04D4CEBC399AEACE07AE05DD293", block.getHash());
    }

}