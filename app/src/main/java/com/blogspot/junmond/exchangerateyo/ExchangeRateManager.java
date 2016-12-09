package com.blogspot.junmond.exchangerateyo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/**
 * Created by HyunjunLee on 2016-11-14.
 */

public class ExchangeRateManager {

    public static String CurrencyList[] = {"USD", "EUR", "JPY", "CNY", "GBP", "CAD", "CHF", "HKD", "SEK", "AUD", "DKK", "NOK", "SAR", "KWD", "BHD", "AED", "SGD",
                                "MYR", "NZD", "THB", "IDR", "ZAR", "RUB", "VND", "PHP", "MXN", "BRL", "HUF", "PLN", "TRY", "CZK"};

    private ArrayList<MoneyList.moneyList> lists;
    private Notifier notifier = null;

    private String htmlPageUrl =  "http://fx.keb.co.kr/FER1101C.web";
    private String alertFileName = "alertList";
    private String alertString = "";
    private Context parentContext = null;
    private Activity parentActivity = null;

    public ExchangeRateManager(Activity activity, Context context){
        this.parentActivity = activity;
        this.parentContext = context;
        notifier = new Notifier(context);
    }

    public void NotifyToUser(String title, String text)
    {
        if(notifier != null)
            notifier.NotifyToUser(title, text);
    }

    public void NotifyIfGoalSatisfied()
    {
        NotifyIfGoalTask notifyIfGoalTask = new NotifyIfGoalTask();
        notifyIfGoalTask.execute();
    }

    private class NotifyIfGoalTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // get data from web
            ArrayList<MoneyList.moneyList> moneyList = getMoneyListViaNet();

