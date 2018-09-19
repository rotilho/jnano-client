package com.rotilho.jnano.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Map;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Value
@Builder
public class NanoRequest implements NanoAPIAction {
    @NonNull
    private final String action;
    @Singular
    private final Map<String, Object> params;

    @JsonAnyGetter
    public Map<String, Object> getParams() {
        return params;
    }
}
