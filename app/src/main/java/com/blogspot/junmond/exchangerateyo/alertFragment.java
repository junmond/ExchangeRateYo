package com.blogspot.junmond.exchangerateyo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class alertFragment extends Fragment
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

        Log.d("Fragment", "onCreateView2");
        int resId = R.layout.tab2;
        return inflater.inflate(resId, null);
    }


    public void addBtnClicked(View v)
    {
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle(getString(R.string.ALERT_ADD_TITLE));


        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.adddialog, null);

        // work with currency spinner
        final Spinner spnCurrency = (Spinner)dialogView.findViewById(R.id.spnCurrency);
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(getContext(), R.array.currency_arrays_long, R.layout.support_simple_spinner_dropdown_item);
        currencyAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnCurrency.setAdapter(currencyAdapter);
        
        // work with standard spinner
        final Spinner spnStandard = (Spinner)dialogView.findViewById(R.id.spnStandard);
        ArrayAdapter<CharSequence> standardAdapter = ArrayAdapter.createFromResource(getContext(), R.array.standard_arrays, R.layout.support_simple_spinner_dropdown_item);
        standardAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnStandard.setAdapter(standardAdapter);

        b.setView(dialogView);

        b.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String currencyName = spnCurrency.getSelectedItem().toString();
                        String standardName = spnStandard.getSelectedItem().toString();
                        EditText edtPrice = (EditText)dialogView.findViewById(R.id.edtPrice);
                        String alertPrice = edtPrice.getText().toString();

                        moneyManager.addAlert(currencyName, standardName, alertPrice);
                        moneyManager.showAlertList();

                        Log.d("alert", "yes clicked, currency(" + currencyName + "), standard(" + standardName + "), price(" + alertPrice + ")");
                    }
                });

        b.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("alert", "no clicked");
                        dialog.cancel();
                    }
                });

        b.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Button btnAdd = (Button)getActivity().findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addBtnClicked(v);
            }
        });

        Button btnRemoveAll = (Button)getActivity().findViewById(R.id.btnRemoveAll);
        btnRemoveAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
                builder
                        .setTitle( getString(R.string.ALERT_DELETEALL_TITLE))
                        .setMessage( getString(R.string.ALERT_DELETEALL_TEXT))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton( "예", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                moneyManager.deleteAlertList();
                                moneyManager.showAlertList();
                            }
                        })
                        .setNegativeButton( "아니오", null)
                        .show();

                //moneyManager.deleteAlertList();
            }
        });

        adManager.showAd();

        Log.d("Fragment", "onActivityCreated2");
        super.onActivityCreated(savedInstanceState);

        if(moneyManager != null)
        {
            moneyManager.showAlertList();
        }
        else
        {
            Log.d("alertFragment", "moneyManager is null");
        }
    }
}