package com.csefee.mashael.muslat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import database.FavDatabaseHelper;
import database.PublicCairoDatabaseHelper;
import database.PublicTransportDataBaseHelper;
import database.SuperJetDatabaseHelper;
import database.TrainDatabaseHelper;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {


    double first_price, second_price;
    List<LatLng> points = new ArrayList<LatLng>();
    private String type;
    private String startpoint, endpoint, starttime, price, train_t, line_num;
    private int id, endtime, distance, favorite, startpointid, endpointid;
    //schedule holders
    private int train_no[], from_id, next_id, before_id, arrive_id, extra, extra_from, extra_to;
    private String depart_time[], arrive_time[], train_type[], station_name[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        final ListView scheduleListView = (ListView) findViewById(R.id.schedule_listView);
        final ListView priceListView = (ListView) findViewById(R.id.price_listView);

        Intent intent = getIntent();
        id = (int) intent.getIntExtra("id", 0);
        extra = (int) intent.getIntExtra("extra", 0);
        extra_from = (int) intent.getIntExtra("from_id", 0);
        extra_to = (int) intent.getIntExtra("to_id", 0);
        Log.w("extra", String.valueOf(extra));
        Log.w("extra_from", String.valueOf(extra_from));
        Log.w("extra_to", String.valueOf(extra_to));

        type = intent.getStringExtra("type");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LinearLayout click_main_info = findViewById(R.id.click_main_info);
        click_main_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout main_info = findViewById(R.id.main_info);
                if (main_info.getVisibility() == View.GONE) {
                    main_info.setVisibility(View.VISIBLE);
                } else if (main_info.getVisibility() == View.VISIBLE) {
                    main_info.setVisibility(View.GONE);
                }


            }
        });


        try {
            SQLiteOpenHelper databaseHelper;
            SQLiteDatabase db = null;
            Cursor cursor = null;

            switch (type) {
                case "jet": {
                    try {
                        ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
                        ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                                //if true, list will be sorted by date
                                false);


                        databaseHelper = new SuperJetDatabaseHelper(this);

                        db = databaseHelper.getReadableDatabase();
                        cursor = db.rawQuery("SELECT * FROM scedule WHERE _id= ?", new String[]{Integer.toString(id)});
                        cursor.moveToFirst();

                        Cursor c = db.rawQuery("SELECT city_name,lat,long FROM city WHERE _id=?", new String[]{Integer.toString(cursor.getInt(0))});
                        c.moveToFirst();
                        startpoint = c.getString(0);
                        points.add(new LatLng(c.getDouble(1), c.getDouble(2)));
                        c.close();
                        c = db.rawQuery("SELECT city_name,lat,long FROM city WHERE _id=?", new String[]{Integer.toString(cursor.getInt(1))});
                        c.moveToFirst();
                        endpoint = c.getString(0);
                        points.add(new LatLng(c.getDouble(1), c.getDouble(2)));
                        c.close();
                        starttime = cursor.getString(2);
                        distance = cursor.getInt(3);
                        price = String.valueOf(cursor.getInt(4));

                        cursor = db.rawQuery("SELECT depart_time FROM scedule WHERE fromc= ? and toc=?", new String[]{Integer.toString(cursor.getInt(0), cursor.getInt(1))});
                        cursor.moveToFirst();

                        int i = 0;
                        Random rnd = new Random();
                        int color;

                        while (!cursor.isAfterLast()) {
                            TimelineRow myRow = new TimelineRow(0);
                            color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                            // To set the row Title (optional)
                            myRow.setTitle(String.valueOf(cursor.getString(0)));
                            // To set row Below Line Color (optional)
                            myRow.setBellowLineSize(0);
                            // To set background color of the row image (optional)
                            myRow.setBackgroundColor(color);
                            // To set the Background Size of the row image in dp (optional)
                            myRow.setBackgroundSize(20);
                            // To set row Title text color (optional)
                            myRow.setTitleColor(color);

                            timelineRowsList.add(myRow);

                            cursor.moveToNext();

                            ++i;
                        }

                        // Create the Timeline Adapter
                        myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                                //if true, list will be sorted by date
                                false);

                        // Get the ListView and Bind it with the Timeline Adapter
                        scheduleListView.setAdapter(myAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case "train": {
                    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
                    databaseHelper = new TrainDatabaseHelper(this);
                    db = databaseHelper.getReadableDatabase();
                    cursor = db.rawQuery("SELECT * FROM trainline WHERE _id= ?", new String[]{Integer.toString(id)});

                    cursor.moveToFirst();
                    startpointid = cursor.getInt(2);
                    Cursor c = db.rawQuery("SELECT StationName FROM Station WHERE _id=?", new String[]{String.valueOf(cursor.getInt(2))});
                    c.moveToFirst();
                    startpoint = c.getString(0);
                    c.close();

                    c = db.rawQuery("SELECT StationName FROM Station WHERE _id=?", new String[]{String.valueOf(cursor.getInt(3))});
                    c.moveToFirst();
                    endpointid = cursor.getInt(3);
                    endpoint = c.getString(0);
                    c.close();
                    starttime = "0";
                    distance = 0;
                    price = "4/5";

                    /////////////////////////////////////////////////////Directions Data binding/////////////////////////////////////////
                    int i = 0;
                    Random rnd = new Random();
                    int color;
                    c = db.rawQuery("SELECT StationID froM StationOrder WHERE TrainLineID=? ORDER BY ordering ASC ", new String[]{String.valueOf(cursor.getInt(0))});
                    c.moveToFirst();
                    station_name = new String[c.getCount()];
                    while (!c.isAfterLast()) {
                        TimelineRow myRow = new TimelineRow(0);
                        Cursor c1 = db.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?", new String[]{String.valueOf(c.getInt(0))});
                        c1.moveToFirst();

                        if (!String.valueOf(c1.getDouble(1)).equals(""))
                            points.add(new LatLng(c1.getDouble(1), c1.getDouble(2)));

                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // To set the row Title (optional)
                        myRow.setTitle(c1.getString(0));
                        station_name[i] = c1.getString(0);
                        // To set row Below Line Color (optional)
                        myRow.setBellowLineColor(color);
                        // To set row Below Line Size in dp (optional)
                        myRow.setBellowLineSize(6);
                        // To set background color of the row image (optional)
                        myRow.setBackgroundColor(color);
                        // To set the Background Size of the row image in dp (optional)
                        myRow.setBackgroundSize(20);
                        // To set row Title text color (optional)
                        myRow.setTitleColor(color);
                        c1.close();
                        timelineRowsList.add(myRow);


                        //for schedule data
                        switch (i) {
                            case 0:
                                from_id = c.getInt(0);
                                Log.w("from", String.valueOf(from_id));
                                break;
                            case 1:
                                next_id = c.getInt(0);
                                Log.w("next", String.valueOf(next_id));
                                break;

                        }
                        if (i == (c.getCount() - 2)) {
                            before_id = c.getInt(0);
                            Log.w("before", String.valueOf(before_id));

                        }
                        if (i == (c.getCount() - 1)) {
                            arrive_id = c.getInt(0);
                            Log.w("arrive", String.valueOf(arrive_id));
                        }


                        c.moveToNext();
                        ++i;
                    }
                    c.close();
                    // Create the Timeline Adapter
                    ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                            //if true, list will be sorted by date
                            false);

                    // Get the ListView and Bind it with the Timeline Adapter
                    myListView.setAdapter(myAdapter);

                    /////////////////////////////Schedule data binding//////////////////////////////////////

                    timelineRowsList = new ArrayList<>();
                    if (extra == 1) {
                        from_id = extra_from;
                        Cursor from_id_c = db.rawQuery("SELECT ordering from stationorder where TrainLineID =? and StationID =? ", new String[]{String.valueOf(id), String.valueOf(extra_from)});
                        from_id_c.moveToFirst();

                        Cursor next_id_c = db.rawQuery("SELECT StationID from stationorder where TrainLineID =? and ordering=?", new String[]{String.valueOf(id), String.valueOf((from_id_c.getInt(0) + 1))});
                        next_id_c.moveToFirst();
                        next_id = next_id_c.getInt(0);

                        from_id_c.close();
                        next_id_c.close();

                        arrive_id = extra_to;
                        Cursor to_id_c = db.rawQuery("SELECT ordering from stationorder where TrainLineID =? and StationID =? ", new String[]{String.valueOf(id), String.valueOf(extra_to)});
                        to_id_c.moveToFirst();

                        Cursor before_id_c = db.rawQuery("SELECT StationID from stationorder where TrainLineID =? and ordering=?", new String[]{String.valueOf(id), String.valueOf((to_id_c.getInt(0) - 1))});
                        before_id_c.moveToFirst();
                        before_id = before_id_c.getInt(0);

                        to_id_c.close();
                        before_id_c.close();
                    }
                    c = db.rawQuery("SELECT  t1.FK_TrainID ,t1.StartTime,t2.ArriveTime FROM Travel t1 inner join Travel t2 on t1.FK_TrainID=t2.FK_TrainID " +
                                    "where t1.FK_StartStationID=? and t1.FK_ArriveStationID=? " +
                                    "and t2.FK_StartStationID=? and t2.FK_ArriveStationID=? ",
                            new String[]{String.valueOf(from_id), String.valueOf(next_id), String.valueOf(before_id), String.valueOf(arrive_id)});
                    c.moveToFirst();

                   /* Cursor c1 = db.rawQuery("SELECT FK_TrainID,StartTime,ArriveTime FROM Travel " +
                                    "WHERE FK_StartStationID=? AND FK_ArriveStationID=? AND FK_TrainID=? ORDER BY StartTime"
                            , new String[]{String.valueOf(before_id), String.valueOf(arrive_id), String.valueOf(c.getInt(0))});
                    c.moveToFirst();*/
                    c.moveToFirst();
                    Log.w("count", String.valueOf(c.getCount()));
                    train_no = new int[c.getCount()];
                    train_type = new String[c.getCount()];
                    depart_time = new String[c.getCount()];
                    arrive_time = new String[c.getCount()];
                    i = 0;
                    while (!c.isAfterLast()) {
                        Cursor train_no_c = db.rawQuery("SELECT TrainNumber,FK_TrolleyTypeID FROM Train " +
                                "WHERE _id=?   ", new String[]{String.valueOf(c.getInt(0))});
                        train_no_c.moveToFirst();
                        train_no[i] = train_no_c.getInt(0);
                        Cursor train_type_c = db.rawQuery("SELECT type FROM TrolleyType " +
                                "WHERE _id=?   ", new String[]{String.valueOf(train_no_c.getInt(1))});
                        if (train_type_c.moveToFirst())
                            train_type[i] = train_type_c.getString(0);
                        train_type_c.close();

                        try {
                            String originalString = c.getString(1);
                            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(originalString);
                            depart_time[i] = new SimpleDateFormat("HH:mm").format(date);

                            originalString = c.getString(2);
                            date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(originalString);
                            arrive_time[i] = new SimpleDateFormat("HH:mm").format(date);

                        } catch (Exception ignored) {

                        }
                        TimelineRow myRow = new TimelineRow(0);
                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // To set the row Title (optional)
                        myRow.setTitle("Train NO : " + train_no[i] + "\nTrain Type : " + train_type[i]);
                        myRow.setDescription("Depart Clock >> " + depart_time[i] + "\nArrive Clock >> " + arrive_time[i]);
                        // To set row Below Line Color (optional)
                        myRow.setBellowLineSize(0);
                        // To set background color of the row image (optional)
                        myRow.setBackgroundColor(color);
                        // To set the Background Size of the row image in dp (optional)
                        myRow.setBackgroundSize(20);
                        // To set row Title text color (optional)
                        myRow.setTitleColor(color);

                        timelineRowsList.add(myRow);
                        c.moveToNext();
                        ++i;
                    }
                    myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                            //if true, list will be sorted by date
                            false);

                    // Get the ListView and Bind it with the Timeline Adapter
                    scheduleListView.setAdapter(myAdapter);


                    timelineRowsList = new ArrayList<>();

                    Cursor price_cursor = db.rawQuery("SELECT first_price,second_price,train_number FROM price " +
                            "WHERE from_station_id=? AND to_station_id=? ", new String[]{String.valueOf(startpointid), String.valueOf(endpointid)});

                    price_cursor.moveToFirst();
                    while (!price_cursor.isAfterLast()) {
                        Log.w("price", String.valueOf(price_cursor.getCount()));

                        try {
                            first_price = price_cursor.getDouble(0);
                            second_price = price_cursor.getDouble(1);
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TimelineRow myRow = new TimelineRow(0);
                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // To set the row Title (optional)
                        Cursor train_trolly_c = db.rawQuery("SELECT FK_TrolleyTypeID FROM Train " +
                                "WHERE TrainNumber=?   ", new String[]{String.valueOf(price_cursor.getInt(2))});
                        train_trolly_c.moveToFirst();
                        Cursor train_type_c = db.rawQuery("SELECT type FROM TrolleyType " +
                                "WHERE _id=?   ", new String[]{String.valueOf(train_trolly_c.getInt(0))});
                        if (train_type_c.moveToFirst())
                            train_t = train_type_c.getString(0);
                        train_type_c.close();
                        train_trolly_c.close();

                        myRow.setTitle("Train NO : " + price_cursor.getInt(2) + "\nTrain Type : " + train_t);
                        myRow.setDescription("First price   = " + first_price + "E£" + "\nSecond price   = " + second_price + "E£");
                        // To set row Below Line Color (optional)
                        myRow.setBellowLineSize(0);
                        // To set background color of the row image (optional)
                        myRow.setBackgroundColor(color);
                        // To set the Background Size of the row image in dp (optional)
                        myRow.setBackgroundSize(20);
                        // To set row Title text color (optional)
                        myRow.setTitleColor(color);

                        timelineRowsList.add(myRow);
                        ++i;
                        price_cursor.moveToNext();

                    }
                    price_cursor.close();
                    myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                            //if true, list will be sorted by date
                            false);

                    // Get the ListView and Bind it with the Timeline Adapter
                    priceListView.setAdapter(myAdapter);
                    break;
                }

                case "public_cairo": {
                    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
                    databaseHelper = new PublicCairoDatabaseHelper(this);
                    db = databaseHelper.getReadableDatabase();
                    cursor = db.rawQuery("SELECT * FROM line WHERE id= ?", new String[]{Integer.toString(id)});

                    cursor.moveToFirst();
                    line_num = String.valueOf(cursor.getInt(1));
                    Cursor c = db.rawQuery("SELECT name FROM area WHERE _id=?", new String[]{String.valueOf(cursor.getInt(2))});
                    c.moveToFirst();
                    startpoint = c.getString(0);
                    c.close();
                    c = db.rawQuery("SELECT name FROM area WHERE _id=?", new String[]{String.valueOf(cursor.getInt(3))});
                    c.moveToFirst();
                    endpoint = c.getString(0);
                    c.close();
                    starttime = "0";
                    distance = 0;
                    price = String.valueOf(0);

                    /////////////////////////////////////////////////////Directions Data binding/////////////////////////////////////////
                    int i = 0;
                    Random rnd = new Random();
                    int color;
                    // line number
                    color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    TimelineRow myRow = new TimelineRow(0);
                    myRow.setTitle("Line NO : " + line_num);
                    // To set row Below Line Color (optional)
                    myRow.setBellowLineColor(color);
                    // To set row Below Line Size in dp (optional)
                    myRow.setBellowLineSize(0);
                    // To set background color of the row image (optional)
                    myRow.setBackgroundColor(color);
                    // To set the Background Size of the row image in dp (optional)
                    myRow.setBackgroundSize(20);
                    // To set row Title text color (optional)
                    myRow.setTitleColor(color);
                    timelineRowsList.add(myRow);
                    /////////////////////////////////////////////////////

                    c = db.rawQuery("SELECT area_id froM Ordering WHERE lineid=? order by ordering", new String[]{String.valueOf(id)});
                    c.moveToFirst();
                    station_name = new String[c.getCount()];
                    while (!c.isAfterLast()) {
                        myRow = new TimelineRow(0);
                        Cursor c1 = db.rawQuery("SELECT name,lat,long FROM area WHERE _id=?", new String[]{String.valueOf(c.getInt(0))});
                        c1.moveToFirst();
                        if (!String.valueOf(c1.getDouble(1)).equals(""))
                            points.add(new LatLng(c1.getDouble(1), c1.getDouble(2)));

                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // To set the row Title (optional)
                        myRow.setTitle(c1.getString(0));
                        station_name[i] = c1.getString(0);
                        // To set row Below Line Color (optional)
                        myRow.setBellowLineColor(color);
                        // To set row Below Line Size in dp (optional)
                        myRow.setBellowLineSize(6);
                        // To set background color of the row image (optional)
                        myRow.setBackgroundColor(color);
                        // To set the Background Size of the row image in dp (optional)
                        myRow.setBackgroundSize(20);
                        // To set row Title text color (optional)
                        myRow.setTitleColor(color);
                        c1.close();
                        timelineRowsList.add(myRow);

                        c.moveToNext();
                        ++i;
                    }
                    c.close();
                    // Create the Timeline Adapter
                    ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                            //if true, list will be sorted by date
                            false);

                    // Get the ListView and Bind it with the Timeline Adapter
                    myListView.setAdapter(myAdapter);

                    break;
                }
                case "public_transport": {
                    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
                    databaseHelper = new PublicTransportDataBaseHelper(this);
                    db = databaseHelper.getReadableDatabase();
                    cursor = db.rawQuery("SELECT * FROM line WHERE _id= ?", new String[]{Integer.toString(id)});

                    cursor.moveToFirst();
                    Cursor c = db.rawQuery("SELECT stationname FROM station WHERE _id=?", new String[]{String.valueOf(cursor.getInt(1))});
                    c.moveToFirst();
                    startpoint = c.getString(0);
                    c.close();
                    c = db.rawQuery("SELECT stationname FROM station WHERE _id=?", new String[]{String.valueOf(cursor.getInt(2))});
                    c.moveToFirst();
                    endpoint = c.getString(0);
                    c.close();
                    starttime = "0";
                    distance = 0;
                    price = String.valueOf(0);

                    /////////////////////////////////////////////////////Directions Data binding/////////////////////////////////////////
                    int i = 0;
                    Random rnd = new Random();
                    int color;
                    c = db.rawQuery("SELECT stationID froM stationorder WHERE LineID=? order by ordering", new String[]{String.valueOf(cursor.getInt(0))});
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        TimelineRow myRow = new TimelineRow(0);
                        Cursor c1 = db.rawQuery("SELECT stationname FROM station WHERE _id=?", new String[]{String.valueOf(c.getInt(0))});
                        c1.moveToFirst();
                        Log.w("city", c1.getString(0));

                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        // To set the row Title (optional)
                        myRow.setTitle(c1.getString(0));
                        // To set row Below Line Color (optional)
                        myRow.setBellowLineColor(color);
                        // To set row Below Line Size in dp (optional)
                        myRow.setBellowLineSize(6);
                        // To set background color of the row image (optional)
                        myRow.setBackgroundColor(color);
                        // To set the Background Size of the row image in dp (optional)
                        myRow.setBackgroundSize(20);
                        // To set row Title text color (optional)
                        myRow.setTitleColor(color);
                        c1.close();
                        timelineRowsList.add(myRow);

                        c.moveToNext();
                        ++i;
                    }
                    c.close();
                    // Create the Timeline Adapter
                    ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                            //if true, list will be sorted by date
                            false);

                    // Get the ListView and Bind it with the Timeline Adapter
                    myListView.setAdapter(myAdapter);

                    break;
                }
            }


            cursor.close();
            db.close();

            TextView startpointView = (TextView) findViewById(R.id.from_loc);
            startpointView.setText(String.valueOf(startpoint));

            TextView endpointView = (TextView) findViewById(R.id.to_loc);
            endpointView.setText(String.valueOf(endpoint));

            TextView timeView = (TextView) findViewById(R.id.time_nu);
            timeView.setText(String.valueOf(starttime));

            TextView distanceView = (TextView) findViewById(R.id.distance_nu);
            distanceView.setText(String.valueOf(distance));

            TextView priceView = (TextView) findViewById(R.id.money_nu);
            priceView.setText(String.valueOf(price));


        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this, "DataUN", Toast.LENGTH_SHORT);
            toast.show();

        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final LinearLayout ratinglayout = findViewById(R.id.rating_layout);
        final FloatingActionButton fab_like = (FloatingActionButton) findViewById(R.id.fab_pos_rate);
        final FloatingActionButton fab_dislike = (FloatingActionButton) findViewById(R.id.fab_neg_rate);


        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            fab.setVisibility(View.GONE);
                            //ratinglayout.setVisibility(View.GONE);
                        } else {

                            fab.setVisibility(View.VISIBLE);
                            // ratinglayout.setVisibility(View.VISIBLE);
                        }

                    }
                });
        SQLiteOpenHelper databaseHelper = new FavDatabaseHelper(this);
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        final Cursor[] cursor = {db.rawQuery("SELECT _id from favourite where tableid=? AND type=?", new String[]{String.valueOf(id), type})};
        final int[] fav = {0};
        if (cursor[0].getCount() != 0) {
            fab.setImageResource(R.drawable.heartok);
            fav[0] = 1;
            cursor[0].moveToFirst();
        } else {
            fab.setImageResource(R.drawable.heart);
            fav[0] = 0;
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    ContentValues favValue = new ContentValues();

                    if (fav[0] == 0) {
                        fab.setImageResource(R.drawable.heartok);
                        favValue.put("type", type);
                        favValue.put("tableid", id);
                        cursor[0].moveToFirst();
                        if (db.insert("favourite", null, favValue) > 0) ;
                        Log.w("added", "added");

                        fav[0] = 1;
                        Snackbar.make(view, "This Direction added to yours Favourites", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();


                    } else if (fav[0] == 1) {
                        fab.setImageResource(R.drawable.heart);
                        cursor[0] = db.rawQuery("SELECT _id from favourite where tableid=? AND type=?", new String[]{String.valueOf(id), type});
                        cursor[0].moveToFirst();
                        if (db.delete("favourite", "_id=?", new String[]{String.valueOf(cursor[0].getInt(0))}) > 0)
                            Log.w("delete", "delete");

                        fav[0] = 0;
                        Snackbar.make(view, "This Direction removed from yours Favourites", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }


                } catch (SQLiteException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "DataUN", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        });

        fab_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_like.setImageResource(R.drawable.like_ok);

            }
        });

        fab_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_dislike.setImageResource(R.drawable.dislike_ok);

            }
        });

        final ImageView directionselectimage = findViewById(R.id.directions_select);
        LinearLayout directionselect = findViewById(R.id.directions);
        directionselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (myListView.getVisibility()) {
                    case View.VISIBLE: {
                        myListView.setVisibility(View.GONE);
                        directionselectimage.setImageDrawable(getResources().getDrawable(R.drawable.down));
                        break;
                    }
                    case View.GONE: {
                        myListView.setVisibility(View.VISIBLE);
                        directionselectimage.setImageDrawable(getResources().getDrawable(R.drawable.up));
                        break;
                    }

                }
            }
        });

        final ImageView scheduleselectimage = findViewById(R.id.schedules_select);
        LinearLayout schedule_select = findViewById(R.id.schedule_select);
        schedule_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (scheduleListView.getVisibility()) {
                    case View.VISIBLE: {
                        scheduleListView.setVisibility(View.GONE);
                        scheduleselectimage.setImageDrawable(getResources().getDrawable(R.drawable.down));
                        break;
                    }
                    case View.GONE: {
                        scheduleListView.setVisibility(View.VISIBLE);
                        scheduleselectimage.setImageDrawable(getResources().getDrawable(R.drawable.up));
                        break;
                    }

                }

            }
        });

        final ImageView priceselectimage = findViewById(R.id.prices_select);
        LinearLayout price_select = findViewById(R.id.price_select);
        price_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (priceListView.getVisibility()) {
                    case View.VISIBLE: {
                        priceListView.setVisibility(View.GONE);
                        priceselectimage.setImageDrawable(getResources().getDrawable(R.drawable.down));
                        break;
                    }
                    case View.GONE: {
                        priceListView.setVisibility(View.VISIBLE);
                        priceselectimage.setImageDrawable(getResources().getDrawable(R.drawable.up));
                        break;
                    }

                }

            }
        });

        LinearLayout notes_select = findViewById(R.id.notes_layout);
        notes_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(DetailActivity.this, DisqusActivity.class);
                DetailActivity.this.startActivity(intent);
