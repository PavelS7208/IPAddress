package ru.pavel.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class InternetAddressUtilsTest {

    @Test
    @DisplayName("IsEquals Test")
    public void isEqual() {
        var address1 = InternetAddress.of(192, 168, 0, 1);
        var address2 = InternetAddress.of(192, 168, 0, 1);
        assertThat(InternetAddressUtils.isEqual(address1, address2)).isTrue();
    }

    @ParameterizedTest(name = "{0} больше {1}")
    @CsvSource({"192.168.0.2,192.168.0.1"})
    @DisplayName("CompareBiggest Test")
    public void compareBiggestTest(String address1, String address2) {
        assertThat(InternetAddressUtils.compare(InternetAddress.of(address1), InternetAddress.of(address2)) > 0).isTrue();
    }

    @ParameterizedTest(name = "{0} меньше {1}")
    @CsvSource({"192.168.0.1,192.168.0.100","192.168.0.1,195.168.0.1"})
    @DisplayName("CompareSmaller Test")
    public void compareSmallerTest(String address1, String address2) {
        assertThat(InternetAddressUtils.compare(InternetAddress.of(address1), InternetAddress.of(address2)) < 0).isTrue();
    }

    @ParameterizedTest(name = "{0} равен {1}")
    @CsvSource({"192.168.0.1,192.168.0.1"})
    @DisplayName("CompareEqual Test")
    public void compareEqualTest(String address1, String address2) {
        assertThat(InternetAddressUtils.compare(InternetAddress.of(address1), InternetAddress.of(address2)) == 0).isTrue();
    }

    @Test
    void decToIntArray() {

    }

    @Test
    void intArrayToDec() {
    }

    @Test
    void hexStringToIntArray() {
    }

    @Test
    void decStringToIntArray() {
    }
}