package com.blogspot.junmond.exchangerateyo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        super.onActivityCreated(savedInstanceState);
    }
}