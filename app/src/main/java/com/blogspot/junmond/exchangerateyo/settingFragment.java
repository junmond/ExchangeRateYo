package com.blogspot.junmond.exchangerateyo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.AlarmManager;

public class settingFragment extends Fragment
{
    private TabLayout tabLayout;
    ExchangeRateManager moneyManager = null;

    public void initMoneyManager(ExchangeRateManager manager)
    {
        this.moneyManager = manager;
    }

    public int getIntervalFromString(String intervalOpt)
    {
        int interval = 1000;    // 1s

        if(intervalOpt.equals("30분")){
            interval = 30 * 60 * 1000;
        } else if(intervalOpt.equals("1시간")){
            interval = 1 * 60 * 60 * 1000;
        } else if(intervalOpt.equals("3시간")){
            interval = 3 * 60 * 60 * 1000;
        } else if(intervalOpt.equals("6시간")){
            interval = 6 * 60 * 60 * 1000;
        } else if(intervalOpt.equals("12시간")){
            interval = 12 * 60 * 60 * 1000;
        } else if(intervalOpt.equals("1일")){
            interval = 1 * 24 * 60 * 60 * 1000;
        } else if(intervalOpt.equals("3일")){
            interval = 3 * 24 * 60 * 60 * 1000;
        }
        return interval;
    }

    public int getItemPosFromInterval(int interval)
    {
        switch(interval)
        {
            case (30*60*1000):
                return 0;
            case (1*60*60*1000):
                return 1;
            case (3*60*60*1000):
                return 2;
            case (6*60*60*1000):
                return 3;
            case (12*60*60*1000):
                return 4;
            case (1*24*60*60*1000):
                return 5;
            case (3*24*60*60*1000):
                return 6;
            default:
                return 0;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);

        Log.d("Fragment", "onCreateView3");
        int resId = R.layout.tab3;
        return inflater.inflate(resId, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Log.d("Fragment", "onActivityCreated3");

        adManager.showAd();

        final Spinner spnPeriod = (Spinner)getActivity().findViewById(R.id.spnPeriod);
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(getContext(), R.array.period_arrays, R.layout.support_simple_spinner_dropdown_item);
        periodAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnPeriod.setAdapter(periodAdapter);

        spnPeriod.setSelection(getItemPosFromInterval(SettingManager.getInterval()));
        spnPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("spnPeriod", "item selected : " + spnPeriod.getSelectedItem().toString());

                // set new interval for alarm receiver
                Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);

                int interval = getIntervalFromString(spnPeriod.getSelectedItem().toString());

                Log.d("settingPeriod","interval change to " + interval);
                SettingManager.setInterval(interval);

                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onActivityCreated(savedInstanceState);
    }
}