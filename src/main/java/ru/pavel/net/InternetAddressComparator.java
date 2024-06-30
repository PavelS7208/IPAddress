package ru.pavel.net;

import java.util.Comparator;

public class InternetAddressComparator implements Comparator<InternetAddress> {


    @Override
    public int compare(InternetAddress address1, InternetAddress address2) {
        long p1 = address1.getDecAddress();
        long p2 = address2.getDecAddress();
        return Long.compare(p1, p2);
    }
}
