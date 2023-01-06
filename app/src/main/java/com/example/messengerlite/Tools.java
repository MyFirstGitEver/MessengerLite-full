package com.example.messengerlite;

import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Tools
{
    public static final String DOMAIN = "http://192.168.0.131:8080";

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            LocalDate dateContainer = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return "Ng. " + dateContainer.getDayOfMonth() + ", Th. " +
                    dateContainer.getMonthValue()  + ", " + dateContainer.getYear();
        }

        return "Date is not supported in your device";
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
