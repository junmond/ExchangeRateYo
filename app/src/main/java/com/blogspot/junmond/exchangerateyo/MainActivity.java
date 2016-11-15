package com.blogspot.junmond.exchangerateyo;

import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;


public class MainActivity extends AppCompatActivity {

    ExchangeRateManager moneyManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moneyManager = new ExchangeRateManager();
        moneyManager.getExchangeRateData();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(1234)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


    }


}
