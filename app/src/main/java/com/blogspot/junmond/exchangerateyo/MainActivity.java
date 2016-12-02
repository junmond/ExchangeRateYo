package com.blogspot.junmond.exchangerateyo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ExchangeRateManager moneyManager = null;

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public void startAlarm() {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //int interval = 3600 * 1000;
        int interval = 10 * 1000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.hide();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00C4CD")));

        moneyManager = new ExchangeRateManager(this, getApplicationContext());

        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());

        curRateFragment rateFragment = new curRateFragment();
        rateFragment.initMoneyManager(moneyManager);
        alertFragment alFragment = new alertFragment();
        alFragment.initMoneyManager(moneyManager);
        settingFragment setFragment = new settingFragment();
        setFragment.initMoneyManager(moneyManager);

        adapter.addFragment(rateFragment, getString(R.string.TAB_NAME_GET_CURRENCY_RATE));
        adapter.addFragment(alFragment, getString(R.string.TAB_NAME_CURRENCY_RATE_ALERT));
        adapter.addFragment(setFragment, getString(R.string.TAB_NAME_SETTINGS));
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //openManagerWithTabIndex(tab.getPosition());
            }
        });


        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        startAlarm();

    }
}
