package fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csefee.mashael.muslat.CalculateDistance;
import com.csefee.mashael.muslat.CartviewAdapter;
import com.csefee.mashael.muslat.DetailActivity;
import com.csefee.mashael.muslat.DrawGraph;
import com.csefee.mashael.muslat.R;
import com.google.android.gms.maps.model.LatLng;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;
import java.util.Random;

import database.PublicCairoDatabaseHelper;
import database.PublicTransportDataBaseHelper;
import database.SuperJetDatabaseHelper;
import database.TrainDatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    LatLng fromLatLng, toLatLng;
    int type, speed = 60, minute, hour;
    String from, to;
    TextView note_lineText;
    CalculateDistance calculateDistance;
    //more depth
    int from_msearch;
    int to_msearch, to1_masearch, to_from_d_search, to_to_d_search, from_extra, to_extra;
    boolean msearch = false, to_d_search = false, extra = false;
    View view;
    private String[] startpoint, endpoint, starttime;
    private int[] id, endtime, distance, price;

    public SearchFragment() {
        // Required empty public constructor
    }

    public void data(int type, String from, String to, View view) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.view = view;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView homeRecycler = RootView.findViewById(R.id.home_recycler);
        note_lineText = getActivity().findViewById(R.id.line_note);

        switch (type) {
            case 0:
                //superjet
                try {

                    int startpointid = 0, endpointid = 0, i = 0;
                    SQLiteOpenHelper superJetDatabaseHelper = new SuperJetDatabaseHelper(getContext());
                    SQLiteDatabase db = superJetDatabaseHelper.getReadableDatabase();

                    Cursor c = db.rawQuery("SELECT _id,lat,long FROM city WHERE city_name=?", new String[]{from});
                    if (c.moveToFirst()) {
                        startpointid = c.getInt(0);
                        fromLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                    }
                    Log.w("fr", String.valueOf(c.getInt(0)));
                    c.close();
                    c = db.rawQuery("SELECT _id,lat,long FROM city WHERE city_name=?", new String[]{to});
                    if (c.moveToFirst()) {
                        endpointid = c.getInt(0);
                        toLatLng = new LatLng(c.getDouble(1), c.getDouble(2));
                        Log.w("to", String.valueOf(c.getInt(0)));
                    }
                    c.close();
                    Cursor c_data = db.rawQuery("SELECT depart_time,_id FROM scedule WHERE fromc=? AND  toc=?"
                            , new String[]{String.valueOf(startpointid), String.valueOf(endpointid)});
                    if (c == null) {
                        Toast.makeText(getActivity(), "Checks from ,to locations value", Toast.LENGTH_SHORT).show();
                    }
                    startpoint = new String[c_data.getCount()];
                    starttime = new String[c_data.getCount()];
                    endpoint = new String[c_data.getCount()];
                    endtime = new int[c_data.getCount()];
                    distance = new int[c_data.getCount()];
                    price = new int[c_data.getCount()];
                    id = new int[c_data.getCount()];

                    i = 0;
                    if (c_data.moveToNext()) {
                        while (!c_data.isAfterLast()) {
                            startpoint[i] = from;
                            endpoint[i] = to;
                            id[i] = c_data.getInt(1);

                            calculateDistance = new CalculateDistance();
                            distance[i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                            starttime[i] = this.gettime(distance[i]);
                            price[i] = 0;
                            c_data.moveToNext();
                            ++i;
                        }
                    }

                    c_data.close();
                    c.close();

                } catch (Exception ignored) {

                }
                break;
            case 1:
                //train
                note_lineText.setVisibility(View.GONE);

                SQLiteOpenHelper trainDatabaseHelper = new TrainDatabaseHelper(getContext());
                SQLiteDatabase db1 = trainDatabaseHelper.getReadableDatabase();

                Cursor from_c = db1.rawQuery("SELECT _id FROM Station WHERE StationName=?", new String[]{from});
                Cursor to_c = db1.rawQuery("SELECT _id FROM Station WHERE StationName=?", new String[]{to});
                if (from_c.moveToFirst() && to_c.moveToFirst()) {
                    Cursor c = db1.rawQuery("SELECT _id FROM trainline WHERE FK_FromStationID=? AND FK_ToStationID=?"
                            , new String[]{String.valueOf(from_c.getInt(0)), String.valueOf(to_c.getInt(0))});


                    if (c.getCount() == 0) {
                        c = db1.rawQuery("SELECT from_order.TrainLineID  FROM StationOrder from_order INNER JOIN StationOrder to_order " +
                                        "ON from_order.TrainLineID=to_order.TrainLineID " +
                                        "WHERE from_order.StationID=? AND to_order.StationID=? AND from_order.Ordering < to_order.Ordering"
                                , new String[]{String.valueOf(from_c.getInt(0)), String.valueOf(to_c.getInt(0))});
                        // Toast.makeText(getActivity(), "Checks from ,to locations value", Toast.LENGTH_SHORT).show();
                        if (c.getCount() > 0) {
                            extra = true;
                            Log.w("True", "ttt");
                            from_extra = from_c.getInt(0);
                            to_extra = to_c.getInt(0);
                        }
                    }


                    if (c.getCount() > 0) {

                        startpoint = new String[c.getCount()];
                        starttime = new String[c.getCount()];
                        endpoint = new String[c.getCount()];
                        endtime = new int[c.getCount()];
                        distance = new int[c.getCount()];
                        price = new int[c.getCount()];
                        id = new int[c.getCount()];

                        int i = 0;
                        if (c.moveToNext()) {
                            while (!c.isAfterLast()) {
                                Cursor directions = db1.rawQuery("SELECT FK_FromStationID,FK_ToStationID FROM trainline WHERE _id=?"
                                        , new String[]{String.valueOf(c.getInt(0))});
                                directions.moveToFirst();

                                from_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                        , new String[]{String.valueOf(directions.getInt(0))});
                                to_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                        , new String[]{String.valueOf(directions.getInt(1))});
                                from_c.moveToFirst();
                                to_c.moveToFirst();
                                startpoint[i] = from_c.getString(0);
                                fromLatLng = new LatLng(from_c.getDouble(1), from_c.getDouble(2));

                                endpoint[i] = to_c.getString(0);
                                toLatLng = new LatLng(to_c.getDouble(1), to_c.getDouble(2));

                                id[i] = c.getInt(0);

                                calculateDistance = new CalculateDistance();
                                distance[i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                                starttime[i] = this.gettime(distance[i]);

                                price[i] = 0;
                                c.moveToNext();
                                ++i;
                            }
                        }
                    } else if (c.getCount() == 0) {
                        from_c.moveToFirst();
                        to_c.moveToFirst();

                        int from_search = from_c.getInt(0);
                        int to_search = to_c.getInt(0);


                        DrawGraph drawGraph = new DrawGraph(getActivity(), from_search, to_search, 0);
                        drawGraph.drawGraph();
                        Log.w("final", String.valueOf(drawGraph.getFinal_path()));
                        outer:
                        if (drawGraph.getFinal_path() == null) {

                            Cursor from_chk = db1.rawQuery("SELECT TrainLineID FROM StationOrder WHERE StationID=? order by ordering"
                                    , new String[]{String.valueOf(from_c.getInt(0))});
                            from_chk.moveToFirst();
                            while (!from_chk.isAfterLast()) {
                                Cursor from_cu = db1.rawQuery("SELECT FK_FromStationID FROM TrainLine WHERE _id=?"
                                        , new String[]{String.valueOf(from_chk.getInt(0))});
                                from_cu.moveToFirst();

                                from_search = from_cu.getInt(0);

                                drawGraph = new DrawGraph(getActivity(), from_search, to_search, 0);
                                drawGraph.drawGraph();

                                if (drawGraph.getFinal_path() != null) {
                                    break outer;
                                } else {
                                    from_cu.moveToFirst();

                                    Cursor from_mchk = db1.rawQuery("SELECT TrainLineID FROM StationOrder WHERE StationID=? order by ordering"
                                            , new String[]{String.valueOf(from_cu.getInt(0))});
                                    from_mchk.moveToFirst();
                                    while (!from_mchk.isAfterLast()) {
                                        Cursor from_mcu = db1.rawQuery("SELECT FK_FromStationID FROM TrainLine WHERE _id=?"
                                                , new String[]{String.valueOf(from_mchk.getInt(0))});
                                        from_mcu.moveToFirst();

                                        from_search = from_mcu.getInt(0);
                                        drawGraph = new DrawGraph(getActivity(), from_search, to_search, 0);
                                        drawGraph.drawGraph();

                                        if (drawGraph.getFinal_path() != null) {

                                            from_c = db1.rawQuery("SELECT _id FROM Station WHERE StationName=?", new String[]{from});
                                            from_c.moveToFirst();
                                            from_msearch = from_c.getInt(0);
                                            Log.w("frommm", String.valueOf(from_msearch));
                                            to_msearch = from_cu.getInt(0);
                                            Log.w("tooo", String.valueOf(to_msearch));
                                            to1_masearch = from_search;

                                            msearch = true;

                                            break outer;
                                        }
                                        from_mchk.moveToNext();

                                    }
                                }

                                from_chk.moveToNext();

                            }
                            drawGraph = new DrawGraph(getActivity(), from_search, to_search, 0);
                            drawGraph.drawGraph();
                            Log.w("final2", String.valueOf(drawGraph.getFinal_path()));

                        }

                        outer:
                        if (drawGraph.getFinal_path() == null) {

                            Cursor to_chk = db1.rawQuery("SELECT TrainLineID FROM StationOrder WHERE StationID=? order by ordering"
                                    , new String[]{String.valueOf(to_c.getInt(0))});
                            to_chk.moveToFirst();
                            while (!to_chk.isAfterLast()) {
                                Cursor from_chk = db1.rawQuery("SELECT TrainLineID FROM StationOrder WHERE StationID=? order by ordering"
                                        , new String[]{String.valueOf(from_c.getInt(0))});
                                from_chk.moveToFirst();
                                while (!from_chk.isAfterLast()) {
                                    Cursor from_cu = db1.rawQuery("SELECT FK_FromStationID FROM TrainLine WHERE _id=?"
                                            , new String[]{String.valueOf(from_chk.getInt(0))});
                                    Cursor to_cu = db1.rawQuery("SELECT FK_FromStationID FROM TrainLine WHERE _id=?"
                                            , new String[]{String.valueOf(to_chk.getInt(0))});
                                    from_cu.moveToFirst();
                                    to_cu.moveToFirst();

                                    from_search = from_cu.getInt(0);
                                    int to_m_search = to_cu.getInt(0);

                                    drawGraph = new DrawGraph(getActivity(), from_search, to_m_search, 0);
                                    drawGraph.drawGraph();

                                    if (drawGraph.getFinal_path() != null) {
                                        break outer;
                                    } else {
                                        from_cu.moveToFirst();

                                        Cursor from_mchk = db1.rawQuery("SELECT TrainLineID FROM StationOrder WHERE StationID=? order by ordering"
                                                , new String[]{String.valueOf(from_cu.getInt(0))});
                                        from_mchk.moveToFirst();
                                        while (!from_mchk.isAfterLast()) {
                                            Cursor from_mcu = db1.rawQuery("SELECT FK_FromStationID FROM TrainLine WHERE _id=?"
                                                    , new String[]{String.valueOf(from_mchk.getInt(0))});
                                            from_mcu.moveToFirst();

                                            from_search = from_mcu.getInt(0);
                                            drawGraph = new DrawGraph(getActivity(), from_search, to_m_search, 0);
                                            drawGraph.drawGraph();

                                            if (drawGraph.getFinal_path() != null) {

                                                from_c = db1.rawQuery("SELECT _id FROM Station WHERE StationName=?", new String[]{from});
                                                from_c.moveToFirst();
                                                from_msearch = from_c.getInt(0);
                                                Log.w("frommm12", String.valueOf(from_msearch));
                                                Cursor final_to = db1.rawQuery("SELECT _id FROM Station WHERE StationName=?", new String[]{to});
                                                final_to.moveToFirst();
                                                Log.w("tooo12", String.valueOf(final_to.getInt(0)));
                                                to1_masearch = from_search;

                                                msearch = true;
                                                to_d_search = true;

                                                to_from_d_search = to_cu.getInt(0);
                                                Log.w("to_from_c", String.valueOf(to_from_d_search));
                                                to_chk.moveToFirst();
                                                to_to_d_search = to_chk.getInt(0);
                                                Log.w("to_to_c", String.valueOf(to_to_d_search));

                                                break outer;
                                            }
                                            from_mchk.moveToNext();

                                        }
                                    }

                                    from_chk.moveToNext();

                                }
                                Log.w("final23", String.valueOf(drawGraph.getFinal_path()));
                                to_chk.moveToNext();

                            }
                        }


                        try {

                            Cursor[] cu = new Cursor[drawGraph.getFinal_path().size() - 1];
                            for (int k = 0; k < (drawGraph.getFinal_path().size() - 1); k++)
                                cu[k] = db1.rawQuery("SELECT _id FROM trainline " +
                                                "WHERE FK_FromStationID=? AND FK_ToStationID=?"
                                        , new String[]{String.valueOf(drawGraph.getFinal_path().get(k))
                                                , String.valueOf(drawGraph.getFinal_path().get(k + 1))});

                            if (!msearch) {

                                startpoint = new String[cu.length];
                                starttime = new String[cu.length];
                                endpoint = new String[cu.length];
                                endtime = new int[cu.length];
                                distance = new int[cu.length];
                                price = new int[cu.length];
                                id = new int[cu.length];

                                int i = 0;

                                while (i < cu.length) {
                                    if (cu[i].moveToFirst()) {

                                        Cursor directions = db1.rawQuery("SELECT FK_FromStationID,FK_ToStationID FROM trainline WHERE _id=?"
                                                , new String[]{String.valueOf(cu[i].getInt(0))});
                                        directions.moveToFirst();

                                        from_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                                , new String[]{String.valueOf(directions.getInt(0))});
                                        to_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                                , new String[]{String.valueOf(directions.getInt(1))});
                                        from_c.moveToFirst();
                                        to_c.moveToFirst();
                                        startpoint[i] = from_c.getString(0);
                                        fromLatLng = new LatLng(from_c.getDouble(1), from_c.getDouble(2));

                                        endpoint[i] = to_c.getString(0);
                                        toLatLng = new LatLng(to_c.getDouble(1), to_c.getDouble(2));

                                        id[i] = cu[i].getInt(0);

                                        calculateDistance = new CalculateDistance();
                                        distance[i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                                        starttime[i] = this.gettime(distance[i]);
                                        price[i] = 0;


                                        ++i;
                                    }
                                }
                                note_lineText.setVisibility(View.VISIBLE);
                                Snackbar.make(view, "some time need search from your Station to first station in the result or from last to your Destation ", Snackbar.LENGTH_LONG)
                                        .setDuration(8000).show();

                            } else if (msearch) {

                                Cursor c_msearch = db1.rawQuery("SELECT from_order.TrainLineID  " +
                                                "FROM StationOrder from_order INNER JOIN StationOrder to_order " +
                                                "ON from_order.TrainLineID=to_order.TrainLineID " +
                                                "WHERE from_order.StationID=? AND to_order.StationID=? AND " +
                                                "from_order.Ordering < to_order.Ordering"
                                        , new String[]{String.valueOf(from_msearch), String.valueOf(to_msearch)});

                                if (to_d_search) {
                                    startpoint = new String[cu.length + 2];
                                    starttime = new String[cu.length + 2];
                                    endpoint = new String[cu.length + 2];
                                    endtime = new int[cu.length + 2];
                                    distance = new int[cu.length + 2];
                                    price = new int[cu.length + 2];
                                    id = new int[cu.length + 2];
                                }


                                startpoint = new String[cu.length + 1];
                                starttime = new String[cu.length + 1];
                                endpoint = new String[cu.length + 1];
                                endtime = new int[cu.length + 1];
                                distance = new int[cu.length + 1];
                                price = new int[cu.length + 1];
                                id = new int[cu.length + 1];

                                c_msearch.moveToFirst();
                                if (c_msearch.getCount() == 0)
                                    c_msearch = db1.rawQuery("SELECT from_order.TrainLineID  " +
                                                    "FROM StationOrder from_order INNER JOIN StationOrder to_order " +
                                                    "ON from_order.TrainLineID=to_order.TrainLineID " +
                                                    "WHERE from_order.StationID=? AND to_order.StationID=? AND " +
                                                    "from_order.Ordering < to_order.Ordering"
                                            , new String[]{String.valueOf(from_msearch), String.valueOf(to1_masearch)});

                                c_msearch.moveToFirst();
                                Cursor mDirections = db1.rawQuery("SELECT FK_FromStationID,FK_ToStationID FROM trainline WHERE _id=?"
                                        , new String[]{String.valueOf(c_msearch.getInt(0))});
                                mDirections.moveToFirst();


                                from_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                        , new String[]{String.valueOf(mDirections.getInt(0))});
                                to_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                        , new String[]{String.valueOf(mDirections.getInt(1))});
                                from_c.moveToFirst();
                                to_c.moveToFirst();
                                startpoint[0] = from_c.getString(0);
                                fromLatLng = new LatLng(from_c.getDouble(1), from_c.getDouble(2));

                                endpoint[0] = to_c.getString(0);
                                toLatLng = new LatLng(to_c.getDouble(1), to_c.getDouble(2));

                                id[0] = c_msearch.getInt(0);

                                calculateDistance = new CalculateDistance();
                                distance[0] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                                starttime[0] = this.gettime(distance[0]);

                                price[0] = 0;


                                int i = 0;
                                while (i < cu.length) {
                                    if (cu[i].moveToFirst()) {

                                        Cursor directions = db1.rawQuery("SELECT FK_FromStationID,FK_ToStationID FROM trainline WHERE _id=?"
                                                , new String[]{String.valueOf(cu[i].getInt(0))});
                                        directions.moveToFirst();

                                        from_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                                , new String[]{String.valueOf(directions.getInt(0))});
                                        to_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                                , new String[]{String.valueOf(directions.getInt(1))});
                                        from_c.moveToFirst();
                                        to_c.moveToFirst();
                                        startpoint[1 + i] = from_c.getString(0);
                                        fromLatLng = new LatLng(from_c.getDouble(1), from_c.getDouble(2));

                                        endpoint[1 + i] = to_c.getString(0);
                                        toLatLng = new LatLng(to_c.getDouble(1), to_c.getDouble(2));

                                        id[1 + i] = cu[i].getInt(0);

                                        calculateDistance = new CalculateDistance();
                                        distance[1 + i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                                        starttime[1 + i] = this.gettime(distance[i]);

                                        price[1 + i] = 0;


                                        ++i;
                                    }
                                }

                                if (to_d_search) {
                                    c_msearch = db1.rawQuery("SELECT from_order.TrainLineID  " +
                                                    "FROM StationOrder from_order INNER JOIN StationOrder to_order " +
                                                    "ON from_order.TrainLineID=to_order.TrainLineID " +
                                                    "WHERE from_order.StationID=? AND to_order.StationID=? AND " +
                                                    "from_order.Ordering < to_order.Ordering"
                                            , new String[]{String.valueOf(to_from_d_search), String.valueOf(to_to_d_search)});

                                    c_msearch.moveToFirst();
                                    mDirections = db1.rawQuery("SELECT FK_FromStationID,FK_ToStationID FROM trainline WHERE _id=?"
                                            , new String[]{String.valueOf(c_msearch.getInt(0))});
                                    mDirections.moveToFirst();

                                    from_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                            , new String[]{String.valueOf(mDirections.getInt(0))});
                                    to_c = db1.rawQuery("SELECT StationName,lat,long FROM Station WHERE _id=?"
                                            , new String[]{String.valueOf(mDirections.getInt(1))});
                                    from_c.moveToFirst();
                                    to_c.moveToFirst();
                                    startpoint[i] = from_c.getString(0);
                                    fromLatLng = new LatLng(from_c.getDouble(1), from_c.getDouble(2));

                                    endpoint[i] = to_c.getString(0);
                                    toLatLng = new LatLng(to_c.getDouble(1), to_c.getDouble(2));

                                    id[i] = c_msearch.getInt(0);

                                    calculateDistance = new CalculateDistance();
                                    distance[i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                                    starttime[i] = this.gettime(distance[i]);
                                    price[i] = 0;


                                }


                                note_lineText.setVisibility(View.VISIBLE);
                                Snackbar.make(view, "some time need search from your Station to first station in the result or from last to your Destation ", Snackbar.LENGTH_LONG)
                                        .setDuration(8000).show();

                            }
                        } catch (NullPointerException e) {
                            Toast.makeText(getActivity(), "Some time there problem to get combined way please check the map to find your way or try search with main lines", Toast.LENGTH_LONG).show();
                        }
                    }
                    c.close();
                }
