package com.rotilho.jnano.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.UncheckedIOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class NanoAPITest {
    @Rule
    public HttpMock httpMock = new HttpMock();

    private NanoAPI nanoAPI;

    @Before
    public void setUp() {
        nanoAPI = httpMock.getNanoAPI();
    }

    @Test(expected = UncheckedIOException.class)
    public void shouldThrowExceptionWhenServerReurnError() {
        // given
        stubFor(post(urlEqualTo("/")).willReturn(aResponse().withStatus(500).withStatusMessage("Internal Server Error")));

        // when
        nanoAPI.execute(NanoRequest.builder().action("action").build(), Object.class);
    }

}