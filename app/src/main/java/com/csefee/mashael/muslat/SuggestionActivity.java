package com.csefee.mashael.muslat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class SuggestionActivity extends AppCompatActivity {

    String from, to, type;
    ArrayList<String> stations;
    int stationNo = 3, typeIdx;

    boolean fromB = false, toB = false, station1B = false, station2B = false;

    RadioGroup typeSelect;
    EditText fromtxt, totxt, stationtxt1, stationtxt2;
    ArrayList<EditText> stationtxt;

    LinearLayout stationsLayout;
    ImageButton addStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        stationsLayout = findViewById(R.id.stations_layout);
        addStation = findViewById(R.id.add_Station);
        addStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add edittext
                EditText et = new EditText(SuggestionActivity.this);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setLayoutParams(p);
                et.setHint("Station No " + String.valueOf(stationNo));
                et.setTextSize(16);
                et.setTextColor(getResources().getColor(R.color.colorPrimary));
                et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                Log.w("idbe", String.valueOf(stationNo));
                et.setId(stationNo++);
                stationsLayout.addView(et);
                Log.w("id", String.valueOf(stationNo));

            }
        });

        typeSelect = findViewById(R.id.type);
        fromtxt = findViewById(R.id.from_su);
        totxt = findViewById(R.id.to_su);

        stationtxt1 = findViewById(R.id.station1);
        stationtxt2 = findViewById(R.id.station2);
        stations = new ArrayList<>();
        typeSelect.check(R.id.train);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fromtxt.getText().toString().equals("")) {
                    Snackbar.make(view, "OOPs,Enter From location", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    fromB = true;
                }

                if (totxt.getText().toString().equals("")) {
                    Snackbar.make(view, "OOPs,Enter To location", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    toB = true;
                }

                if (stationtxt1.getText().toString().equals("")) {
                    Snackbar.make(view, "OOPs,Enter first station", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    station1B = true;
                }

                if (totxt.getText().toString().equals("")) {
                    Snackbar.make(view, "OOPs,Enter second station", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    station2B = true;
                }

                if (fromB || toB || station1B || station2B) {

                    CheckIntrenetConnection checkIntrenetConnection = new CheckIntrenetConnection(getApplicationContext());
                    if (checkIntrenetConnection.isNetworkConnected()) {

                        typeIdx = typeSelect.getCheckedRadioButtonId();
                        switch (typeIdx) {
                            case 0:
                                type = "Train";
                                break;
                            case 1:
                                type = "Superjet";
                                break;
                            case 2:
                                type = "CairoBus";
                                break;
                            case 3:
                                type = "PublicTransport";
                                break;

                        }

                        from = fromtxt.getText().toString();
                        to = totxt.getText().toString();
                        stations.add(stationtxt1.getText().toString());
                        stations.add(stationtxt2.getText().toString());
                        for (int x = 3; x < stationNo; x++) {
                            EditText et = findViewById(x);
                            if (!et.getText().toString().equals(""))
                                stations.add(et.getText().toString());


                        }

                        //Add data to be send.
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                        nameValuePairs.add(new BasicNameValuePair("from", from));
                        nameValuePairs.add(new BasicNameValuePair("to", to));
                        nameValuePairs.add(new BasicNameValuePair("stations count", String.valueOf(stations.size())));

                        for (int x = 0; x < stations.size(); x++) {
                            nameValuePairs.add(new BasicNameValuePair("station", stations.get(x)));

                        }
                        SendData sendData = new SendData();
                        sendData.sendData(nameValuePairs);

                        Toast toast = Toast.makeText(getApplicationContext(), "Thank you for help us , your suggestion will Reviewed", Toast.LENGTH_SHORT);
                        toast.show();

                        Intent intent = new Intent(SuggestionActivity.this, MainActivity.class);
                        startActivity(intent);


                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Check your Network Connection", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

            }
        });
    }


}


