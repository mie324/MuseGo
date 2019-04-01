package com.ece1778.musego.Utils;

import com.ece1778.musego.Model.Path;

import java.util.Comparator;

public class ShortToLongCompareUtil implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        Path p1 = (Path) o1;
        Path p2 = (Path) o2;

        return convert(p1.getEstimated_time()) - convert(p2.getEstimated_time());
    }

    public int convert(String time){

        String[] times = time.split("/");

        return Integer.parseInt(times[0].trim())*60 + Integer.parseInt(times[1].trim());

    }
}


