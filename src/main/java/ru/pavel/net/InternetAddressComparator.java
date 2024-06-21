package ru.pavel.net;

import java.util.Comparator;

public class InternetAddressComparator implements Comparator<InternetAddress> {


    @Override
    public int compare(InternetAddress address1, InternetAddress address2) {
        int[] p1 = address1.getAddress();
        int[] p2 = address2.getAddress();
        for (int i = 0; i < 4; i++) {
            if (p1[i] != p2[i])
                return Integer.compare(p1[i], p2[i]);
        }
        return 0;
    }
}