            // read alert list
            String alertList = readAlertListFile();
            if(alertList == null)
            {
                alertList = "";
            }
            for(String alertItem : alertList.split("@"))
            {
                if(alertItem.length() < 5)
                {
                    continue;
                }
                String alertCurrency = alertItem.split("\t")[0];
                String alertStandard = alertItem.split("\t")[1];
                String alertPrice = alertItem.split("\t")[2];

                Log.d("NotifyIfGoalTask", "alertItem : currency(" + alertCurrency + "), standard(" + alertStandard + "), price(" + alertPrice + ")");

                for(MoneyList.moneyList moneyData : moneyList)
                {
                    if(moneyData.currencyName.contains(alertCurrency))
                    {
                        float comparePrice = 0;
                        float fAlertPrice = Float.parseFloat(alertPrice);
                        boolean bWantBigger = false;

                        Log.d("NotifyIfGoalTask", "found currency(" + moneyData.currencyName + "), buying(" + moneyData.buying + "), selling(" + moneyData.selling
                                + "(, sending(" + moneyData.sending + "), receiving(" + moneyData.receiving + ")");

                        // compare if goal satisfied.
                        if(alertStandard.equals("현찰 살 때"))
                        {
                            comparePrice = Float.parseFloat(moneyData.buying);
                            bWantBigger = false;
                        }
                        else if(alertStandard.equals("현찰 팔 때"))
                        {
                            comparePrice = Float.parseFloat(moneyData.selling);
                            bWantBigger = true;
                        }
                        else if(alertStandard.equals("송금 보낼 때"))
                        {
                            comparePrice = Float.parseFloat(moneyData.sending);
                            bWantBigger = false;
                        }
                        else if(alertStandard.equals("송금 받을 때"))
                        {
                            comparePrice = Float.parseFloat(moneyData.receiving);
                            bWantBigger = true;
                        }

                        boolean bAlert = false;

                        bAlert = bWantBigger ? (fAlertPrice<comparePrice) : (fAlertPrice>comparePrice);

                        if(bAlert)
                        {
                            // make notification.
                            Log.d("destReached", "send notification");

                            notifier.NotifyToUser(parentContext.getString(R.string.NOTIFICATION_TITLE),
                                    alertCurrency + parentContext.getString(R.string.NOTIFICATION_TEXT) + Float.toString(comparePrice));
                        }
                        else
                        {
                            Log.d("destReached", "no notification, destPrice(" + Float.toString(fAlertPrice) + "), compare(" + Float.toString(comparePrice) + "), wantBigger:" + bWantBigger);
                        }
                    }
                }


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    public void writeCurrentAlertList()
    {
        if(alertString.length() < 5)
        {
            Log.d("writeCurrentAlertList", "invalid alertString length : " + alertString.length());
        }
        String writeString = alertString;

        try
        {
            OutputStreamWriter osw = new OutputStreamWriter(parentContext.openFileOutput(alertFileName, parentContext.MODE_PRIVATE));
            // write buffer
            osw.write(writeString);
            Log.d("writeCurrentAlertList", "string written(" + writeString + ")");
            osw.close();
            alertString = writeString;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addAlert(String currencyName, String standardName, String price)
    {
        // save to file
        String addString = "";
        addString += (currencyName + "\t");
        addString += (standardName + "\t");
        addString += (price + "@");

        String writeString = "";

        if(alertString == null)
        {
            writeString = addString;
        }
        else
        {
            writeString = alertString + addString;
        }

        try
        {
            OutputStreamWriter osw = new OutputStreamWriter(parentContext.openFileOutput(alertFileName, parentContext.MODE_PRIVATE));
            // write buffer
            osw.write(writeString);
            Log.d("addAlert", "string added(" + writeString + ")");
            osw.close();
            alertString = writeString;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private String readAlertListFile()
    {
        String output = null;

        try
        {
            InputStream inputStream = parentContext.openFileInput(alertFileName);

            if( inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();

                output = stringBuilder.toString();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return output;
    }

    public void showAlertList()
    {
        String output = null;

        output = readAlertListFile();

        if(output == null || output.length() < 5)
        {
            Log.d("showAlertList", "output is too short, len : " + Integer.toString(output!=null?output.length():0));
            output = "";
        }
        alertString = output;
        //alertString.split("\n");

        if(alertString == null)
        {
            return;
        }

        final ArrayList<MoneyList.alertList> alertLists = new ArrayList<MoneyList.alertList>();

        for(String str : alertString.split("@"))
        {
            if(str.length()< 5)
            {
                continue;
            }

            MoneyList.alertList item = new MoneyList.alertList();

            String currencyName = str.split("\t")[0];
            String standardName = str.split("\t")[1];
            String priceValue = str.split("\t")[2];

            item.currencyName = currencyName;
            item.standardName = standardName;
            item.priceValue = priceValue;

            alertLists.add(item);

            Log.d("ReadAlert", "currency : " + currencyName + ", standard : " + standardName + ", price : " + priceValue);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                parentActivity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ListView listView = (ListView)parentActivity.findViewById(R.id.lstAlert);
                        listView.setAdapter(new AlertListAdapter(parentContext, alertLists));
                    }
                });
            }
        }).start();
    }

    public void deleteAlertList()
    {
          Log.d("deleteAlertList", "delete : " + alertFileName);
          parentContext.deleteFile(alertFileName);
          alertString = null;
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
        Log.d("getRate", "getExchangeRateData called");
        GetAndShowRateTask getAndShowRateTask = new GetAndShowRateTask();
        getAndShowRateTask.execute();
    }

    public void ShowMoney()
    {
        ArrayList<MoneyList.moneyList> moneyLists = lists;
    }


    private ArrayList<MoneyList.moneyList> getMoneyListViaNet()
    {
        final ArrayList<MoneyList.moneyList> moneyLists = new ArrayList<MoneyList.moneyList>();

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

                    MoneyList.moneyList item = new MoneyList.moneyList();

                    item.currencyName = CurrencyName;
                    item.buying = buying;
                    item.selling = selling;
                    item.sending = sending;
                    item.receiving = receiving;
                    moneyLists.add(item);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("getDataVia", "moneyList len : " + moneyLists.size());

        return moneyLists;
    }

    private boolean getDataViaJSOUP(){

        ArrayList<MoneyList.moneyList> moneyLists = getMoneyListViaNet();
        Log.d("getDataVia", "got money list size : " + moneyLists.size());

        int moneylistSize = moneyLists.size();
        boolean failed = false;

        if(moneyLists == null ||
            moneyLists.size() == 0)
        {
            int retLimit = 5, retCount = 0;
            while(moneyLists == null || moneyLists.size() == 0)
            {
                if(retCount++ > retLimit)
                {
                    failed = true;
                    break;
                }
                moneyLists = getMoneyListViaNet();
                Log.d("getDataVia", "retried got money list size : " + moneyLists.size());
            }
        }

        if(failed == true)
        {
            Log.d("getDataVia","failed!!!");
            return false;
        }

        final ArrayList<MoneyList.moneyList> lists = moneyLists;

        new Thread(new Runnable() {
            @Override
            public void run() {
                parentActivity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ListView listView = (ListView)parentActivity.findViewById(R.id.lstCustom);
                        listView.setAdapter(new ExchangeRateAdapter(parentContext, lists));
                    }
                });
            }
        }).start();

