package com.ece1778.musego.Utils;

import com.ece1778.musego.Model.Path;

import java.util.Comparator;

public class PopularCompareUtil implements Comparator {


    @Override
    public int compare(Object o1, Object o2) {

        Path path1 = (Path) o1;
        Path path2 = (Path) o2;

        return path2.getLikeList().size() - path1.getLikeList().size();
    }
}
