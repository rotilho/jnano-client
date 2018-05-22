package com.rotilho.jnano.client;

import com.rotilho.jnano.client.NanoAccountOperations.AccountInformation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class NanoAccountOperationsTest {
    private static final String ACCOUNT = "nano_3iwi45me3cgo9aza9wx5f7rder37hw11xtc1ek8psqxw5oxb8cujjad6qp9y";

    @Rule
    public HttpMock httpMock = new HttpMock();

    private NanoAccountOperations operations;

    @Before
    public void setUp() {
        operations = new NanoAccountOperations(httpMock.getNanoAPI());
    }

    @Test
    public void shouldReturnInfo() throws Exception {
        // given
        String request = "{  \n" +
                "  \"action\": \"account_info\",  \n" +
                "  \"account\": \"xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\",    \n" +
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
                "    \"representative\": \"xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3\",   \n" +
                "    \"weight\": \"1105577030935649664609129644855132177\",   \n" +
                "    \"pending\": \"2309370929000000000000000000000000\"   \n" +
                "}";
        httpMock.mock(request, response);

        // when
        AccountInformation balance = operations.getInfo("xrb_3t6k35gi95xu6tergt6p69ck76ogmitsa8mnijtpxm9fkcm736xtoncuohr3");

        // then
        JSONAssert.assertEquals(response, JSON.stringify(balance), JSONCompareMode.LENIENT);
    }


}