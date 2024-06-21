package ru.pavel;

import ru.pavel.net.InternetAddress;
import ru.pavel.net.InternetAddressException;

import java.io.IOException;
import java.util.Scanner;

public class InternetAddressApp {

    private final Scanner scanner = new Scanner(System.in);

    public void run() {

        boolean exitFlag = false;
        while (!exitFlag) {
            System.out.println("Введите два ip адреса через пробел:");
            String[] tokens = getCommandLineTokens();

            if (tokens == null || tokens.length != 2) {
                System.out.println("Не верный формат ввода, должно быть два ip адреса через пробел");
                continue;
            }

            //  Введены две лексемы
            try {
                var start = InternetAddress.of(tokens[0]);
                var end = InternetAddress.of(tokens[1]);

                //  определяем диапазон, начало и конец не включаем

                long count = start.untilCount(end, false, false);

                // Печать диапазона
                if (count > 0) {
                    System.out.println("Между этими адресами диапазон из " + count + " адресов:");
                    System.out.println("Введите p/P - если список адресов или любую клавишу для выхода:");

                    if (getPressedKey('p', 'P', 'р', 'Р')) {
                        //ArrayList<InternetAddress> range = start.until(end, false, false);
                        //range.forEach(System.out::println);
                        // Если нужно просто напечатать, нет смысла сохранять в списке
                        start.until(end, false, false, System.out::println);
                    }
                } else {
                    System.out.println("Между этими адресами нет диапазона");
                }
                exitFlag = true;
            } catch (InternetAddressException | IOException ex) {
                System.out.println(ex.getMessage());
            }
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

