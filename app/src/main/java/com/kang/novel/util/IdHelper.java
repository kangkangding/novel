package com.kang.novel.util;

/**
 *  2016/12/9.
 */

public class IdHelper {

    public static String getId(){
        java.util.Date date = new java.util.Date();
        int rand = (int)(1+ Math.random()*(25-0+1));
//        char c = (char) ('a' + rand);
        return String.valueOf(date.getTime()%100000000);

    }
}
