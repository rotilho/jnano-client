package com.rotilho.jnano.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rotilho.jnano.client.NanoAccountOperations.BalanceInformation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UncheckedIOException;
import java.math.BigInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.rotilho.jnano.client.NanoAccountOperations.BalanceRequest;
import static org.junit.Assert.assertEquals;

public class NanoAPITest {
    private static final BalanceRequest REQUEST = new BalanceRequest("xrb_3e3j5tkog48pnny9dmfzj1r16pg8t1e76dz5tmac6iq689wyjfpi00000000");

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().dynamicPort());

    private NanoAPI nanoAPI;

    @Before
    public void setUp() {
        nanoAPI = new NanoAPI("http://localhost:" + wireMockRule.port() + "/");
    }

    @Test
    public void shouldSendRequestAndReceiveResponse() throws JsonProcessingException {
        // given
        String json = new ObjectMapper().writeValueAsString(REQUEST);

        stubFor(post(urlEqualTo("/"))
                .withRequestBody(equalToJson(json))
                .willReturn(aResponse()
                        .withBody("{  \n" +
                                "  \"balance\": \"10000\",  \n" +
                                "  \"pending\": \"10000\",  \n" +
                                "  \"unknown\": \"10000\"  \n" +
                                "}")));

        // when
        BalanceInformation response = nanoAPI.execute(REQUEST, BalanceInformation.class);

        // then
        BalanceInformation expectedResponse = new BalanceInformation(new BigInteger("10000"), new BigInteger("10000"));
        assertEquals(expectedResponse, response);
    }

    @Test(expected = UncheckedIOException.class)
    public void shouldThrowExceptionWhenServerReurnError() {
        // given
        stubFor(post(urlEqualTo("/")).willReturn(aResponse().withStatus(500).withStatusMessage("Internal Server Error")));

        // when
        nanoAPI.execute(REQUEST, BalanceInformation.class);
    }

}