        return true;
    }

    private class GetAndShowRateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            Log.d("GetAndShowRate", "preExecute");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return getDataViaJSOUP();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result == false)
            {
                Log.d("post", "called");
                Toast.makeText(parentContext, parentContext.getString(R.string.TOAST_FAIL_GET_DATA), Toast.LENGTH_LONG).show();
            }

        }
    }

    public class ExchangeRateAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MoneyList.moneyList> moneyList;

        public ExchangeRateAdapter(Context context, ArrayList<MoneyList.moneyList> eventTitleList){
            this.inflater = LayoutInflater.from(context);
            this.moneyList = eventTitleList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHandler viewMembers;

            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.moneylist, null);
                viewMembers = new ViewHandler();
                viewMembers.CurrencyName = (TextView)convertView.findViewById(R.id.txtCurrencyName);
                viewMembers.Buying = (TextView)convertView.findViewById(R.id.txtBuyingValue);
                viewMembers.Selling = (TextView)convertView.findViewById(R.id.txtSellingValue);
                viewMembers.Sending = (TextView)convertView.findViewById(R.id.txtSendingValue);
                viewMembers.Receiving = (TextView)convertView.findViewById(R.id.txtReceivingValue);
                convertView.setTag(viewMembers);
            }
            else
            {
                viewMembers = (ViewHandler) convertView.getTag();
            }

            MoneyList.moneyList listMem = this.moneyList.get(position);

            viewMembers.CurrencyName.setText(listMem.currencyName);
            viewMembers.Buying.setText(listMem.buying + " " + parentContext.getString(R.string.UNIT_MONEY_KRW));
            viewMembers.Selling.setText(listMem.selling + " " + parentContext.getString(R.string.UNIT_MONEY_KRW));
            viewMembers.Sending.setText(listMem.sending + " " + parentContext.getString(R.string.UNIT_MONEY_KRW));
            viewMembers.Receiving.setText(listMem.receiving + " " + parentContext.getString(R.string.UNIT_MONEY_KRW));

            if(listMem.currencyName.equals("ads"))
            {
                Log.d("AdapterCurRate", "found ads! at the position of " + position);
                RelativeLayout relayoutMoney = (RelativeLayout)convertView.findViewById(R.id.rLayoutMoneyList);
                relayoutMoney.removeAllViews();

                RelativeLayout relayoutAds = (RelativeLayout)convertView.findViewById(R.id.rLayoutAds);
                AdView mAdView = (AdView) relayoutAds.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                if(mAdView != null && adRequest != null)
                    mAdView.loadAd(adRequest);
                else
                {
                    Log.d("AdapterAds", "mAdView : " + mAdView + "adRequeset : " + adRequest);
                }
            }
            else
            {
                RelativeLayout relayoutAds = (RelativeLayout)convertView.findViewById(R.id.rLayoutAds);
                relayoutAds.removeAllViews();
            }

            Log.d("AdapterCurRate", "set " + listMem.currencyName);

            return convertView;
        }
        class ViewHandler{
            TextView CurrencyName;
            TextView Buying;
            TextView Selling;
            TextView Sending;
            TextView Receiving;
        }

        public final int getCount() {
            return moneyList.size();
        }

        public final Object getItem(int position) {
            return moneyList.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }

        public final MoneyList.moneyList getItemAtPosition(int position)
        {
            return moneyList.get(position);
        }
    }

    public class AlertListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<MoneyList.alertList> alertList;

        public AlertListAdapter(Context context, ArrayList<MoneyList.alertList> eventTitleList){
            this.inflater = LayoutInflater.from(context);
            this.alertList = eventTitleList;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHandler viewMembers;
            final ViewGroup viewParent = parent;

            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.alertlist, null);
                viewMembers = new ViewHandler();
                viewMembers.txtCurrencyName = (TextView)convertView.findViewById(R.id.txtAlertCurrencyNameValue);
                viewMembers.txtStandardName = (TextView)convertView.findViewById(R.id.txtAlertStandardNameValue);
                viewMembers.txtPriceValue = (TextView)convertView.findViewById(R.id.txtAlertPriceValue);
                Button btnRemove = (Button)convertView.findViewById(R.id.btnRemove);
                btnRemove.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( viewParent.getContext() , android.R.style.Theme_Material_Light_Dialog_Alert);
                        builder
                                .setTitle( viewParent.getContext().getString(R.string.ALERT_DELETE_TITLE))
                                .setMessage( viewParent.getContext().getString(R.string.ALERT_DELETE_CURRENCY) + viewMembers.txtCurrencyName.getText() + "\n" +
                                            viewParent.getContext().getString(R.string.ALERT_DELETE_STANDARD) + viewMembers.txtStandardName.getText() + "\n" +
                                            viewParent.getContext().getString(R.string.ALERT_DELETE_PRICE) + viewMembers.txtPriceValue.getText() + "\n\n" +
                                            viewParent.getContext().getString(R.string.ALERT_DELETE_TEXT))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton( "예", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        Log.d("BtnRemove", "before del : " + alertString);
                                        alertString = alertString.replace(alertString.split("@")[position] + "@", "");
                                        Log.d("BtnRemove", "after del : " + alertString);
                                        writeCurrentAlertList();
                                        showAlertList();
                                    }
                                })
                                .setNegativeButton( "아니오", null)
                                .show();


                    }
                });
                convertView.setTag(viewMembers);
            }
            else
            {
                viewMembers = (ViewHandler) convertView.getTag();
            }

            MoneyList.alertList listMem = this.alertList.get(position);

            viewMembers.txtCurrencyName.setText(listMem.currencyName);
            viewMembers.txtStandardName.setText(listMem.standardName);
            viewMembers.txtPriceValue.setText(listMem.priceValue + " KRW");

            Log.d("AdapterAlert", "set " + listMem.currencyName);

            return convertView;
        }
        class ViewHandler{
            TextView txtCurrencyName;
            TextView txtStandardName;
            TextView txtPriceValue;
        }

        public final int getCount() {
            return alertList.size();
        }

        public final Object getItem(int position) {
            return alertList.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }

        public final MoneyList.alertList getItemAtPosition(int position)
        {
            return alertList.get(position);
        }

    }

}
