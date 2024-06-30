package ru.pavel.net;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class InternetAddressRange {

    final private InternetAddress start, end;
    final private boolean startIncluding, endIncluding;
    private long size;
    private long limit = Long.MAX_VALUE;


    public InternetAddressRange(InternetAddress start, InternetAddress end, boolean startIncluding, boolean endIncluding) {

        //  Проверки на возможность выдать диапазон,
        //  start <= end
        //  если нельзя - кидает исключения
        if (InternetAddressUtils.compare(start, end) > 0)
            throw new NoSuchElementException("Нельзя выдать диапазон, так как начальный адрес старше второго: " + start + " - " + end);

        this.start = start;
        this.end = end;
        this.startIncluding = startIncluding;
        this.endIncluding = endIncluding;

        calculateSize();
    }

    public InternetAddress getStart() {
        return start;
    }

    public InternetAddress getEnd() {
        return end;
    }

    public boolean isStartIncluding() {
        return startIncluding;
    }

    public boolean isEndIncluding() {
        return endIncluding;
    }

    public long size() {
        return size;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
    public long getLimit() {
        return limit;
    }

    public boolean isHasLimit() {
        return limit != Long.MAX_VALUE;
    }



    // Подсчет числа адресов диапазона
    private void calculateSize() {
        if (InternetAddressUtils.isEqual(start, end)) {
            size = (startIncluding || endIncluding) ? 1 : 0;
        } else {
            size = (end.getDecAddress() - start.getDecAddress() - 1);
            if (startIncluding) size++;
            if (endIncluding) size++;
        }
    }

    // Отдает List диапазона c учетом ограничения
    public List<InternetAddress> toList() {
        LinkedList<InternetAddress> list = new LinkedList<>();
        forEach(list::add);
        return list;
    }

    // Отдает stream c учетом ограничения
    public Stream<InternetAddress> stream() {
        LinkedList<InternetAddress> list = new LinkedList<>();
        forEach(list::add);
        return list.stream();
    }

    // Бежим по всему диапазону ip но с учетом ограничения на кол-во
    public void forEach(Consumer<InternetAddress> action) {

        // Если размер диапазона полный равен нулю и лимит не положительный, то и бежать не нужно
        if (size == 0 || limit < 1) return;

        long count = 0;
        if (startIncluding) {
            action.accept(start);
            count++;
        }

        for (long i = start.getDecAddress() + 1; (i < end.getDecAddress() && count<limit); i++, count++) {
            action.accept(InternetAddress.of(i));
        }
        //  если указано включать конец, включаем
        if (count<limit && endIncluding)
            action.accept(end);
    }

}

