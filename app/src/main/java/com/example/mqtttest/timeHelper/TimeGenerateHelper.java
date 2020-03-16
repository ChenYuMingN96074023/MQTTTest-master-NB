package com.example.mqtttest.timeHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeGenerateHelper {
    public String getTime(){
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));//此方法將導致只固定產生+8時區的時間，而非手機設定或當地時區自動產生的時間
        String time = dff.format(new Date());
        return time;
    }

    public String formatTheTime(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //傳入引數的format格式
        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm"); //輸出結果的format格式
        try
        {
            Date date = sdf.parse(str.trim());
            return timeOnly.format(date);
        } catch (ParseException e)
        {
            return "";
        }
    }
}
