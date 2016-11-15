package com.blogspot.junmond.exchangerateyo;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by HyunjunLee on 2016-11-14.
 */

public class ExchangeRateManager {

    private String CurrencyList[] = {"USD", "EUR", "JPY", "CNY", "GBP", "CAD", "CHF", "HKD", "SEK", "AUD", "DKK", "NOK", "SAR", "KWD", "BHD", "AED", "SGD",
                                "MYR", "NZD", "THB", "IDR", "ZAR", "RUB", "VND", "PHP", "MXN", "BRL", "HUF", "PLN", "TRY", "CZK"};

    private String htmlPageUrl =  "http://fx.keb.co.kr/FER1101C.web";
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat;

    public ExchangeRateManager(){

    }

    private boolean isInCurrencyList(String CurrencyName)
    {
        for(String c : CurrencyList)
        {
            if(CurrencyName.contains(c))
                return true;
        }

        return false;
    }

    public void getExchangeRateData(){
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private void getDataViaJSOUP(){
        try {
            Document doc = Jsoup.connect(htmlPageUrl).userAgent("Mozilla").get();
            Elements es = doc.getElementsByClass("tbl_list_type2");
            for (Element e : es) {
                Elements trs = e.getElementsByTag("tr");

                for (Element tr : trs)
                {
                    Elements aTag = tr.getElementsByTag("a");

                    if(!aTag.hasText())
                    {
                        continue;
                    }

                    //Log.d("htmlJun", "tr tag : " + tr.html());

                    String CurrencyName = aTag.text();

                    if(!isInCurrencyList(CurrencyName))
                    {
                        continue;
                    }

                    Elements tds = tr.getElementsByTag("td");

                    // indexes : 1(현찰사실때), 3(현찰파실때), 5(송금보내실때), 6(송금받으실때)
                    String buying = tds.get(1).text();
                    String selling = tds.get(3).text();
                    String sending = tds.get(5).text();
                    String receiving = tds.get(6).text();

                    Log.d("htmlJun", "currency : " + CurrencyName);
                    Log.d("htmlJun", "price : " + buying + ", " + selling + ", " + sending + ", " + receiving);
                    Log.d("htmlJun", "--------------------------");

                    htmlContentInStringFormat += (tr.html().toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getDataViaJSOUP();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //if (htmlContentInStringFormat.length() > 1)
                //Log.d("JSOUPJUN", htmlContentInStringFormat);
        }
    }

}