//                from_c.close();
//                to_c.close();

                break;
            case 2:

                note_lineText.setVisibility(View.GONE);

                SQLiteOpenHelper publiccairoDatabaseHelper = new PublicCairoDatabaseHelper(getContext());
                SQLiteDatabase db2 = publiccairoDatabaseHelper.getReadableDatabase();

                from_c = db2.rawQuery("SELECT _id FROM area WHERE Name=?", new String[]{from});
                to_c = db2.rawQuery("SELECT _id FROM area WHERE Name=?", new String[]{to});
                if (from_c.moveToFirst() && to_c.moveToFirst()) {
                    Cursor c = db2.rawQuery("SELECT id FROM line WHERE fromArea=? AND toArea=?"
                            , new String[]{String.valueOf(from_c.getInt(0)), String.valueOf(to_c.getInt(0))});

                    c.moveToFirst();

                    if (c.getCount() == 0) {
                        c = db2.rawQuery("SELECT from_order.lineid  FROM ordering from_order INNER JOIN ordering to_order " +
                                        "ON from_order.lineid=to_order.lineid " +
                                        "WHERE from_order.area_id=? AND to_order.area_id=? AND from_order.Ordering < to_order.Ordering "
                                , new String[]{String.valueOf(from_c.getInt(0)), String.valueOf(to_c.getInt(0))});
                        // Toast.makeText(getActivity(), "Checks from ,to locations value", Toast.LENGTH_SHORT).show();
                        if (c.moveToFirst())
                            Log.w("cairoid", String.valueOf(c.getInt(0)));
                    }


                    if (c.getCount() > 0) {

                        startpoint = new String[c.getCount()];
                        starttime = new String[c.getCount()];
                        endpoint = new String[c.getCount()];
                        endtime = new int[c.getCount()];
                        distance = new int[c.getCount()];
                        price = new int[c.getCount()];
                        id = new int[c.getCount()];

                        int i = 0;
                        if (c.moveToFirst()) {
                            while (!c.isAfterLast()) {
                                Cursor directions = db2.rawQuery("SELECT fromArea,toArea FROM line WHERE id=?"
                                        , new String[]{String.valueOf(c.getInt(0))});
                                directions.moveToFirst();

                                from_c = db2.rawQuery("SELECT name,lat,long FROM area WHERE _id=?"
                                        , new String[]{String.valueOf(directions.getInt(0))});
                                to_c = db2.rawQuery("SELECT name,lat,long FROM area WHERE _id=?"
                                        , new String[]{String.valueOf(directions.getInt(1))});
                                from_c.moveToFirst();
                                to_c.moveToFirst();
                                startpoint[i] = from_c.getString(0);
                                fromLatLng = new LatLng(from_c.getDouble(1), from_c.getDouble(2));

                                endpoint[i] = to_c.getString(0);
                                toLatLng = new LatLng(to_c.getDouble(1), to_c.getDouble(2));

                                id[i] = c.getInt(0);

                                calculateDistance = new CalculateDistance();
                                distance[i] = (int) calculateDistance.CalculationByDistance(fromLatLng, toLatLng);
                                starttime[i] = this.gettime(distance[i]);
                                price[i] = 0;
                                c.moveToNext();
                                ++i;
                            }
                        }
                    } else if (c.getCount() == 0) { // combined case...not on one line
                        from_c.moveToFirst();
                        to_c.moveToFirst();

                        int from_search = from_c.getInt(0);
                        int to_search = to_c.getInt(0);


                        DrawGraph drawGraph = new DrawGraph(getActivity(), from_search, to_search, 1);
                        drawGraph.drawGraph();
                        Log.w("finalC", String.valueOf(drawGraph.getFinal_path()));
                        if (drawGraph.getFinal_path() != null) {
                            //get line values
                            final ListView lineListView = (ListView) RootView.findViewById(R.id.home_listView);
                            lineListView.setVisibility(View.VISIBLE);
                            homeRecycler.setVisibility(View.GONE);


                            ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
                            ArrayList<Integer> id2 = new ArrayList<>();
                            ArrayList<Integer> id_final = new ArrayList<>();

                            TimelineRow myRow = new TimelineRow(0);
                            for (int index = 0; index < drawGraph.getFinal_path().size(); index++) {
                                ArrayList<Integer> id = new ArrayList<>();

                                Random rnd = new Random();
                                int color;
                                myRow = new TimelineRow(0);


                                //Station data

                                StringBuilder lineData = new StringBuilder("");
                                Cursor cstation = db2.rawQuery("Select name from Area where _id=?"
                                        , new String[]{String.valueOf(drawGraph.getFinal_path().get(index))});
                                cstation.moveToFirst();
                                lineData.append(cstation.getString(0)).append(" \nLine NO : ");
                                cstation.close();

                                Cursor clist = db2.rawQuery("Select lineid from ordering where area_id=? AND lineid <21"
                                        , new String[]{String.valueOf(drawGraph.getFinal_path().get(index))});

                                if (index < drawGraph.getFinal_path().size() - 1) {
                                    Cursor clistnext = db2.rawQuery("Select lineid from ordering where area_id=? AND lineid <21"
                                            , new String[]{String.valueOf(drawGraph.getFinal_path().get(index + 1))});
                                    clist.moveToFirst();
                                    while (!clist.isAfterLast()) {

                                        clistnext.moveToFirst();
                                        while (!clistnext.isAfterLast()) {
                                            if (clistnext.getInt(0) == clist.getInt(0))
                                                id.add(clist.getInt(0));

                                            clistnext.moveToNext();
                                        }

                                        clist.moveToNext();
                                    }
                                }
                                boolean clear_ids = true;
                                if (!id2.isEmpty()) {
                                    for (int idx = 0; idx < id.size(); idx++) {
                                        if (clear_ids)
                                            id_final.clear();
                                        for (int idx2 = 0; idx2 < id2.size(); idx2++) {
                                            clear_ids = false;
                                            if (id.get(idx).equals(id2.get(idx2)))
                                                id_final.add(id2.get(idx2));
                                        }
                                    }
                                } else
                                    id_final.addAll(id);

                                if (!id_final.isEmpty()) {

                                    for (int x = 0; x < id_final.size(); x++) {
                                        Cursor clnum = db2.rawQuery("Select num from line where id=?  ", new String[]{String.valueOf(id_final.get(x))});
                                        clnum.moveToFirst();
                                        lineData.append(clnum.getString(0)).append(" / ");

                                    }
                                } else {
                                    for (int x = 0; x < id.size(); x++) {
                                        Cursor clnum = db2.rawQuery("Select num from line where id=?  ", new String[]{String.valueOf(id.get(x))});
                                        clnum.moveToFirst();
                                        lineData.append(clnum.getString(0)).append(" / ");

                                    }
                                }
                                id2.clear();
                                id2.addAll(id_final);
                                color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                                // To set the row Title (optional)
                                myRow.setTitle(String.valueOf(lineData));
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
                                timelineRowsList.add(myRow);


                            }

                            ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(getActivity(), 0, timelineRowsList, false);
                            //if true, list will be sorted by date

                            // Get the ListView and Bind it with the Timeline Adapter
                            ViewGroup.LayoutParams params = lineListView.getLayoutParams();
                            params.height = timelineRowsList.size() * myRow.getBackgroundSize() * myRow.getBackgroundSize();

                            lineListView.setLayoutParams(params);

                            lineListView.setAdapter(myAdapter);

                        }
                    }
                    c.close();
                }
                break;
            case 3:

                note_lineText.setVisibility(View.GONE);

                SQLiteOpenHelper publicTransportDatabaseHelper = new PublicTransportDataBaseHelper(getContext());
                SQLiteDatabase db3 = publicTransportDatabaseHelper.getReadableDatabase();

                from_c = db3.rawQuery("SELECT _id FROM station WHERE stationname=?", new String[]{from});
                to_c = db3.rawQuery("SELECT _id FROM station WHERE stationname=?", new String[]{to});
                if (from_c.moveToFirst() && to_c.moveToFirst()) {
                    Cursor c = db3.rawQuery("SELECT _id FROM line WHERE FK_FromStationID=? AND FK_ToStationID=?"
                            , new String[]{String.valueOf(from_c.getInt(0)), String.valueOf(to_c.getInt(0))});

                    c.moveToFirst();

                    if (c.getCount() == 0) {
                        c = db3.rawQuery("SELECT from_order.lineID  FROM stationorder from_order INNER JOIN stationorder to_order " +
                                        "ON from_order.lineID=to_order.lineID " +
                                        "WHERE from_order.Stationid=? AND to_order.Stationid=? "
                                , new String[]{String.valueOf(from_c.getInt(0)), String.valueOf(to_c.getInt(0))});
                        // Toast.makeText(getActivity(), "Checks from ,to locations value", Toast.LENGTH_SHORT).show();

                    }


                    if (c.getCount() > 0) {

                        startpoint = new String[c.getCount()];
                        starttime = new String[c.getCount()];
                        endpoint = new String[c.getCount()];
                        endtime = new int[c.getCount()];
                        distance = new int[c.getCount()];
                        price = new int[c.getCount()];
                        id = new int[c.getCount()];

                        int i = 0;
                        if (c.moveToFirst()) {
                            while (!c.isAfterLast()) {
                                Cursor directions = db3.rawQuery("SELECT FK_FromStationID,FK_ToStationID FROM line WHERE _id=?"
                                        , new String[]{String.valueOf(c.getInt(0))});
                                directions.moveToFirst();

                                from_c = db3.rawQuery("SELECT Stationname FROM station WHERE _id=?"
                                        , new String[]{String.valueOf(directions.getInt(0))});
                                to_c = db3.rawQuery("SELECT Stationname FROM station WHERE _id=?"
                                        , new String[]{String.valueOf(directions.getInt(1))});
                                from_c.moveToFirst();
                                to_c.moveToFirst();
                                startpoint[i] = from_c.getString(0);
                                endpoint[i] = to_c.getString(0);
                                id[i] = c.getInt(0);
                                starttime[i] = "0";

                                distance[i] = 0;
                                starttime[i] = "0";
                                c.moveToNext();
                                ++i;
                            }
                        }
                    } else if (c.getCount() == 0) { // combined case...not on one line
                        from_c.moveToFirst();
                        to_c.moveToFirst();

                        int from_search = from_c.getInt(0);
                        int to_search = to_c.getInt(0);


                        DrawGraph drawGraph = new DrawGraph(getActivity(), from_search, to_search, 1);
                        drawGraph.drawGraph();
                        Log.w("finalC", String.valueOf(drawGraph.getFinal_path()));
                        if (drawGraph.getFinal_path() != null) {
                            //get line values
                            final ListView lineListView = (ListView) RootView.findViewById(R.id.home_listView);
                            lineListView.setVisibility(View.VISIBLE);
                            homeRecycler.setVisibility(View.GONE);


                            ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
                            ArrayList<Integer> id2 = new ArrayList<>();
                            ArrayList<Integer> id_final = new ArrayList<>();

                            TimelineRow myRow = new TimelineRow(0);
                            for (int index = 0; index < drawGraph.getFinal_path().size(); index++) {
                                ArrayList<Integer> id = new ArrayList<>();

                                Random rnd = new Random();
                                int color;
                                myRow = new TimelineRow(0);


                                //Station data

                                StringBuilder lineData = new StringBuilder("");
                                Cursor cstation = db3.rawQuery("Select StationName from Station where _id=?"
                                        , new String[]{String.valueOf(drawGraph.getFinal_path().get(index))});
                                cstation.moveToFirst();
                                lineData.append(cstation.getString(0)).append(" \nLine : ");
                                cstation.close();

                                Cursor clist = db3.rawQuery("Select LineID from StationOrder where StationID=? AND lineid <21"
                                        , new String[]{String.valueOf(drawGraph.getFinal_path().get(index))});

                                if (index < drawGraph.getFinal_path().size() - 1) {
                                    Cursor clistnext = db3.rawQuery("Select LineID from StationOrder where StationID=? AND lineid <21"
                                            , new String[]{String.valueOf(drawGraph.getFinal_path().get(index + 1))});
                                    clist.moveToFirst();
                                    while (!clist.isAfterLast()) {

                                        clistnext.moveToFirst();
                                        while (!clistnext.isAfterLast()) {
                                            if (clistnext.getInt(0) == clist.getInt(0))
                                                id.add(clist.getInt(0));

                                            clistnext.moveToNext();
                                        }

                                        clist.moveToNext();
                                    }
                                }
                                boolean clear_ids = true;
                                if (!id2.isEmpty()) {
                                    for (int idx = 0; idx < id.size(); idx++) {
                                        if (clear_ids)
                                            id_final.clear();
                                        for (int idx2 = 0; idx2 < id2.size(); idx2++) {
                                            clear_ids = false;
                                            if (id.get(idx).equals(id2.get(idx2)))
                                                id_final.add(id2.get(idx2));
                                        }
                                    }
                                } else
                                    id_final.addAll(id);

                                if (!id_final.isEmpty()) {

                                    for (int x = 0; x < id_final.size(); x++) {
                                        Cursor clnum = db3.rawQuery("Select FK_FromStationID ,FK_ToStationID from line where _id=?  ", new String[]{String.valueOf(id_final.get(x))});
                                        clnum.moveToFirst();
                                        Cursor from_p_c = db3.rawQuery("Select StationName  from station where _id=?  ", new String[]{String.valueOf(clnum.getInt(0))});
                                        Cursor to_p_c = db3.rawQuery("Select StationName from station where _id=?  ", new String[]{String.valueOf(clnum.getInt(1))});
                                        from_p_c.moveToFirst();
                                        to_p_c.moveToFirst();
                                        lineData.append(from_p_c.getString(0)).append(" >> ").append(to_p_c.getString(0));

                                    }
                                } else {
                                    for (int x = 0; x < id.size(); x++) {
                                        Cursor clnum = db3.rawQuery("Select FK_FromStationID ,FK_ToStationID from line where _id=?  ", new String[]{String.valueOf(id.get(x))});
                                        clnum.moveToFirst();
                                        Cursor from_p_c = db3.rawQuery("Select StationName  from station where _id=?  ", new String[]{String.valueOf(clnum.getInt(0))});
                                        Cursor to_p_c = db3.rawQuery("Select StationName from station where _id=?  ", new String[]{String.valueOf(clnum.getInt(1))});
                                        from_p_c.moveToFirst();
                                        to_p_c.moveToFirst();
                                        lineData.append(from_p_c.getString(0)).append(" >> ").append(to_p_c.getString(0));

                                    }
                                }
                                id2.clear();
                                id2.addAll(id_final);
                                color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                                // To set the row Title (optional)
                                myRow.setTitle(String.valueOf(lineData));
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
                                timelineRowsList.add(myRow);


                            }

                            ArrayAdapter<TimelineRow> myAdapter = new TimelineViewAdapter(getActivity(), 0, timelineRowsList, false);
                            //if true, list will be sorted by date

                            // Get the ListView and Bind it with the Timeline Adapter
                            ViewGroup.LayoutParams params = lineListView.getLayoutParams();
                            params.height = timelineRowsList.size() * myRow.getBackgroundSize() * myRow.getBackgroundSize();

                            lineListView.setLayoutParams(params);

                            lineListView.setAdapter(myAdapter);

                        }
                    }
                    c.close();
                }
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
                if (extra) {
                    intent.putExtra("extra", 1);
                    intent.putExtra("from_id", from_extra);
                    intent.putExtra("to_id", to_extra);
                } else {
                    intent.putExtra("extra", "0");

                }
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

    String gettime(int distance) {

        hour = distance / speed;
        minute = distance % speed;

        return hour + " H " + minute + " M";
    }

}
