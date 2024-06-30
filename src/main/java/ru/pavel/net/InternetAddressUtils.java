package ru.pavel.net;

public class InternetAddressUtils {

    public static boolean isEqual(InternetAddress address1, InternetAddress address2) {
        return address1.equals(address2);
    }

    public static int compare(InternetAddress address1, InternetAddress address2) {
        return new InternetAddressComparator().compare(address1, address2);
    }

    public static boolean isMaxAddress(InternetAddress address) {
        return address.equals(InternetAddress.MAX_ADDRESS);
    }

    public static int[] decToIntArray(long decAddress) {
        int[] addr = new int[4];
        addr[0] = (int) (decAddress >> 24);
        addr[1] = (int) (decAddress >> 16 & 0x00000000000000FF);
        addr[2] = (int) (decAddress >> 8 & 0x00000000000000FF);
        addr[3] = (int) (decAddress & 0x00000000000000FF);
        if (addr[0] == 0) {
            throw new InternetAddressException("Неверный формат ip адреса. В строке должно быть положительное 10тичное число от 16 777 216L до 4 294 967 295L");
        }
        return addr;
    }

    public static long intArrayToDec(int... address) {
        if (address.length != 4) {
            throw new InternetAddressException("Неверный формат ip адреса. Должно быть 4 разряда вида x.x.x.x.");
        }
        for (int i = 0; i < 4; i++) {
            int digit = address[i];
            if (digit < 0 || digit > 255) {
                throw new InternetAddressException("Неверный формат ip адреса. В " + (i + 1) + " разряде стоит " + digit + ", должны быть цифры от 0 до 255.");
            }
        }
        if (address[0] == 0) {
            throw new InternetAddressException("Неверный формат ip адреса. В первом разряде не может быть 0");
        }

        return (address[3] + 256L * address[2] + 256L * 256L * address[1] + 256L * 256L * 256L * address[0]);
    }

    public static int[] hexStringToIntArray(String address) {
        if (address.length() != 8) {
            throw new InternetAddressException("Неверный формат ip адреса. В строке должно быть 8-ми значное 16ти ричное число");
        }
        try {
            int[] addr = InternetAddressUtils.decToIntArray(Long.parseLong(address, 16));

            if (addr[0] == 0) {
                throw new InternetAddressException("Неверный формат ip адреса. В первом разряде не может быть 0");
            }
            return addr;
        } catch (NumberFormatException ex) {
            throw new InternetAddressException("Неверный формат ip адреса:" + address + ". Не 16-ти ричное число", ex);
        }
    }

    public static int[] decStringToIntArray(String address) {
        String[] ips = address.trim().split("\\.");
        if (ips.length != 4) {
            throw new InternetAddressException("Неверный формат ip адреса. В строке должно быть 4 разряда вида x.x.x.x");
        }
        int[] addr = new int[4];
        for (int i = 0; i < 4; i++) {
            try {
                addr[i] = Integer.parseInt(ips[i], 10);
            } catch (NumberFormatException ex) {
                throw new InternetAddressException("Неверный формат ip адреса:" + address + ". Не цифра в " + (i + 1) + " разряде.", ex);
            }
        }
        return addr;
    }


    /*
    Возвращает обертку над диапазоном ip адресов
    start - начальный адрес
    end - конечный адрес
    startIncluding, endIncluding - включать или нет в диапазон начальный и конечный адреса.
    Бросает исключение NoSuchElementException  - если start больше end
     */
    public static InternetAddressRange createIpAddressRange(InternetAddress start, InternetAddress end, boolean startIncluding, boolean endIncluding) {
        return new InternetAddressRange(start, end, startIncluding, endIncluding);
    }


    // Подсчет числа адресов диапазона
    // end - конечный адрес
    // startIncluding, endIncluding - включать или нет в диапазон начальный и конечный адреса.
    // Бросает исключение NoSuchElementException  - если start больше end
    public static long internetAddressRangeSize(InternetAddress start, InternetAddress end, boolean startIncluding, boolean endIncluding) {
        return new InternetAddressRange(start, end, startIncluding, endIncluding).size();
    }


}

