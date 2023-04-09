package com.example.messengerlite.commontools;

import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Tools
{
    private static final long A_DAY = 86_400_000;

    public static final String DOMAIN = "http://:8081/";
    public static final String IP_AND_PORT = ":8081";

    public static Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public static String convertToReadable(Date date)
    {
        if(date == null)
            return "Trống";

        return "Ng. " + date.getDate() + ", Th. " +
                (date.getMonth() + 1) + ", " + (date.getYear() + 1900);
    }

    public static String fromToday(Date date)
    {
        Date now = new Date();

        if(now.getDate() == date.getDate() && now.getTime() - date.getTime() < A_DAY)
            return printTime(date.getHours(), date.getMinutes());
        else
            return convertToReadable(date) + ", " +
                    printTime(date.getHours(), date.getMinutes());
    }

    public static String printTime(int hour, int min)
    {
        return twoDigitFormat(hour) + ":" + twoDigitFormat(min);
    }

    public static String twoDigitFormat(int num)
    {
        if(num < 10)
            return "0" + num;
        else
            return Integer.toString(num);
    }

    public static String convertToddmmyy(Date date)
    {
        if(date == null)
            return "01/01/1970";

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        return formatter.format(date);
    }

    public static Date reConvertddmmyy(String str)
    {
        try
        {
            return new SimpleDateFormat("dd/MM/yyyy").parse(str);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
