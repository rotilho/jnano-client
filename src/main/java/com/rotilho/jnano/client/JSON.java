package com.rotilho.jnano.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.rotilho.jnano.commons.NanoAmount;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;

import lombok.NonNull;

public final class JSON {
    private static final SimpleModule customDeserializer = new SimpleModule()
            .addDeserializer(NanoAmount.class, new NanoAmountDeserializer())
            .addSerializer(NanoAmount.class, new ToStringSerializer(NanoAmount.class));
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(customDeserializer)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

    private JSON() {
    }

    @NonNull
    public static <T> T parse(@NonNull String json, @NonNull Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NonNull
    public static <T> String stringify(@NonNull T object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class NanoAmountDeserializer extends StdDeserializer<NanoAmount> {
        protected NanoAmountDeserializer() {
            super(NanoAmount.class);
        }

        @Override
        public NanoAmount deserialize(JsonParser p, DeserializationContext context) throws IOException {
            BigDecimal amount = getValue(p);
            return NanoAmount.ofRaw(amount);
        }

        private BigDecimal getValue(JsonParser p) throws IOException {
            switch (p.getCurrentTokenId()) {
                case JsonTokenId.ID_NUMBER_INT:
                case JsonTokenId.ID_NUMBER_FLOAT:
                    return p.getDecimalValue();
                default:
                    return new BigDecimal(p.getText().trim());
            }
        }
    }
}
