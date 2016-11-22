package com.blogspot.junmond.exchangerateyo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    ExchangeRateManager moneyManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moneyManager = new ExchangeRateManager(this, getApplicationContext());

        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());

        curRateFragment rateFragment = new curRateFragment();
        rateFragment.initMoneyManager(moneyManager);
        alertFragment alFragment = new alertFragment();
        alFragment.initMoneyManager(moneyManager);
        settingFragment setFragment = new settingFragment();
        setFragment.initMoneyManager(moneyManager);

        adapter.addFragment(rateFragment, "환율 조회");
        adapter.addFragment(alFragment, "환율 알림");
        adapter.addFragment(setFragment, "설정");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //openManagerWithTabIndex(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //openManagerWithTabIndex(tab.getPosition());
            }
        });

    }
}
