package ru.pavel.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static ru.pavel.net.InternetAddressStringRadix.DECIMAL;

final public class InternetAddress {

    public static InternetAddress MAX_ADDRESS = new InternetAddress(new int[]{255, 255, 255, 255});
    final int[] p = new int[4];

    private InternetAddress(int[] address) {
        System.arraycopy(address, 0, p, 0, address.length);
    }

    /*
    Публичные статические методы создания разными способами
    */
    public static InternetAddress of(String address) {
        return parse(address, DECIMAL);
    }

    public static InternetAddress of(int p1, int p2, int p3, int p4) {
        return create(new int[]{p1, p2, p3, p4});

    }

    public static InternetAddress of(int[] address) {
        return create(address);
    }

    public static InternetAddress parse(String address, InternetAddressStringRadix radix) {
        return create(address, radix);
    }

    /*
        Внутренние методы create c проверками
    */
    //  Парсим переданные 10тичные числа
    private static InternetAddress create(int[] address) {
        if (address.length != 4) {
            throw new InternetAddressException("Неверный формат ip адреса. Должно быть 4 разряда.");
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
        return new InternetAddress(address);
    }

    //  Парсим введенную символьную строку адреса с учетом системы исчисления
    // radix = 8 10 16
    private static InternetAddress create(String address, InternetAddressStringRadix r) throws InternetAddressException {

        int radix = r.toDigit();
        String[] ips = address.trim().split("\\.");
        if (ips.length != 4) {
            throw new InternetAddressException("Неверный формат ip адреса. Должно быть 4 разряда вида x.x.x.x");
        }
        int[] addr = new int[4];
        for (int i = 0; i < 4; i++) {
            try {
                String s = ips[i];
                if (radix == 16 && (s.length() == 4 && s.charAt(0) == '0' && (s.charAt(1) == 'x' || s.charAt(1) == 'X')))
                    s = s.substring(2);
                int digit = Integer.parseInt(s, radix);
                if (digit < 0 || digit > 255) {
                    throw new InternetAddressException("Неверный формат ip адреса:" + address + ". В " + (i + 1) + " разряде стоит " + digit + ",  должны быть цифры от 0 до 255.");
                }
                addr[i] = digit;
            } catch (NumberFormatException ex) {
                throw new InternetAddressException("Неверный формат ip адреса:" + address + ". Ошибка в цифре в " + (i + 1) + " разряде.", ex);
            }
        }
        return new InternetAddress(addr);
    }

    // Выводим в одном из трех форматов
    //  строка с десятичными числами через точку  168.10.0.1
    // строка с десятичными числами через точку с ведущими нулями 168.010.000.001
    // строка с восьмиричныыми числами через точку с ведущими нулями 0300.0250.000.001
    public String format(InternetAddressPrintFormat type) {
        return switch (type) {
            case CLASSIC -> String.format("%d.%d.%d.%d", p[0], p[1], p[2], p[3]);

            case ZERO_FILLED -> String.format("%03d.%03d.%03d.%03d", p[0], p[1], p[2], p[3]);
            case OCTAL -> String.format("%4s", Integer.toString(p[0], 8)).replace(' ', '0') + "." +
                    String.format("%4s", Integer.toString(p[1], 8)).replace(' ', '0') + "." +
                    String.format("%4s", Integer.toString(p[2], 8)).replace(' ', '0') + "." +
                    String.format("%4s", Integer.toString(p[3], 8)).replace(' ', '0');

        };
    }

    //  Получение массива чисел, 4 части адреса
    public int[] getAddress() {
        return Arrays.copyOf(p, p.length);
    }

    @Override
    public String toString() {
        return format(InternetAddressPrintFormat.CLASSIC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternetAddress address = (InternetAddress) o;
        return Objects.deepEquals(p, address.p);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(p);
    }

    /*
    Возвращает сортированный список диапазонов адресов до указанного адреса
    end - конечный адрес
    startIncluding, endIncluding  - включать или нет в диапазон начальный и конечный адреса

    Бросает InternetAddressException если конец меньше начала
     */
    public ArrayList<InternetAddress> until(InternetAddress end, boolean startIncluding, boolean endIncluding) throws InternetAddressException {

        //  Проверки на возможность выдать диапазон, если нельзя - кидает исключения
        rangeCheck(end);

        ArrayList<InternetAddress> range = new ArrayList<>();
        //  Если начало и конец равны, или начало максимально возможный,
        //  то диапазон или пусто или один элемент, если указано, что концы включать
        if (this.equals(end) || this.equals(MAX_ADDRESS)) {
            if (startIncluding || endIncluding) range.add(this);
            return range;
        }

        //  если указано включать начало, включаем
        if (startIncluding)
            range.add(this);
        // Бежим от начала до конца
        InternetAddress iterator = this.nextAddress();
        while (!iterator.equals(end)) {
            range.add(iterator);
            iterator = iterator.nextAddress();
        }
        //  если указано включать конец, включаем
        if (endIncluding)
            range.add(end);

        return range;
    }

    /*
    Делает указанное действие над каждым элементом списка диапазонов адресов до указанного адреса
    end - конечный адрес
    startIncluding, endIncluding  - включать или нет в диапазон начальный и конечный адреса
    action - действие-метод

    Бросает InternetAddressException если конец меньше начала
     */
    public void until(InternetAddress end, boolean startIncluding, boolean endIncluding, Consumer<? super InternetAddress> action) throws InternetAddressException {

        //  Проверки на возможность выдать диапазон, если нельзя - кидает исключения
        rangeCheck(end);

        //  Если начало и конец равны, или начало максимально возможный,
        //  то диапазон или пусто или один элемент, если указано, что концы включать
        if (this.equals(end) || this.equals(MAX_ADDRESS)) {
            if (startIncluding || endIncluding) action.accept(this);
            return;
        }

        //  если указано включать начало, включаем
        if (startIncluding)
            action.accept(this);
        // Бежим от начала до конца
        InternetAddress iterator = this.nextAddress();
        while (!iterator.equals(end)) {
            action.accept(iterator);
            iterator = iterator.nextAddress();
        }
        //  если указано включать конец, включаем
        if (endIncluding)
            action.accept(end);

    }




    // Подсчет числа адресов диапазона
    // end - конечный адрес
    // startIncluding, endIncluding  - включать или нет в диапазон начальный и конечный адреса
    //
    //Бросает InternetAddressException если конец меньше начала
    public long untilCount(InternetAddress end, boolean startIncluding, boolean endIncluding) throws InternetAddressException {
        //  Проверки на возможность выдать диапазон
        rangeCheck(end);

        //  Если начало и конец равны, или начало максимально возможный,
        //  то диапазон или пусто или один элемент, если указано, что концы включать
        long count = 0;
        if (this.equals(end) || this.equals(MAX_ADDRESS))
            return (startIncluding || endIncluding) ? 1 : 0;

        // "вычитаем" из конца начало
        int[] v = this.getAddress();
        int[] u = end.getAddress();
        int[] res = new int[]{0, 0, 0, 0};
        boolean minusFlag = false;
        for (int i = 3; i >= 0; i--) {
            int u1 = u[i] - (minusFlag ? 1 : 0);
            if (u1 >= v[i]) {
                res[i] = u1 - v[i];
                minusFlag = false;
            } else {
                res[i] = 256 + u1 - v[i];
                minusFlag = true;
            }
        }
        // Переводим в 10тичное
        count += (res[3] + 256L * res[2] + 256L * 256L * res[1] + 256L * 256L * 256L * res[0] - 1);

        if (startIncluding) count++;
        if (endIncluding) count++;

        return count;
    }

    private void rangeCheck(InternetAddress end) throws InternetAddressException {
        //  Если начало больше конца, то диапазон невозможно выдать
        InternetAddressComparator comparator = new InternetAddressComparator();
        if (comparator.compare(this, end) > 0)
            throw new InternetAddressException("Нельзя выдать диапазон, так как начальный адрес старше второго:" + this + "-" + end);
    }


    //  Выдает следующий по старшинству адрес
    // Бросает InternetAddressException если адрес максимальный 255.255.255.255
    private InternetAddress nextAddress() {

        if (this.equals(MAX_ADDRESS))
            throw new InternetAddressException("Адрес 255.255.255.255 максимально возможный, нельзя взять следующий");

        int[] addressPlusOne = this.getAddress();
        boolean addFlag = false;
        for (int i = 3; i >= 0; i--) {
            int newDigit = addressPlusOne[i] + (i == 3 ? 1 : 0) + (addFlag ? 1 : 0);
            if (newDigit == 256) {
                addressPlusOne[i] = 0;
                addFlag = true;
            } else {
                addressPlusOne[i] = newDigit;
                break;
            }
        }
        return new InternetAddress(addressPlusOne);
    }

}
