package ru.pavel.net;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InternetAddressRange Test")
class InternetAddressRangeTest {


    @ParameterizedTest(name = "От {0} до {1} концы [{2},{3}] равно {4}")
    @CsvSource({"192.168.0.1,192.168.0.10,true,true,10", "192.168.0.1,192.168.0.10,false,false,8", "192.169.0.1,192.170.0.1,false,false,65535" })
    @DisplayName("internetAddressRangeSize Test")
    public void internetAddressRangeSizeTest(String address1, String address2, boolean startIncluding, boolean endIncluding, long size) {

        var start = InternetAddress.of(address1);
        var end = InternetAddress.of(address2);

        assertThat(InternetAddressUtils.internetAddressRangeSize(start, end, startIncluding, endIncluding)).isEqualTo(size);
    }

    @ParameterizedTest(name = "От {0} до {1} с лимитом {2} размер диапазона {3} последнее значение диапазона равно {4}")
    @CsvSource({"192.168.0.1,192.169.0.1,1,1,192.168.0.2", "192.168.0.1,192.169.0.1,1000000,65535,192.169.0.0"})
    @DisplayName("InternetAddressRangeLimit Test")
    public void internetAddressRangeLimitTest(String address1, String address2, long limit, long size, String expectedString){

        var start = InternetAddress.of(address1);
        var end = InternetAddress.of(address2);

        //  определяем диапазон, начало и конец включаем
        InternetAddressRange range = InternetAddressUtils.createIpAddressRange(start, end, false, false);
        range.setLimit(limit);
        assertThat(range.stream().count()).as("check limit size").isEqualTo(size);
        assertThat(range.stream().toList().getLast().toString()).as("check last string").isEqualTo(expectedString);
    }


    @ParameterizedTest(name = "От {0} до {1} содержит {2} и не содержит {3}")
    @CsvSource({"192.168.0.1,192.169.0.1,192.168.0.2,192.169.0.1", "255.255.255.253,255.255.255.255,255.255.255.254,255.255.255.255"})
    @DisplayName("InternetAddressRangeContains Test")
    public void internetAddressRangeContainsTest(String address1, String address2, String containAddress, String doesNotContainAddress){

        var start = InternetAddress.of(address1);
        var end = InternetAddress.of(address2);

        //  определяем диапазон, начало и конец включаем
        InternetAddressRange range = InternetAddressUtils.createIpAddressRange(start, end, false, false);
        //range.setLimit();
        assertThat(range.toList()).extracting(InternetAddress::toString).contains(containAddress).doesNotContain(doesNotContainAddress);
    }

}