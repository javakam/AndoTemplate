package ando.library.widget.datetime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class DateTimePicker {

    //==============系统=================//
    public static Dialog showDatePickerDialog(Context context, int themeResId, final TextView tv, Calendar calendar) {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, themeResId
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                tv.setText(String.format(Locale.getDefault(),"您选择了：%d年%d月%d日", year, (monthOfYear + 1), dayOfMonth));
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        return datePickerDialog;
    }

    public static Dialog showTimePickerDialog(Context context, int themeResId, final TextView tv, Calendar calendar) {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(context
                , themeResId
                , new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tv.setText(String.format(Locale.getDefault(),"您选择了：%d时%d分", hourOfDay, minute));
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , false);
        timePickerDialog.show();
        return timePickerDialog;
    }

}