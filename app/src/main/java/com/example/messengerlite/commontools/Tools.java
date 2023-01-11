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

public class Tools
{
    private static final long A_DAY = 86_400_000;

    public static final String DOMAIN = "http://192.168.0.131:8080";
    public static final String IP_AND_PORT = "192.168.0.131:8080";

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

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        return "Ng. " + calendar.get(Calendar.DAY_OF_MONTH) + ", Th. " +
                (calendar.get(Calendar.MONTH) + 1) + ", " + calendar.get(Calendar.YEAR);
    }

    public static String fromToday(Date date)
    {
        Date now = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        if(now.getTime() - date.getTime() < A_DAY)
            return printTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        else
            return convertToReadable(date) + ", " +
                    printTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
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
