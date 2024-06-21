package ru.pavel.net;

public enum InternetAddressStringRadix {
    DECIMAL(10), OCTAL(8), HEX(16);

    private final int radix;
    private InternetAddressStringRadix(int radix) {
        this.radix = radix;
    }
    public int toDigit() {
        return radix;
    }
}
