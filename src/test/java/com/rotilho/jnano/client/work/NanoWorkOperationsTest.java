package com.rotilho.jnano.client.work;

import com.rotilho.jnano.client.HttpMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NanoWorkOperationsTest {

    @Rule
    public HttpMock httpMock = new HttpMock();

    private NanoWorkOperations operations;

    @Before
    public void setUp() {
        operations = NanoWorkOperations.of(httpMock.getNanoAPI());
    }


    @Test
    public void shouldReturnInfo() {
        // given
        String request = "{  \n" +
                "    \"action\": \"work_generate\",  \n" +
                "    \"hash\": \"718CC2121C3E641059BC1C2CFC45666C99E8AE922F7A807B7D07B62C995D79E2\"  \n" +
                "}";
        String response = "{  \n" +
                "    \"work\": \"2bf29ef00786a6bc\"  \n" +
                "}";
        httpMock.mock(request, response);

        // when
        String work = operations.generate("718CC2121C3E641059BC1C2CFC45666C99E8AE922F7A807B7D07B62C995D79E2");

        // then
        assertEquals("2bf29ef00786a6bc", work);
    }
}