package com.csefee.mashael.muslat;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

public class SendData {
    void sendData(ArrayList<NameValuePair> data) {
        // 1) Connect via HTTP. 2) Encode data. 3) Send data.
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new
                    HttpPost("http://www.blah.com/AddAccelerationData.php");
            httppost.setEntity(new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute(httppost);
            Log.i("postData", response.getStatusLine().toString());
            //Could do something better with response.
        } catch (Exception e) {
            Log.e("log_tag", "Error:  " + e.toString());
        }
    }
}
