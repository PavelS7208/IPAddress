package ru.pavel.net;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.pavel.net.InternetAddressStringRadix.*;


public class InternetAddressTest {

    @Test
    public void createByDigitCheck() {
        var address1 = InternetAddress.of(192, 168, 0, 1);
        assertEquals(address1.toString(), "192.168.0.1");

        int[] a = new int[]{1, 10, 0, 1};
        var addr3 = InternetAddress.of(a);
        assertEquals(addr3.toString(), "1.10.0.1");

    }

    @Test
    public void createByLiteralParseCheck() {

        var addr1 = InternetAddress.of(" 192.168.1.5 ");
        assertEquals(addr1.toString(), "192.168.1.5");

        var addr2 = InternetAddress.parse("1.010.0.1", DECIMAL);
        assertEquals(addr2.toString(), "1.10.0.1");

        var addr3 = InternetAddress.parse("0300.0250.0.1", OCTAL);
        assertEquals(addr3.toString(), "192.168.0.1");

        var addr4 = InternetAddress.parse("0xC0.0x00.22.0XEB", HEX);
        assertEquals(addr4.toString(), "192.0.34.235");
    }

    @Test
    public void getDigitArray() {
        var addr1 = InternetAddress.of("192.168.1.5");
        assertArrayEquals(addr1.getAddress(), new int[]{192, 168, 1, 5});
    }

    @Test
    public void maxAddressCheck() {
        assertEquals(InternetAddress.MAX_ADDRESS.toString(), "255.255.255.255");
    }

    @Test
    public void literalParseError() {
        String expectedMessage = "Неверный формат ip адреса";

        Exception exception1 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.parse("192.600.1.5", DECIMAL));
        assertTrue(exception1.getMessage().contains(expectedMessage));

        Exception exception2 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.parse("192.6.1 .5", DECIMAL));
        assertTrue(exception2.getMessage().contains(expectedMessage));

        Exception exception3 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.parse("0xC0.0x00.22.0xEK", HEX));
        assertTrue(exception3.getMessage().contains(expectedMessage));

        Exception exception4 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.of(new int[]{1, 10, 0}));
        assertTrue(exception4.getMessage().contains(expectedMessage));

        Exception exception5 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.of(new int[]{1, 10, 0, 456}));
        assertTrue(exception5.getMessage().contains(expectedMessage));


    }


    @Test
    public void toStringCheck() {
        var addr1 = InternetAddress.of("192.168.1.5");
        assertEquals(addr1.format(InternetAddressPrintFormat.ZERO_FILLED), "192.168.001.005");

        var addr2 = InternetAddress.of("192.168.1.5");
        assertEquals(addr2.format(InternetAddressPrintFormat.OCTAL), "0300.0250.0001.0005");

    }

    @Test
    public void comparatorCheck() {
        var addr1 = InternetAddress.of("192.168.1.5");
        var addr2 = InternetAddress.of("192.167.2.6");

        InternetAddressComparator comp = new InternetAddressComparator();

        assertEquals(1, comp.compare(addr1, addr2));
    }

    @Test
    public void rangeCheckError() {
        String expectedMessage = "начальный адрес старше";

        // Первый адресс старше конца
        Exception exception = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.of("192.168.1.254")
                        .until(InternetAddress.of("190.168.2.9"), true, false));
        assertTrue(exception.getMessage().contains(expectedMessage));

    }

    @Test
    public void rangeCheck() {
        var range1 = InternetAddress.of("192.168.1.254")
                .until(InternetAddress.of("192.168.2.8"), true, false);
        assertEquals(range1.size(), 10);

        var range2 = InternetAddress.of("192.255.255.254")
                .until(InternetAddress.of("193.0.0.8"), false, true);
        assertEquals(range2.size(), 10);

        var range3 = InternetAddress.of("192.255.255.254")
                .until(InternetAddress.of("193.0.0.8"), false, false);
        assertEquals(range3.size(), 9);

        var range4 = InternetAddress.MAX_ADDRESS
                .until(InternetAddress.MAX_ADDRESS, false, false);
        assertEquals(range4.size(), 0);

        var range5 = InternetAddress.MAX_ADDRESS
                .until(InternetAddress.MAX_ADDRESS, true, false);
        assertEquals(range5.getFirst().toString(), "255.255.255.255");
    }

    @Test
    public void countRangeCheck() {
        var range1 = InternetAddress.of("192.168.1.254")
                .untilCount(InternetAddress.of("192.168.2.8"), true, false);
        assertEquals(range1, 10);

        var range2 = InternetAddress.of("192.255.255.254")
                .untilCount(InternetAddress.of("193.0.0.8"), false, true);
        assertEquals(range2, 10);

        var range3 = InternetAddress.of("192.255.255.254")
                .untilCount(InternetAddress.of("193.0.0.8"), false, false);
        assertEquals(range3, 9);

        var range4 = InternetAddress.MAX_ADDRESS
                .untilCount(InternetAddress.MAX_ADDRESS, false, false);
        assertEquals(range4, 0);

        var range5 = InternetAddress.MAX_ADDRESS
                .untilCount(InternetAddress.MAX_ADDRESS, true, false);
        assertEquals(range5, 1);

        var range6 = InternetAddress.of("192.168.1.254")
                .untilCount(InternetAddress.of("193.168.2.8"), false, false);
        assertEquals(range6, 256L * 256L * 256L + 10 - 1);
    }

}