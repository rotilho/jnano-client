package com.rotilho.jnano.client.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import lombok.Value;

@Value
public final class AccountInformation {
    private final String frontier;
    @JsonProperty("open_block")
    private final String openBlock;
    @JsonProperty("representative_block")
    private final String representativeBlock;
    @JsonSerialize(using = ToStringSerializer.class)
    private final BigInteger balance;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("modified_timestamp")
    private final Long modifiedTimestamp;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("block_count")
    private final Long blockCount;
    private final String representative;
    @JsonSerialize(using = ToStringSerializer.class)
    private final BigInteger weight;
    @JsonSerialize(using = ToStringSerializer.class)
    private final BigInteger pending;

    @JsonIgnore
    public LocalDateTime getModifiedDateTime() {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(modifiedTimestamp),
                TimeZone.getDefault().toZoneId()
        );
    }
}