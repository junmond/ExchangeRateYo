package com.blogspot.junmond.exchangerateyo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
            for(String alertItem : alertList.split("@"))
            {
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
                            notifier.NotifyToUser("money dest reached", alertCurrency + " 환율이 목표 금액에 도달했습니다. 현재 가격 : " + Float.toString(comparePrice));
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

        if(output.length() < 5)
        {
            Log.d("showAlertList", "output is too short, len : " + output.length());
            return;
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

//                    Log.d("htmlJun", "currency : " + CurrencyName);
  //                  Log.d("htmlJun", "price : " + buying + ", " + selling + ", " + sending + ", " + receiving);
    //                Log.d("htmlJun", "--------------------------");

                    moneyLists.add(item);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return moneyLists;
    }

    private void getDataViaJSOUP(){

        final ArrayList<MoneyList.moneyList> moneyLists = getMoneyListViaNet();

        new Thread(new Runnable() {
            @Override
            public void run() {
                parentActivity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        ListView listView = (ListView)parentActivity.findViewById(R.id.lstCustom);
                        listView.setAdapter(new ExchangeRateAdapter(parentContext, moneyLists));
                    }
                });
            }
        }).start();

    }

    private class GetAndShowRateTask extends AsyncTask<Void, Void, Void> {

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
                viewMembers.Buying = (TextView)convertView.findViewById(R.id.txtBuying);
                viewMembers.Selling = (TextView)convertView.findViewById(R.id.txtSelling);
                viewMembers.Sending = (TextView)convertView.findViewById(R.id.txtSending);
                viewMembers.Receiving = (TextView)convertView.findViewById(R.id.txtReceiving);
                convertView.setTag(viewMembers);
            }
            else
            {
                viewMembers = (ViewHandler) convertView.getTag();
            }

            MoneyList.moneyList listMem = this.moneyList.get(position);

            viewMembers.CurrencyName.setText(listMem.currencyName);
            viewMembers.Buying.setText(listMem.buying);
            viewMembers.Selling.setText(listMem.selling);
            viewMembers.Sending.setText(listMem.sending);
            viewMembers.Receiving.setText(listMem.receiving);

            Log.d("Adapter", "set " + listMem.currencyName);

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

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHandler viewMembers;

            if(convertView == null)
            {
                convertView = inflater.inflate(R.layout.alertlist, null);
                viewMembers = new ViewHandler();
                viewMembers.txtCurrencyName = (TextView)convertView.findViewById(R.id.txtAlertCurrencyNameValue);
                viewMembers.txtStandardName = (TextView)convertView.findViewById(R.id.txtAlertStandardNameValue);
                viewMembers.txtPriceValue = (TextView)convertView.findViewById(R.id.txtAlertPriceValue);
                convertView.setTag(viewMembers);
            }
            else
            {
                viewMembers = (ViewHandler) convertView.getTag();
            }

            MoneyList.alertList listMem = this.alertList.get(position);

            viewMembers.txtCurrencyName.setText(listMem.currencyName);
            viewMembers.txtStandardName.setText(listMem.standardName);
            viewMembers.txtPriceValue.setText(listMem.priceValue);

            Log.d("Adapter", "set " + listMem.currencyName);

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
