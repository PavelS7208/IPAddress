package ru.pavel.net;

import java.util.Arrays;
import java.util.Objects;

import static ru.pavel.net.InternetAddressStringFormat.DECIMAL;
import static ru.pavel.net.InternetAddressStringFormat.HEX;

final public class InternetAddress {

    public static InternetAddress MAX_ADDRESS = InternetAddress.of("255.255.255.255");

    final int[] p = new int[4];
    final long decAddress;

    //  Создание из массива 4х целых чисел [192  168 0  5]
    public InternetAddress(int... address) {
        this.decAddress = InternetAddressUtils.intArrayToDec(address);
        System.arraycopy(address, 0, p, 0, 4);
    }

    //  Создание из строки 192.168.0.5
    public InternetAddress(String address) {
        int[] addr = InternetAddressUtils.decStringToIntArray(address);
        System.arraycopy(addr, 0, p, 0, 4);
        this.decAddress = InternetAddressUtils.intArrayToDec(p);
    }

        /*
    Публичные статические методы создания разными способами
    */
    //  Создание из строки 192.168.0.5
    public static InternetAddress of(String address) {
        return InternetAddress.parse(address, DECIMAL);
    }

    //  Создание из массива 4х целых чисел [192  168 0  5]
    public static InternetAddress of(int... address) {
        return new InternetAddress(address);
    }

    //  Создание из целого беззнакового
    public static InternetAddress of(long address) {
        int[] addr = InternetAddressUtils.decToIntArray(address);
        return new InternetAddress(addr);
    }



    //  Парсим введенную символьную строку адреса с учетом системы исчисления
    // radix = 10 16
    //  Или строка в виде десятичные цифры вида 192.168.0.5
    //   Или строка в виде 16ти ричного 8ми значного числа 0A064156
    public static InternetAddress parse(String address, InternetAddressStringFormat format) {

        int[] addr = switch (format) {
            case DECIMAL ->  InternetAddressUtils.decStringToIntArray(address);
            case HEX     -> InternetAddressUtils.hexStringToIntArray(address);
        };
        return new InternetAddress(addr);


    }


    // Выводим в одном из трех форматов
    //  строка с десятичными числами через точку  168.10.0.1
    // строка с десятичными числами через точку с ведущими нулями 168.010.000.001
    // строка с восьмиричныыми числами через точку с ведущими нулями 0300.0250.000.001
    public String format(InternetAddressStringFormat type) {
        return switch (type) {
            case DECIMAL -> String.format("%d.%d.%d.%d", p[0], p[1], p[2], p[3]);

            case HEX -> String.format("%8s", Long.toHexString(decAddress).toUpperCase());
        };
    }

    //  Получение массива чисел, 4 части адреса
    public int[] getAddress() {
        return Arrays.copyOf(p, p.length);
    }

    //  Получение массива чисел, 4 части адреса
    public long getDecAddress() {
        return decAddress;
    }

    public String getHexAddress() {
        return format(HEX) ;
    }

    @Override
    public String toString() {
        return format(DECIMAL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternetAddress address = (InternetAddress) o;
        return decAddress == address.decAddress;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(decAddress);
    }
}
