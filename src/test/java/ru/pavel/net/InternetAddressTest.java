package ru.pavel.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("InternetAddress Test")
public class InternetAddressTest {

    @Test
    public void createByDigitCheck() {
        var address1 = InternetAddress.of(192, 168, 0, 1);
        assertEquals(address1.toString(), "192.168.0.1");
    }

    @Test
    public void createByArrayDigitCheck2() {

        var addr3 = InternetAddress.of(1, 10, 0, 1);
        assertEquals(addr3.toString(), "1.10.0.1");
    }

    @Test
    public void createByLiteralParseCheck() {
        var addr1 = InternetAddress.of(" 192.168.1.5 ");
        assertEquals(addr1.toString(), "192.168.1.5");
    }


    @ParameterizedTest (name="Строка {0} по системе {1}  распарсилась успешно")
    @CsvSource({" 192.168.0.1,DECIMAL,192.168.0.1","1.010.0.1,DECIMAL,1.10.0.1","C00022EB,HEX,192.0.34.235"})
    @DisplayName("CreateByLiteralParse Test")
    void createByLiteralParse(String inputString, InternetAddressStringFormat type, String expectedString) {
        var address = InternetAddress.parse(inputString, type);
        assertEquals(address.toString(), expectedString);
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



    @ParameterizedTest (name="В строке {0} по системе {1}  ошибка обнаружена успешно")
    @CsvSource({"192.600.1.5,DECIMAL","192.6.1 .5,DECIMAL","C00022EK,HEX","000022EB,HEX"})
    @DisplayName("LiteralParseError Test")
    public void literalParseError(String inputString, InternetAddressStringFormat type) {
        String expectedMessage = "Неверный формат ip адреса";

        Exception exception1 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.parse(inputString, type));
        assertTrue(exception1.getMessage().contains(expectedMessage));

    }

    @ParameterizedTest(name="При создании из массива {index} ip-адреса ошибка обнаружена успешно")
    @CsvSource({"[1, 10, 0]","[1, 10, 0, 456]","[192, 198, 0, 1,1 ]"})
    @DisplayName("IntArrayCreateError Test")
    public void intArrayCreateError(String arrayAsString) {
        String expectedMessage = "Неверный формат ip адреса";

        int[] addressArray = Arrays.stream(arrayAsString
                                            .replace("[", "")
                                            .replace("]", "")
                                            .split(","))
                .mapToInt(c -> Integer.parseInt(c.trim()))
                .toArray();


        Exception exception5 = assertThrows(
                InternetAddressException.class,
                () -> InternetAddress.of(addressArray));
        assertTrue(exception5.getMessage().contains(expectedMessage));

    }


    @ParameterizedTest
    @CsvSource({"1.10.0.1,DECIMAL,1.10.0.1","192.168.1.5,HEX,C0A80105"})
    @DisplayName("toStringCheck Test")
    public void toStringCheck(String inputString, InternetAddressStringFormat type, String expectedString) {
        var address = InternetAddress.of(inputString);
        assertEquals(address.format(type), expectedString);
    }


    @Test
    public void comparatorCheck() {
        var addr1 = InternetAddress.of("192.168.1.5");
        var addr2 = InternetAddress.of("192.167.2.6");

        InternetAddressComparator comp = new InternetAddressComparator();

        assertEquals(1, comp.compare(addr1, addr2));
    }
/*
    @Test
    public void rangeCheckError() {
        String expectedMessage = "начальный адрес старше";

        // Первый адрес старше конца
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
    */
/*
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
*/
}