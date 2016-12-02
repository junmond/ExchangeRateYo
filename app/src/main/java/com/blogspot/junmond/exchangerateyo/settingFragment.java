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

        final Spinner spnPeriod = (Spinner)getActivity().findViewById(R.id.spnPeriod);
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(getContext(), R.array.period_arrays, R.layout.support_simple_spinner_dropdown_item);
        periodAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnPeriod.setAdapter(periodAdapter);

        spnPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("spnPeriod", "item selected : " + spnPeriod.getSelectedItem().toString());

                // set new interval for alarm receiver
                Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                am.cancel(pendingIntent);

                int interval = 10 * 1000;

                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onActivityCreated(savedInstanceState);
    }
}