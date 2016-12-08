package com.blogspot.junmond.exchangerateyo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class curRateFragment extends Fragment
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
        Log.d("Fragment", "onCreateView1");
        int resId = R.layout.tab1;

        return inflater.inflate(resId, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Log.d("Fragment", "onActivityCreated1");

        moneyManager = new ExchangeRateManager(getActivity(), getActivity().getApplicationContext() );
        moneyManager.getExchangeRateData();

        adManager.showAd();

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)getActivity().findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("refreshLayout", "onRefresh called from SwipeRefreshLayout");

                        if(moneyManager != null)
                        {
                            moneyManager.getExchangeRateData();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                }
        );

        super.onActivityCreated(savedInstanceState);
    }
}