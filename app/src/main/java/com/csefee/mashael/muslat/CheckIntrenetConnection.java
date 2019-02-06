package com.csefee.mashael.muslat;

import android.content.Context;
import android.net.ConnectivityManager;

class CheckIntrenetConnection {

    private Context mContext;

    CheckIntrenetConnection(Context mContext) {
        this.mContext = mContext;
    }

    boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


}
