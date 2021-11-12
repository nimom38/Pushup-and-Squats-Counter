package com.example.android.getfit;

import org.joda.time.DateTime;

public class TimeUtils {
    public static String getDay() {
        DateTime dt = new DateTime();
        DateTime.Property pDoW = dt.dayOfWeek();
        String strST = pDoW.getAsShortText(); // returns "Mon", "Tue", etc.
//        String strT = pDoW.getAsText(); // returns "Monday", "Tuesday", etc.
        return strST;
    }

    public static String getMonth() {
        DateTime dt = new DateTime();
        String month = dt.monthOfYear().getAsText();
        return month;
    }

    public static  String getDate() {
        DateTime dt = new DateTime();
        String date = dt.dayOfMonth().getAsText();
        return date;
    }

    public static String getYear() {
        DateTime dt = new DateTime();
        Integer year = dt.getYear();
        return year.toString();
    }

    public static String getTime() {
        DateTime dt = new DateTime();
        Integer hour = dt.getHourOfDay();
        Integer min = dt.getMinuteOfHour();
        String ans = "";
        if( hour >= 12 ) {
            if( hour > 12 ) hour -= 12;
            ans += hour.toString();
            ans += ":";
            if( min.toString().length() == 1 ) ans += "0" + min.toString();
            else ans += min.toString();
            ans += "pm";
        }
        else {
            if(hour == 0) hour = 12;
            ans += hour.toString();
            ans += ":";
            if( min.toString().length() == 1 ) ans += "0" + min.toString();
            else ans += min.toString();
            ans += "am";
        }
        return ans;
    }
}
