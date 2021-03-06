package com.rotilho.jnano.client.account;

import com.google.common.collect.ImmutableMap;

import com.rotilho.jnano.client.HttpMock;
import com.rotilho.jnano.client.JSON;
import com.rotilho.jnano.client.NanoTestAccountType;
import com.rotilho.jnano.client.transaction.NanoTransaction;
import com.rotilho.jnano.commons.NanoAmount;
import com.rotilho.jnano.commons.NanoHelper;
import com.rotilho.jnano.commons.NanoKeys;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class NanoAccountOperationsTest {
    @Rule
    public HttpMock httpMock = new HttpMock();

    private NanoAccountOperations operations;

    @Before
    public void setUp() {
        operations = NanoAccountOperations.of(new NanoTestAccountType(), httpMock.getNanoAPI());
    }

    @Test
    public void shouldReturnInfo() throws Exception {
        // given
        String request = "{  \n" +
                "  \"action\": \"account_info\",  \n" +
                "  \"account\": \"test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\",    \n" +
                "  \"representative\": \"true\",  \n" +
                "  \"weight\": \"true\",  \n" +
                "  \"pending\": \"true\"  \n" +
                "}";
        String response = "{  \n" +
                "    \"frontier\": \"FF84533A571D953A596EA401FD41743AC85D04F406E76FDE4408EAED50B473C5\",   \n" +
                "    \"open_block\": \"991CF190094C00F0B68E2E5F75F6BEE95A2E0BD93CEAA4A6734DB9F19B728948\",   \n" +
                "    \"representative_block\": \"991CF190094C00F0B68E2E5F75F6BEE95A2E0BD93CEAA4A6734DB9F19B728948\",   \n" +
                "    \"balance\": \"235580100176034320859259343606608761791\",   \n" +
                "    \"modified_timestamp\": \"1501793775\",   \n" +
                "    \"block_count\": \"33\",   \n" +
                "    \"representative\": \"test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\",   \n" +
                "    \"weight\": \"1105577030935649664609129644855132177\",   \n" +
                "    \"pending\": \"2309370929000000000000000000000000\"   \n" +
                "}";
        httpMock.mock(request, response);

        // when
        NanoAccountInfo information = operations.getInfoOrFail("test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3");

        // then
        assertEquals(response, JSON.stringify(information), JSONCompareMode.LENIENT);
    }

    @Test
    public void shouldReturnInfoWhenAccountDoesNotExists() {
        // given
        String request = "{  \n" +
                "  \"action\": \"account_info\",  \n" +
                "  \"account\": \"test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\",    \n" +
                "  \"representative\": \"true\",  \n" +
                "  \"weight\": \"true\",  \n" +
                "  \"pending\": \"true\"  \n" +
                "}";
        String response = "{  \n" +
                "    \"error\": \"Account not found\"   \n" +
                "}";
        httpMock.mock(request, response);

        // when
        Optional<NanoAccountInfo> info = operations.getInfo("test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3");

        // then
        assertFalse(info.isPresent());
    }

    @Test
    public void shouldCreateAccount() {
        // given
        byte[] seed = NanoHelper.toByteArray("1234567890123456789012345678901234567890123456789012345678901234");
        byte[] privateKey = NanoKeys.createPrivateKey(seed, 0);
        byte[] publicKey = NanoKeys.createPublicKey(privateKey);

        // when
        String account = operations.create(publicKey);

        // then
        String expectedAccount = "test_3iwi45me3cgo9aza9wx5f7rder37hw11xtc1ek8psqxw5oxb8cujjad6qp9y";
        assertEquals(expectedAccount, account);
    }

    @Test
    public void shouldReturnHistoryWithOpenBlock() throws Exception {
        String block = "{\n" +
                "         \"type\":\"open\",\n" +
                "         \"source\":\"19D3D919475DEED4696B5D13018151D1AF88B2BD3BCFF048B45031C1F36D1858\",\n" +
                "         \"representative\":\"test_1hza3f7wiiqa7ig3jczyxj5yo86yegcmqk3criaz838j91sxcckpfhbhhra1\",\n" +
                "         \"account\":\"test_1hza3f7wiiqa7ig3jczyxj5yo86yegcmqk3criaz838j91sxcckpfhbhhra1\",\n" +
                "         \"work\":\"4ec76c9bda2325ed\",\n" +
                "         \"signature\":\"5974324F8CC42DA56F62FC212A17886BDCB18DE363D04DA84EEDC99CB4A33919D14A2CF9DE9D534FAA6D0B91D01F0622205D898293525E692586C84F2DCF9208\"\n" +
                "}\n";

        shouldReturnHistory("test_1hza3f7wiiqa7ig3jczyxj5yo86yegcmqk3criaz838j91sxcckpfhbhhra1", block);
    }

    @Test
    public void shouldReturnHistoryWithReceiveBlock() throws Exception {
        String block = "{\n" +
                "    \"type\": \"receive\",\n" +
                "    \"previous\": \"F47B23107E5F34B2CE06F562B5C435DF72A533251CB414C51B2B62A8F63A00E4\",\n" +
                "    \"source\": \"19D3D919475DEED4696B5D13018151D1AF88B2BD3BCFF048B45031C1F36D1858\",\n" +
                "    \"work\": \"6acb5dd43a38d76a\",\n" +
                "    \"signature\": \"A13FD22527771667D5DFF33D69787D734836A3561D8A490C1F4917A05D77EA09860461D5FBFC99246A4EAB5627F119AD477598E22EE021C4711FACF4F3C80D0E\"\n" +
                "}\n";

        shouldReturnHistory("test_1hza3f7wiiqa7ig3jczyxj5yo86yegcmqk3criaz838j91sxcckpfhbhhra1", block);
    }

    @Test
    public void shouldReturnHistoryWithSendBlock() throws Exception {
        String block = "{\n" +
                "    \"type\": \"send\",\n" +
                "    \"previous\": \"314BA8D9057678C1F53371C2DB3026C1FAC01EC8E7802FD9A2E8130FC523429E\",\n" +
                "    \"destination\": \"test_18gmu6engqhgtjnppqam181o5nfhj4sdtgyhy36dan3jr9spt84rzwmktafc\",\n" +
                "    \"balance\": \"1000000\",\n" +
                "    \"work\": \"478563b2d9facfd4\",\n" +
                "    \"signature\": \"F19CA177EFA8692C8CBF7478CE3213F56E4A85DF760DA7A9E69141849831F8FD79BA9ED89CEC807B690FB4AA42D5008F9DBA7115E63C935401F1F0EFA547BC00\"\n" +
                "}\n";

        shouldReturnHistory("test_18gmu6engqhgtjnppqam181o5nfhj4sdtgyhy36dan3jr9spt84rzwmktafc", block);
    }

    @Test
    public void shouldReturnHistoryWithChangeBlock() throws Exception {
        String block = "{\n" +
                "    \"type\": \"change\",\n" +
                "    \"previous\": \"F958305C0FF0551421D4ABEDCCF302079D020A0A3833E33F185E2B0415D4567A\",\n" +
                "    \"representative\": \"test_18gmu6engqhgtjnppqam181o5nfhj4sdtgyhy36dan3jr9spt84rzwmktafc\",\n" +
                "    \"work\": \"55e5b7a83edc3f4f\",\n" +
                "    \"signature\": \"98B4D56881D9A88B170A6B2976AE21900C26A27F0E2C338D93FDED56183B73D19AA5BEB48E43FCBB8FF8293FDD368CEF50600FECEFD490A0855ED702ED209E04\"\n" +
                "}\n";

        shouldReturnHistory("test_18gmu6engqhgtjnppqam181o5nfhj4sdtgyhy36dan3jr9spt84rzwmktafc", block);
    }

    @Test
    public void shouldReturnHistoryWithStateBlock() throws Exception {
        String block = "{\n" +
                "    \"type\": \"state\",\n" +
                "    \"account\": \"test_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php\",\n" +
                "    \"previous\": \"0000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "    \"representative\": \"test_3p1asma84n8k84joneka776q4egm5wwru3suho9wjsfyuem8j95b3c78nw8j\",\n" +
                "    \"balance\": \"1\",\n" +
                "    \"link\": \"1EF0AD02257987B48030CC8D38511D3B2511672F33AF115AD09E18A86A8355A8\",\n" +
                "    \"signature\": \"593D865DDCC6018F197C0EACD15E5CED3DAF134EDFAF6553DB9C1D0E11DBDCBBE1B01E1A4C6D4378289567E59BA122DA5BFD49729AA6C2B0FC9E592A546B4F09\",\n" +
                "    \"work\": \"0000000000000000\"\n" +
                "}\n";

        shouldReturnHistory("test_3igf8hd4sjshoibbbkeitmgkp1o6ug4xads43j6e4gqkj5xk5o83j8ja9php", block);
    }

    @Test
    public void shouldReturnPublicKey() {
        // given
        String address = "test_3iwi45me3cgo9aza9wx5f7rder37hw11xtc1ek8psqxw5oxb8cujjad6qp9y";

        // when
        byte[] publicKey = operations.toPublicKey(address);

        // then
        assertEquals(address, operations.create(publicKey));
    }

    @Test
    public void shouldReturnPendingTransactionListWhenSendingOneAccount() {
        // given
        String request = "{  \n" +
                "  \"action\": \"accounts_pending\",  \n" +
                "  \"accounts\": [\"test_1111111111111111111111111111111111111111111111111117353trpda\"], \n" +
                "  \"count\": \"9223372036854775807\", \n" +
                "  \"threshold\": \"1\" \n" +
                "}";
        String response = "{  \n" +
                "  \"blocks\" : {\n" +
                "    \"test_1111111111111111111111111111111111111111111111111117353trpda\": {    \n" +
                "        \"142A538F36833D1CC78B94E11C766F75818F8B940771335C6C1B8AB880C5BB1D\": \"6000000000000000000000000000000\"    \n" +
                "    }    \n" +
                "   } \n" +
                "}";
        httpMock.mock(request, response);

        // when
        Map<String, NanoAmount> pending = operations.getPending("test_1111111111111111111111111111111111111111111111111117353trpda");

        // then
        assertEquals(singletonMap("142A538F36833D1CC78B94E11C766F75818F8B940771335C6C1B8AB880C5BB1D", NanoAmount.ofRaw("6000000000000000000000000000000")), pending);
    }

    @Test
    public void shouldReturnPendingTransactionMapWhenSendingMultipleAccount() {
        // given
        String request = "{  \n" +
                "  \"action\": \"accounts_pending\",  \n" +
                "  \"accounts\": [\"test_1111111111111111111111111111111111111111111111111117353trpda\", \"test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\"],  \n" +
                "  \"count\": \"9223372036854775807\", \n" +
                "  \"threshold\": \"1\" \n" +
                "}";
        String response = "{  \n" +
                "  \"blocks\" : {\n" +
                "    \"test_1111111111111111111111111111111111111111111111111117353trpda\": {    \n" +
                "        \"142A538F36833D1CC78B94E11C766F75818F8B940771335C6C1B8AB880C5BB1D\": \"6000000000000000000000000000000\"    \n" +
                "    },    \n" +
                "    \"test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\": {    \n" +
                "        \"4C1FEEF0BEA7F50BE35489A1233FE002B212DEA554B55B1B470D78BD8F210C74\": \"106370018000000000000000000000000\"    \n" +
                "    }  \n" +
                "   } \n" +
                "}";
        httpMock.mock(request, response);

        // when
        List<String> accounts = Arrays.asList(
                "test_1111111111111111111111111111111111111111111111111117353trpda",
                "test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3"
        );
        Map<String, Map<String, NanoAmount>> pending = operations.getPending(accounts);

        // then

        Map<String, Map<String, NanoAmount>> expectedPending = ImmutableMap.of(
                "test_1111111111111111111111111111111111111111111111111117353trpda", singletonMap("142A538F36833D1CC78B94E11C766F75818F8B940771335C6C1B8AB880C5BB1D", NanoAmount.ofRaw("6000000000000000000000000000000")),
                "test_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3", singletonMap("4C1FEEF0BEA7F50BE35489A1233FE002B212DEA554B55B1B470D78BD8F210C74", NanoAmount.ofRaw("106370018000000000000000000000000"))
        );
        assertEquals(expectedPending, pending);
    }

    @Test
    public void shouldReturnTrueWhenValidatingValidAddress() {
        assertTrue(operations.isValid("test_1111111111111111111111111111111111111111111111111117353trpda"));
    }


    private void shouldReturnHistory(String account, String block) throws Exception {
        // given
        String request = "{  \n" +
                "  \"action\": \"account_history\",  \n" +
                "  \"account\": \"" + account + "\",  \n" +
                "  \"count\": \"-1\"\n," +
                "  \"raw\": \"true\"\n" +
                "}";
        String response = "{\n" +
                "   \"history\":[\n" +
                block +
                "   ]\n" +
                "}";
        httpMock.mock(request, response);

        // when
        List<NanoTransaction<?>> transactions = operations.getHistory(account);

        // then
        assertEquals(block, JSON.stringify(transactions.get(0)), JSONCompareMode.LENIENT);
    }
}