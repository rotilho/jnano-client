package com.rotilho.jnano.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NanoAPIException extends RuntimeException {
    private final NanoRequest request;
    private final String error;
    private final String message;
}
