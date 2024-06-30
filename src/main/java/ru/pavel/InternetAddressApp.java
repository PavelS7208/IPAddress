package ru.pavel;

import ru.pavel.net.InternetAddress;
import ru.pavel.net.InternetAddressException;
import ru.pavel.net.InternetAddressRange;
import ru.pavel.net.InternetAddressUtils;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InternetAddressApp {

    private final Scanner scanner = new Scanner(System.in);

    public void run() {


        System.out.println("Введите два ip адреса через пробел:");
        String[] tokens = getCommandLineTokens();

        if (tokens == null || tokens.length != 2) {
            System.out.println("Не верный формат ввода, должно быть два ip адреса через пробел");
            return;
        }

        //  Введены две лексемы
        try {
            var start = InternetAddress.of(tokens[0]);
            var end = InternetAddress.of(tokens[1]);

            //  определяем диапазон, начало и конец включаем
            InternetAddressRange range = InternetAddressUtils.createIpAddressRange(start, end, true, true);
            // ограничим для примера миллионом записей чтобы не забить память
            range.setLimit(1000000L);
            // Печать диапазона
            long rangeSize = range.size();
            if (rangeSize > 0) {
                System.out.println("Между этими адресами диапазон из " + range.size() + " адресов:");
                System.out.println("Введите p/P - если напечатать список адресов (не более 300) или любую клавишу для выхода:");

                if (getPressedKey('p', 'P', 'р', 'Р')) {

                    if (rangeSize > 300) {
                        System.out.println("Список первых " + 300 + " значений ip диапазона (включая начало):");
                        // Через stream работаем
                        range.stream().limit(300).forEach(System.out::println);
                    } else {
                        System.out.println("Список значений ip диапазона (включая начало и конец):");
                        // или через forEach
                        range.forEach(System.out::println);
                    }
                }
            } else {
                System.out.println("Между этими адресами нет диапазона");
            }
        } catch (InternetAddressException | IOException | NoSuchElementException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private String[] getCommandLineTokens() {
        String[] tokens = null;
        if (scanner.hasNextLine()) {
            tokens = scanner.nextLine().split("\\s+");
        }
        return tokens;
    }

    private boolean getPressedKey(char... keys) throws IOException {
        char inputChar = (char) System.in.read();
        for (char ch : keys) {
            if (inputChar == ch) return true;
        }
        return false;
    }
}

