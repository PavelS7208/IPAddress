package ru.pavel.net;

public enum InternetAddressStringFormat {
    DECIMAL(10), HEX(16);
    private final int radix;

    private InternetAddressStringFormat(int radix) {
        this.radix = radix;
    }

    public int toDigit() {
        return radix;
    }
}
