package fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.csefee.mashael.muslat.CalculateDistance;
import com.csefee.mashael.muslat.CartviewAdapter;
import com.csefee.mashael.muslat.DetailActivity;
import com.csefee.mashael.muslat.R;
import com.google.android.gms.maps.model.LatLng;

import database.PublicCairoDatabaseHelper;
import database.PublicTransportDataBaseHelper;
import database.SuperJetDatabaseHelper;
import database.TrainDatabaseHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    int type = 0;
    LatLng fromLatLng, toLatLng;
    LatLng[] latLng;
    CalculateDistance calculateDistance;
    private String[] startpoint, endpoint, starttime;
    private int[] id, endtime, distance, price;
    private int jetNo = 1, length, speed = 60, minute, hour, total_distance = 0;


    public HomeFragment() {
        type = getType();
        // Required empty public constructor
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView homeRecycler = RootView.findViewById(R.id.home_recycler);


        //create cursor
        try {

            SQLiteOpenHelper superJetDatabaseHelper = new SuperJetDatabaseHelper(getContext());
            SQLiteDatabase db = superJetDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM scedule", null);

            SQLiteOpenHelper trainDatabaseHelper = new TrainDatabaseHelper(getContext());
            SQLiteDatabase db1 = trainDatabaseHelper.getReadableDatabase();
            Cursor cursor1 = db1.rawQuery("SELECT * FROM trainline", null);

            SQLiteOpenHelper publicCairoDatabaseHelper = new PublicCairoDatabaseHelper(getContext());
            SQLiteDatabase db2 = publicCairoDatabaseHelper.getReadableDatabase();
            Cursor cursor2 = db2.rawQuery("SELECT * FROM line", null);

            SQLiteOpenHelper publicTransportDataBaseHelper = new PublicTransportDataBaseHelper(getContext());
            SQLiteDatabase db3 = publicTransportDataBaseHelper.getReadableDatabase();
            Cursor cursor3 = db3.rawQuery("SELECT * FROM line", null);

            switch (this.getType()) {
                case 0: {
                    startpoint = new String[cursor.getCount()];
                    starttime = new String[cursor.getCount()];
                    endpoint = new String[cursor.getCount()];
                    endtime = new int[cursor.getCount()];
                    distance = new int[cursor.getCount()];
                    price = new int[cursor.getCount()];
                    id = new int[cursor.getCount()];

                    int i = 0;
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            Cursor c = db.rawQuery("SELECT city_name,lat,long FROM city WHERE _id=?", new String[]{Integer.toString(cursor.getInt(0))});
                            c.moveToFirst();
                            startpoint[i] = c.getString(0);
                            fromLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                            c.close();
                            c = db.rawQuery("SELECT city_name,lat,long FROM city WHERE _id=?", new String[]{Integer.toString(cursor.getInt(1))});
                            c.moveToFirst();
                            endpoint[i] = c.getString(0);
                            toLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                            c.close();
                            calculateDistance = new CalculateDistance();
                            distance[i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                            starttime[i] = this.gettime(distance[i], 0);
                            price[i] = cursor.getInt(4);
                            id[i] = cursor.getInt(5);

                            cursor.moveToNext();
                            i++;
                        }
                    }
                    cursor.close();
                    break;

                }
                case 1: {
                    startpoint = new String[cursor1.getCount()];
                    starttime = new String[cursor1.getCount()];
                    endpoint = new String[cursor1.getCount()];
                    endtime = new int[cursor1.getCount()];
                    distance = new int[cursor1.getCount()];
                    price = new int[cursor1.getCount()];
                    id = new int[cursor1.getCount()];

                    int i = 0;
                    if (cursor1.moveToFirst()) {

                        while (!cursor1.isAfterLast()) {

                            Cursor c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(2))});
                            c.moveToFirst();
                            startpoint[i] = c.getString(0);
                            fromLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                            c.close();
                            c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?", new String[]{String.valueOf(cursor1.getInt(3))});
                            c.moveToFirst();
                            endpoint[i] = c.getString(0);
                            toLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                            c.close();

                            price[i] = 0;
                            id[i] = cursor1.getInt(0);

                            Cursor latlong_cursor = db1.rawQuery("SELECT StationID FROM StationOrder WHERE TrainLineID=?", new String[]{String.valueOf(id[i])});
                            latLng = new LatLng[latlong_cursor.getCount()];

                            if (latlong_cursor.moveToFirst()) {
                                int idx = 0;
                                Cursor data_c;
                                while (!latlong_cursor.isAfterLast()) {
                                    data_c = db1.rawQuery("SELECT lat,long FROM Station WHERE _id=?", new String[]{String.valueOf(latlong_cursor.getInt(0))});
                                    data_c.moveToFirst();
                                    latLng[idx] = new LatLng(data_c.getDouble(0), data_c.getDouble(1));
                                    latlong_cursor.moveToNext();
                                    data_c.close();
                                    idx++;
                                    Log.w("Here", "//" + idx);

                                }

                                total_distance = 0;
                                --idx;
                                while (idx > 0) {
                                    calculateDistance = new CalculateDistance();
                                    total_distance = total_distance + (int) calculateDistance.CalculationByDistance(latLng[idx], latLng[idx - 1]);

                                    --idx;
                                }
                                latlong_cursor.close();


                            }
                            distance[i] = total_distance;
                            Cursor max_order_c = db1.rawQuery("SELECT max(ordering) FROM StationOrder WHERE TrainLineID=?", new String[]{String.valueOf(id[i])});
                            max_order_c.moveToFirst();

                            starttime[i] = this.gettime(distance[i], max_order_c.getInt(0) - 2);
                            cursor1.moveToNext();
                            i++;
                        }
                    }

                    cursor1.close();
                    break;
                }
                case 2: {
                    startpoint = new String[cursor2.getCount()];
                    starttime = new String[cursor2.getCount()];
                    endpoint = new String[cursor2.getCount()];
                    endtime = new int[cursor2.getCount()];
                    distance = new int[cursor2.getCount()];
                    price = new int[cursor2.getCount()];
                    id = new int[cursor2.getCount()];

                    int i = 0;
                    if (cursor2.moveToFirst()) {

                        while (!cursor2.isAfterLast()) {

                            Cursor c = db2.rawQuery("SELECT name,lat,long FROM area WHERE _id=?", new String[]{String.valueOf(cursor2.getInt(2))});
                            c.moveToFirst();
                            startpoint[i] = c.getString(0);
                            fromLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                            c.close();
                            c = db2.rawQuery("SELECT name,lat,long FROM area WHERE _id=?", new String[]{String.valueOf(cursor2.getInt(3))});
                            c.moveToFirst();
                            endpoint[i] = c.getString(0);
                            toLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                            c.close();

                            price[i] = 4;
                            id[i] = cursor2.getInt(0);

                            Cursor latlong_cursor = db2.rawQuery("SELECT area_id FROM ordering WHERE lineid=?", new String[]{String.valueOf(id[i])});
                            latLng = new LatLng[latlong_cursor.getCount()];
                            Log.w("Here", String.valueOf(id[i]));

                            if (latlong_cursor.moveToFirst()) {
                                int idx = 0;
                                Cursor data_c;
                                while (!latlong_cursor.isAfterLast()) {
                                    data_c = db2.rawQuery("SELECT lat,long FROM area WHERE _id=?", new String[]{String.valueOf(latlong_cursor.getInt(0))});
                                    data_c.moveToFirst();
                                    latLng[idx] = new LatLng(data_c.getDouble(0), data_c.getDouble(1));
                                    latlong_cursor.moveToNext();
                                    data_c.close();
                                    idx++;
                                    Log.w("Here", "//" + idx);

                                }

                                total_distance = 0;
                                --idx;
                                while (idx > 0) {
                                    calculateDistance = new CalculateDistance();
                                    total_distance = total_distance + (int) calculateDistance.CalculationByDistance(latLng[idx], latLng[idx - 1]);

                                    --idx;
                                }
                                latlong_cursor.close();


                            }
                            distance[i] = total_distance;
                            Cursor max_order_c = db2.rawQuery("SELECT max(ordering) FROM ordering WHERE lineid=?", new String[]{String.valueOf(id[i])});
                            max_order_c.moveToFirst();
                            starttime[i] = this.gettime(distance[i], max_order_c.getInt(0) - 2);
                            cursor2.moveToNext();
                            i++;
                        }
                    }

                    cursor2.close();
                    break;
                }
                case 3: {
                    startpoint = new String[cursor3.getCount()];
                    starttime = new String[cursor3.getCount()];
                    endpoint = new String[cursor3.getCount()];
                    endtime = new int[cursor3.getCount()];
                    distance = new int[cursor3.getCount()];
                    price = new int[cursor3.getCount()];
                    id = new int[cursor3.getCount()];


                    int i = 0;
                    if (cursor3.moveToFirst()) {
                        while (!cursor3.isAfterLast()) {

                            Cursor c = db3.rawQuery("SELECT stationname FROM station WHERE _id=?", new String[]{String.valueOf(cursor3.getInt(1))});
                            c.moveToFirst();
                            startpoint[i] = c.getString(0);
                            c.close();
                            c = db3.rawQuery("SELECT stationname FROM station WHERE _id=?", new String[]{String.valueOf(cursor3.getInt(2))});
                            c.moveToFirst();
                            endpoint[i] = c.getString(0);
                            c.close();


                            distance[i] = 0;
                            starttime[i] = "0";
                            price[i] = 4;
                            id[i] = cursor3.getInt(0);
                            Log.w("public", String.valueOf(id[i]));

                            cursor3.moveToNext();
                            i++;
                        }
                    }

                    cursor3.close();
                    break;
                }

            }
            db.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getContext(), "DataUN", Toast.LENGTH_SHORT);
            toast.show();

        } catch (Exception ignored) {

        }


        CartviewAdapter adapter = new CartviewAdapter(startpoint, starttime, endpoint, endtime, distance, price);
        homeRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new CartviewAdapter.Listener() {
            @Override
            public void onClick(int postion) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", (int) id[postion]);
                Log.w("id", String.valueOf(id[postion]));
                switch (type) {
                    case 0:
                        intent.putExtra("type", "jet");
                        break;
                    case 1:
                        intent.putExtra("type", "train");
                        break;
                    case 2:
                        intent.putExtra("type", "public_cairo");
                        break;
                    case 3:
                        intent.putExtra("type", "public_transport");
                        break;
                }
                getActivity().startActivity(intent);
            }
        });
        return RootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView note_lineText = getActivity().findViewById(R.id.line_note);
        note_lineText.setVisibility(View.GONE);

    }

    String gettime(int distance, int station_count) {
        Log.w("type", String.valueOf(type));
        switch (type) {
            case 1:
                speed = 60;
                distance = distance + (5 * station_count);
                break;
            case 2:
                speed = 50;
                distance = distance + (((5 * 5) / 6) * station_count);
                break;


        }

        hour = distance / speed;
        minute = distance % speed;

        return hour + " H " + minute + " M";
    }
}
