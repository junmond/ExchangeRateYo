package com.blogspot.junmond.exchangerateyo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private ExchangeRateManager moneyManager = null;

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        if(moneyManager == null)
        {
            moneyManager = new ExchangeRateManager(null, arg0);
        }

        Log.d("AlarmReceiver", "notification!");
        moneyManager.NotifyIfGoalSatisfied();

        // For our recurring task, we'll just display a message
        //Toast.makeText(arg0, "test alarm", Toast.LENGTH_SHORT).show();
    }
}