*/
                // launching facebook comments activity
                Intent intent = new Intent(DetailActivity.this, FbCommentsActivity.class);

                // passing the article url
                intent.putExtra("url", "https://www.androidhive.info/2016/06/android-firebase-integrate-analytics/");
                startActivity(intent);
            }
        });

        Button suggestionBtn = (Button) findViewById(R.id.add_suggestion);
        suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, SuggestionActivity.class);
                DetailActivity.this.startActivity(intent);
            }
        });

        Button contactBtn = (Button) findViewById(R.id.contact_us);
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, contactus.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.train);
        BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.bus);
        int i = 0;
        for (i = 0; i < points.size() - 1; i++) {
            switch (type) {
                case "train":
                    googleMap.addMarker(new MarkerOptions().position(points.get(i)).icon(icon1).title(station_name[i]));
                    break;
                case "public_cairo":
                    googleMap.addMarker(new MarkerOptions().position(points.get(i)).icon(icon2).title(station_name[i]));
                    break;
            }
            LatLng src = points.get(i);
            LatLng dest = points.get(i + 1);


            // mMap is the Map Object
            Polyline line = googleMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude)
                    ).width(10).color(getResources().getColor(R.color.colorAccent)).geodesic(true).endCap(new SquareCap()
                    )
            );
        }

        switch (type) {
            case "train":
                googleMap.addMarker(new MarkerOptions().position(points.get(i)).icon(icon1).title(station_name[i]));
                break;
            case "public_cairo":
                googleMap.addMarker(new MarkerOptions().position(points.get(i)).icon(icon2).title(station_name[i]));
                break;
        }


        //camera zoom
        try {

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : points)
                boundsBuilder.include(latLngPoint);

            int routePadding = 100;
            LatLngBounds latLngBounds = boundsBuilder.build();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        } catch (Exception ignored) {

        }
    }


}
