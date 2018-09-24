package com.rotilho.jnano.client.block;

import com.rotilho.jnano.commons.NanoAmount;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NanoStateBlockTest {
    @Test
    public void shouldHashOpenStateBlock() {
        //given
        // given
        String account = "xrb_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php";
        String previous = "0";
        String representative = "xrb_3p1asma84n8k84joneka776q4egm5wwru3suho9wjsfyuem8j95b3c78nw8j";
        NanoAmount balance = NanoAmount.ofRaw("1");
        String link = "1EF0AD02257987B48030CC8D38511D3B2511672F33AF115AD09E18A86A8355A8";

        // when
        NanoStateBlock block = NanoStateBlock.builder()
                .account(account)
                .previous(previous)
                .representative(representative)
                .balance(balance)
                .link(link)
                .build();

        // then
        assertEquals("FC5A7FB777110A858052468D448B2DF22B648943C097C0608D1E2341007438B0", block.getHash());
    }

    @Test
    public void shouldHashReceiveStateBlock() {
        // given
        String account = "xrb_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php";
        String previous = "FC5A7FB777110A858052468D448B2DF22B648943C097C0608D1E2341007438B0";
        String representative = "xrb_3p1asma84n8k84joneka776q4egm5wwru3suho9wjsfyuem8j95b3c78nw8j";
        NanoAmount balance = NanoAmount.ofRaw("5000000000000000000000000000001");
        String link = "B2EC73C1F503F47E051AD72ECB512C63BA8E1A0ACC2CEE4EA9A22FE1CBDB693F";

        // when
        NanoStateBlock block = NanoStateBlock.builder()
                .account(account)
                .previous(previous)
                .representative(representative)
                .balance(balance)
                .link(link)
                .build();

        // then
        assertEquals("597395E83BD04DF8EF30AF04234EAAFE0606A883CF4AEAD2DB8196AAF5C4444F", block.getHash());
    }

    @Test
    public void shouldHashSendStateBlock() {
        // given
        String account = "xrb_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php";
        String previous = "597395E83BD04DF8EF30AF04234EAAFE0606A883CF4AEAD2DB8196AAF5C4444F";
        String representative = "xrb_3p1asma84n8k84joneka776q4egm5wwru3suho9wjsfyuem8j95b3c78nw8j";
        NanoAmount balance = NanoAmount.ofRaw("3000000000000000000000000000001");
        String link = "xrb_1q3hqecaw15cjt7thbtxu3pbzr1eihtzzpzxguoc37bj1wc5ffoh7w74gi6p";

        // when
        NanoStateBlock block = NanoStateBlock.builder()
                .account(account)
                .previous(previous)
                .representative(representative)
                .balance(balance)
                .link(link)
                .build();

        // then
        assertEquals("128106287002E595F479ACD615C818117FCB3860EC112670557A2467386249D4", block.getHash());
    }

    @Test
    public void shouldHashChangeStateBlock() {
        // given
        String account = "xrb_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php";
        String previous = "128106287002E595F479ACD615C818117FCB3860EC112670557A2467386249D4";
        String representative = "xrb_1anrzcuwe64rwxzcco8dkhpyxpi8kd7zsjc1oeimpc3ppca4mrjtwnqposrs";
        NanoAmount balance = NanoAmount.ofRaw("3000000000000000000000000000001");
        String link = "0000000000000000000000000000000000000000000000000000000000000000";

        // when
        NanoStateBlock block = NanoStateBlock.builder()
                .account(account)
                .previous(previous)
                .representative(representative)
                .balance(balance)
                .link(link)
                .build();

        // then
        assertEquals("2A322FD5ACAF50C057A8CF5200A000CF1193494C79C786B579E0B4A7D10E5A1E", block.getHash());
    }

    @Test
    public void shouldHashChangeAndSendStateBlock() {
        // given
        String account = "xrb_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php";
        String previous = "2A322FD5ACAF50C057A8CF5200A000CF1193494C79C786B579E0B4A7D10E5A1E";
        String representative = "xrb_3p1asma84n8k84joneka776q4egm5wwru3suho9wjsfyuem8j95b3c78nw8j";
        NanoAmount balance = NanoAmount.ofRaw("1");
        String link = "xrb_1q3hqecaw15cjt7thbtxu3pbzr1eihtzzpzxguoc37bj1wc5ffoh7w74gi6p";

        // when
        NanoStateBlock block = NanoStateBlock.builder()
                .account(account)
                .previous(previous)
                .representative(representative)
                .balance(balance)
                .link(link)
                .build();

        // then
        assertEquals("9664412A834F0C27056C7BC4A363FBAE86DF8EF51341A5A5EA14061727AE519F", block.getHash());
    }
}