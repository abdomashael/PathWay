package com.csefee.mashael.muslat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class ContactChangeActivity extends AppCompatActivity {
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_change);

        Intent intent = getIntent();
        TextView companyName = findViewById(R.id.company_name);
        companyName.setText(companyName.getText() + intent.getStringExtra("Company name"));


        final EditText email = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter E-Mail", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (phone.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter Phone Number", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    CheckIntrenetConnection checkIntrenetConnection = new CheckIntrenetConnection(getApplicationContext());
                    if (checkIntrenetConnection.isNetworkConnected()) {
                        //Add data to be send.
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                        nameValuePairs.add(new BasicNameValuePair("company_id", String.valueOf(id)));
                        nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
                        nameValuePairs.add(new BasicNameValuePair("phone", phone.getText().toString()));

                        SendData sendData = new SendData();
                        sendData.sendData(nameValuePairs);

                        Toast toast = Toast.makeText(getApplicationContext(), "Thank you for help us , your suggestion will Reviewed", Toast.LENGTH_SHORT);
                        toast.show();

                        Intent intent = new Intent(ContactChangeActivity.this, MainActivity.class);
                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                        startActivity(intent);


                    }

                }
            }
        });

    }
}
