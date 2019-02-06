package com.csefee.mashael.muslat;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import database.FavDatabaseHelper;
import database.PublicCairoDatabaseHelper;
import database.PublicTransportDataBaseHelper;
import database.SuperJetDatabaseHelper;
import database.TrainDatabaseHelper;

public class Splash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        final Thread superjetApprove = new Thread() {
            @Override
            public void run() {
                try {
                    SuperJetDatabaseHelper mDBHelper = new SuperJetDatabaseHelper(getApplicationContext());

                    //Check exists database
                    /*File database = getApplicationContext().getDatabasePath(SuperJetDatabaseHelper.DBNAME);
                    //if (!database.exists()) {
                    */

                    mDBHelper.getReadableDatabase();
                    //Copy db
                    if (copyDatabase(getApplicationContext(), 1)) {
                        Log.w("Superjet", "DB copied");
                    } else {
                        Toast.makeText(getApplicationContext(), "Copy data error", Toast.LENGTH_SHORT).show();
                    }
                    mDBHelper.closeDatabase();
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        superjetApprove.run();


        final Thread trainApprove = new Thread() {
            @Override
            public void run() {
                try {

                    TrainDatabaseHelper mDBHelper1 = new TrainDatabaseHelper(getApplicationContext());
                    //Check exists database
                    /*File database1 = getApplicationContext().getDatabasePath(TrainDatabaseHelper.DBNAME);
                     *//*if (!database1.exists())

                    {*/
                    mDBHelper1.getReadableDatabase();
                    //Copy db
                    if (copyDatabase(getApplicationContext(), 2)) {
                        Log.w("Train", "DB copied");
                    } else {
                        Toast.makeText(getApplicationContext(), "Copy data error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDBHelper1.closeDatabase();
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        trainApprove.run();

        final Thread publicCairoApprove = new Thread() {
            @Override
            public void run() {
                try {
                    PublicCairoDatabaseHelper mDBHelper2 = new PublicCairoDatabaseHelper(getApplicationContext());

                    //Check exists database
                    /*File database = getApplicationContext().getDatabasePath(SuperJetDatabaseHelper.DBNAME);
                    //if (!database.exists()) {
                    */

                    mDBHelper2.getReadableDatabase();
                    //Copy db
                    if (copyDatabase(getApplicationContext(), 3)) {
                        Log.w("Public Cairo", "DB copied");
                    } else {
                        Toast.makeText(getApplicationContext(), "Copy data error", Toast.LENGTH_SHORT).show();
                    }
                    mDBHelper2.closeDatabase();
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        publicCairoApprove.run();


        final Thread publicTransportApprove = new Thread() {
            @Override
            public void run() {
                try {
                    PublicTransportDataBaseHelper mDBHelper3 = new PublicTransportDataBaseHelper(getApplicationContext());

                    //Check exists database
                    /*File database = getApplicationContext().getDatabasePath(SuperJetDatabaseHelper.DBNAME);
                    //if (!database.exists()) {
                    */

                    mDBHelper3.getReadableDatabase();
                    //Copy db
                    if (copyDatabase(getApplicationContext(), 4)) {
                        Log.w("Public Transport", "DB copied");
                    } else {
                        Toast.makeText(getApplicationContext(), "Copy 222 data error", Toast.LENGTH_SHORT).show();
                    }
                    mDBHelper3.closeDatabase();
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        publicTransportApprove.run();

        final Thread favApprove = new Thread() {
            @Override
            public void run() {
                try {

                    FavDatabaseHelper mDBHelper3 = new FavDatabaseHelper(getApplicationContext());
                    //Check exists database
                    /*File database1 = getApplicationContext().getDatabasePath(TrainDatabaseHelper.DBNAME);
                     *//*if (!database1.exists())

                    {*/
                    SQLiteDatabase db = mDBHelper3.getReadableDatabase();
                    Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{"favourite"});
                    //Copy db
                    if (!(cursor.getCount() > 0)) {
                        if (copyDatabase(getApplicationContext(), 5)) {
                            Log.w("Fav", "DB copied");
                        } else {
                            Toast.makeText(getApplicationContext(), "Copy data error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mDBHelper3.closeDatabase();
                        //}
                    }
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        favApprove.run();


        Thread splash = new Thread() {
            @Override
            public void run() {
                try {
                    publicCairoApprove.join();
                    trainApprove.join();
                    superjetApprove.join();
                    publicTransportApprove.join();
                    favApprove.join();


                    sleep(5000);
                } catch (InterruptedException ignored) {

                } finally {
                    startActivity(intent);
                    finish();
                }

            }
        };

        splash.start();


    }


    private boolean copyDatabase(Context context, int i) {
        try {
            InputStream inputStream = null;
            String outFileName = null;
            if (i == 1) {
                inputStream = context.getAssets().open(SuperJetDatabaseHelper.DBNAME);
                outFileName = SuperJetDatabaseHelper.DBLOCATION + SuperJetDatabaseHelper.DBNAME;

            } else if (i == 2) {
                inputStream = context.getAssets().open(TrainDatabaseHelper.DBNAME);
                outFileName = TrainDatabaseHelper.DBLOCATION + TrainDatabaseHelper.DBNAME;

            } else if (i == 3) {
                inputStream = context.getAssets().open(PublicCairoDatabaseHelper.DBNAME);
                outFileName = PublicCairoDatabaseHelper.DBLOCATION + PublicCairoDatabaseHelper.DBNAME;

            } else if (i == 4) {
                inputStream = context.getAssets().open(PublicTransportDataBaseHelper.DBNAME);
                outFileName = PublicTransportDataBaseHelper.DBLOCATION + PublicTransportDataBaseHelper.DBNAME;

            } else if (i == 5) {
                inputStream = context.getAssets().open(FavDatabaseHelper.DBNAME);
                outFileName = FavDatabaseHelper.DBLOCATION + FavDatabaseHelper.DBNAME;

            }
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity", "DB copied");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
