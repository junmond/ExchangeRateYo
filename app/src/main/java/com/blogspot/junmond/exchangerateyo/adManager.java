package com.blogspot.junmond.exchangerateyo;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by HyunjunLee on 2016-12-06.
 */

public class adManager {

    private static InterstitialAd mInterstitialAd;

    public static void InitAdManager(Context context)
    {
        mInterstitialAd = new InterstitialAd(context);
        //mInterstitialAd.setAdUnitId(context.getString(R.string.junmond_interstitial_ad_unit_id_test));
        mInterstitialAd.setAdUnitId(context.getString(R.string.junmond_interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
    }

    private static void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public static void showAd(){
        if (mInterstitialAd.isLoaded()) {
            Log.d("adsjun", "loaded");
            mInterstitialAd.show();
        }
        else{
            Log.d("adsjun", "not loaded yet...");
        }
    }
}
