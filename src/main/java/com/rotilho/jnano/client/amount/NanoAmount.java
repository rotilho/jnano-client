package com.rotilho.jnano.client.amount;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NanoAmount {
    @NonNull
    private final BigDecimal raw;

    public static NanoAmount of(@NonNull BigDecimal amount, @NonNull NanoUnit unit) {
        BigDecimal raw = amount.multiply(unit.getMultiplier());
        if (raw.signum() < 0) {
            throw new IllegalArgumentException("Amount(" + amount + ") can't be negative");
        }
        if (raw.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Amount(" + amount + ") have raw decimals");
        }
        return new NanoAmount(raw);
    }

    public static NanoAmount of(@NonNull String amount, @NonNull NanoUnit unit) {
        return of(new BigDecimal(amount), unit);
    }

    public static NanoAmount ofGiga(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.GIGA);
    }

    public static NanoAmount ofGiga(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.GIGA);
    }

    public static NanoAmount ofMega(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.MEGA);
    }

    public static NanoAmount ofMega(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.MEGA);
    }

    public static NanoAmount ofKilo(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.KILO);
    }

    public static NanoAmount ofKilo(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.KILO);
    }

    public static NanoAmount ofNano(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.NANO);
    }

    public static NanoAmount ofNano(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.NANO);
    }

    public static NanoAmount ofMilli(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.MILLI);
    }

    public static NanoAmount ofMilli(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.MILLI);
    }

    public static NanoAmount ofMicro(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.MICRO);
    }

    public static NanoAmount ofMicro(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.MICRO);
    }

    public static NanoAmount ofRaw(@NonNull BigDecimal amount) {
        return NanoAmount.of(amount, NanoUnit.RAW);
    }

    public static NanoAmount ofRaw(@NonNull String amount) {
        return NanoAmount.of(amount, NanoUnit.RAW);
    }

    public NanoAmount add(NanoAmount amount) {
        return NanoAmount.ofRaw(this.raw.add(amount.raw));
    }

    public NanoAmount subtract(@NonNull NanoAmount amount) {
        return NanoAmount.ofRaw(this.raw.subtract(amount.raw));
    }

    public NanoAmount multiply(@NonNull NanoAmount amount) {
        return NanoAmount.ofRaw(this.raw.multiply(amount.raw));
    }

    public NanoAmount divide(@NonNull NanoAmount amount, @NonNull RoundingMode roundingMode) {
        return NanoAmount.ofRaw(this.raw.divide(amount.raw, roundingMode));
    }

    public BigDecimal to(@NonNull NanoUnit unit) {
        return raw.divide(unit.getMultiplier());
    }

    public BigDecimal toGiga() {
        return to(NanoUnit.GIGA);
    }

    public BigDecimal toMega() {
        return to(NanoUnit.MEGA);
    }

    public BigDecimal toKilo() {
        return to(NanoUnit.KILO);
    }

    public BigDecimal toNano() {
        return to(NanoUnit.NANO);
    }

    public BigDecimal toMilli() {
        return to(NanoUnit.MILLI);
    }

    public BigDecimal toMicro() {
        return to(NanoUnit.MICRO);
    }

    public BigDecimal toRaw() {
        return raw;
    }

    public String toString(@NonNull NanoUnit unit) {
        return raw.multiply(unit.getMultiplier()).toString();
    }

    public String toString() {
        return toString(NanoUnit.RAW);
    }
}
