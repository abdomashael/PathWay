package com.csefee.mashael.muslat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class clientid extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientid);
        final Spinner companyName = findViewById(R.id.company_name_select);
        final EditText clientID = (EditText) findViewById(R.id.cid);
        Button checkUser = findViewById(R.id.check_ID);
        checkUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIntrenetConnection checkIntrenetConnection = new CheckIntrenetConnection(getApplicationContext());
                if (checkIntrenetConnection.isNetworkConnected()) {
                    if (!clientID.getText().equals("")) {
                        String result = null;
                        InputStream is = null;

                        try {
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("http://10.0.2.2/selectall.php");
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity entity = response.getEntity();
                            is = entity.getContent();

                            Log.e("log_tag", "connection success ");
                            //   Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("log_tag", "Error in http connection " + e.toString());
                            Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();

                        }
                        //convert response to string
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                                //  Toast.makeText(getApplicationContext(), "Input Reading pass", Toast.LENGTH_SHORT).show();
                            }
                            is.close();

                            result = sb.toString();
                        } catch (Exception e) {
                            Log.e("log_tag", "Error converting result " + e.toString());
                            Toast.makeText(getApplicationContext(), " Input reading fail", Toast.LENGTH_SHORT).show();

                        }

                        //parse json data
                        try {

                            JSONArray jArray = new JSONArray(result);
                        } catch (Exception e) {

                        }

                        Intent intent = new Intent(clientid.this, ContactChangeActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                        intent.putExtra("Company name", companyName.getSelectedItem().toString());
                        startActivity(intent);
                        Toast toast = Toast.makeText(getApplicationContext(), companyName.getSelectedItem().toString(), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Check your Internet Connection", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }
        });
    }

}
