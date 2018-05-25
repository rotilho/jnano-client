package com.rotilho.jnano.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class HttpMock extends WireMockRule {


    public HttpMock() {
        super(options().dynamicPort());
    }

    public NanoAPI getNanoAPI() {
        return NanoAPI.builder().endpoint("http://localhost:" + port() + "/").build();
    }

    public void mock(String request, String response) {
        stubFor(post(urlEqualTo("/"))
                .withRequestBody(equalToJson(request))
                .willReturn(aResponse().withBody(response)));
    }
}
