package com.blogspot.junmond.exchangerateyo;

import android.content.Context;
import android.util.Log;
import android.util.StringBuilderPrinter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by HyunjunLee on 2016-12-05.
 */

public class SettingManager {

    private static String settingFileName = "ExchangeRateYoSettings";
    private static Context parentContext = null;
    private static int alertInterval = 0;
    private static int alertInterval_min = 10 * 1000;
    private static int alertInterval_def = 30 * 60 * 1000;

    public static void initSettingManager(Context ctx)
    {
        parentContext = ctx;
        getSettingValueFromConfig();
    }

    public static int getInterval()
    {
        return alertInterval!=0?alertInterval:alertInterval_def;
    }

    public static void setInterval(int interval)
    {
        if(alertInterval != interval)
        {
            alertInterval = interval;
            writeCurrentSetting();
        }
    }

    public static void writeCurrentSetting()
    {
        if(parentContext == null)
            return;

        try
        {
            OutputStreamWriter osw = new OutputStreamWriter( parentContext.openFileOutput(settingFileName, parentContext.MODE_PRIVATE) );
            osw.write( Integer.toString(alertInterval) + "@" ); // write alert interval
            Log.d("writeSetting", "new interval : " + alertInterval);
            osw.close();
        }
        catch( Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void getSettingValueFromConfig()
    {
        if(parentContext == null)
            return;

        int interval = 0;

        try
        {
            InputStream inputStream =  parentContext.openFileInput(settingFileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();

                int settingIndex = 0;
                for(String str : stringBuilder.toString().split("@"))
                {
                    switch(settingIndex++)
                    {
                        case 0:
                            Log.d("getSetting", "gotten interval : " + str);
                            interval = Integer.parseInt(str);
                            break;
                        default:
                            // invalid setting
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            interval = 0;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            interval = 0;
        }

        if(interval < alertInterval_min)
            interval = alertInterval_def;

        Log.d("readConfig", "read alert interval : " + interval);

        alertInterval = interval;
    }

}
